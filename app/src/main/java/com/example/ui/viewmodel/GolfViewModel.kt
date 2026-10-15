package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Course
import com.example.data.model.DotCatalog
import com.example.data.model.GolfGameDefinition
import com.example.data.model.GolfGamesCatalog
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Player
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.data.model.Tee
import com.example.data.repository.GolfRepository
import com.example.engine.GolfGameEngine
import com.example.engine.HandicapCalculator
import com.example.engine.PlayerDotStanding
import com.example.engine.SixesHoleResult
import com.example.engine.SkinsHoleResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GolfTab(val title: String) {
    GAMES("Games Hub"),
    SCORECARD("Scorecard"),
    COURSES("Courses"),
    PLAYERS("Golfers"),
    ANALYTICS("Analytics")
}

class GolfViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GolfRepository.create(application)

    private val _currentTab = MutableStateFlow(GolfTab.GAMES)
    val currentTab: StateFlow<GolfTab> = _currentTab.asStateFlow()

    // Selected game for rule inspection dialog/drawer
    private val _selectedGameForInfo = MutableStateFlow<GolfGameDefinition?>(null)
    val selectedGameForInfo: StateFlow<GolfGameDefinition?> = _selectedGameForInfo.asStateFlow()

    // Database Flows
    val players: StateFlow<List<Player>> = repository.getAllPlayersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<Course>> = repository.getAllCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roundsHistory: StateFlow<List<Round>> = repository.getAllRoundsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Round State
    private val _activeRound = MutableStateFlow<Round?>(null)
    val activeRound: StateFlow<Round?> = _activeRound.asStateFlow()

    private val _activeRoundPlayers = MutableStateFlow<List<RoundPlayer>>(emptyList())
    val activeRoundPlayers: StateFlow<List<RoundPlayer>> = _activeRoundPlayers.asStateFlow()

    private val _activeHoles = MutableStateFlow<List<Hole>>(emptyList())
    val activeHoles: StateFlow<List<Hole>> = _activeHoles.asStateFlow()

    private val _activeHoleScores = MutableStateFlow<List<HoleScore>>(emptyList())
    val activeHoleScores: StateFlow<List<HoleScore>> = _activeHoleScores.asStateFlow()

    private val _currentHoleNumber = MutableStateFlow(1)
    val currentHoleNumber: StateFlow<Int> = _currentHoleNumber.asStateFlow()

    // UI Dialog Toggles
    private val _showNewRoundDialog = MutableStateFlow(false)
    val showNewRoundDialog: StateFlow<Boolean> = _showNewRoundDialog.asStateFlow()

    private val _showFullScorecardDialog = MutableStateFlow(false)
    val showFullScorecardDialog: StateFlow<Boolean> = _showFullScorecardDialog.asStateFlow()

    private val _showLeaderboardDialog = MutableStateFlow(false)
    val showLeaderboardDialog: StateFlow<Boolean> = _showLeaderboardDialog.asStateFlow()

    private val _isSearchingApi = MutableStateFlow(false)
    val isSearchingApi: StateFlow<Boolean> = _isSearchingApi.asStateFlow()

    // Pre-selected game to pass into New Round modal
    private val _preselectedGameId = MutableStateFlow("SIXES")
    val preselectedGameId: StateFlow<String> = _preselectedGameId.asStateFlow()

    init {
        // Observe and load the most recent round if available
        viewModelScope.launch {
            repository.getAllRoundsFlow().collect { rounds ->
                if (_activeRound.value == null && rounds.isNotEmpty()) {
                    val inProgress = rounds.find { it.status == "IN_PROGRESS" } ?: rounds.first()
                    loadRound(inProgress.id)
                }
            }
        }
    }

    fun setTab(tab: GolfTab) {
        _currentTab.value = tab
    }

    fun selectGameForInfo(game: GolfGameDefinition?) {
        _selectedGameForInfo.value = game
    }

    fun openNewRoundWithGame(gameId: String) {
        _preselectedGameId.value = gameId
        _showNewRoundDialog.value = true
    }

    fun showNewRoundDialog(show: Boolean) {
        _showNewRoundDialog.value = show
    }

    fun showFullScorecardDialog(show: Boolean) {
        _showFullScorecardDialog.value = show
    }

    fun showLeaderboardDialog(show: Boolean) {
        _showLeaderboardDialog.value = show
    }

    fun setCurrentHole(hole: Int) {
        if (hole in 1..18) {
            _currentHoleNumber.value = hole
        }
    }

    fun nextHole() {
        if (_currentHoleNumber.value < 18) {
            _currentHoleNumber.value += 1
        }
    }

    fun previousHole() {
        if (_currentHoleNumber.value > 1) {
            _currentHoleNumber.value -= 1
        }
    }

    fun loadRound(roundId: String) {
        viewModelScope.launch {
            val r = repository.getRoundById(roundId) ?: return@launch
            _activeRound.value = r

            val rPlayers = repository.getRoundPlayersSync(roundId)
            _activeRoundPlayers.value = rPlayers

            val holes = repository.getHolesForTee(r.teeId)
            _activeHoles.value = holes

            repository.getHoleScoresForRoundFlow(roundId).collect { scores ->
                _activeHoleScores.value = scores
            }
        }
    }

    fun startNewRound(
        course: Course,
        tee: Tee,
        gameType: String,
        selectedPlayers: List<Player>,
        isHandicapEnabled: Boolean,
        dotsEnabled: Boolean,
        betPerUnit: Double
    ) {
        viewModelScope.launch {
            val round = repository.createRound(
                course = course,
                tee = tee,
                gameType = gameType,
                selectedPlayers = selectedPlayers,
                isHandicapEnabled = isHandicapEnabled,
                dotsEnabled = dotsEnabled,
                betPerUnit = betPerUnit
            )
            _showNewRoundDialog.value = false
            loadRound(round.id)
            _currentHoleNumber.value = 1
            _currentTab.value = GolfTab.SCORECARD
        }
    }

    fun updateScore(
        playerId: String,
        holeNumber: Int,
        newGrossScore: Int
    ) {
        viewModelScope.launch {
            val round = _activeRound.value ?: return@launch
            val currentScores = _activeHoleScores.value
            val existing = currentScores.find { it.playerId == playerId && it.holeNumber == holeNumber } ?: return@launch

            val hole = _activeHoles.value.find { it.holeNumber == holeNumber }
            val roundPlayer = _activeRoundPlayers.value.find { it.playerId == playerId }

            val strokesAllocated = if (round.isHandicapEnabled && roundPlayer != null && hole != null) {
                HandicapCalculator.getStrokesForHole(roundPlayer.courseHandicap, hole.handicapRank)
            } else 0

            val netScore = if (newGrossScore > 0) {
                HandicapCalculator.calculateNetScore(newGrossScore, strokesAllocated)
            } else 0

            // Auto-detect GIR based on score and putts if putts recorded
            val par = hole?.par ?: 4
            val strokesToGreen = newGrossScore - existing.putts
            val isGir = (newGrossScore > 0 && existing.putts > 0 && strokesToGreen <= (par - 2))

            val updated = existing.copy(
                grossScore = newGrossScore.coerceAtLeast(0),
                netScore = netScore,
                strokesAllocated = strokesAllocated,
                gir = isGir
            )
            repository.updateHoleScore(updated)
        }
    }

    fun updatePutts(
        playerId: String,
        holeNumber: Int,
        newPutts: Int
    ) {
        viewModelScope.launch {
            val currentScores = _activeHoleScores.value
            val existing = currentScores.find { it.playerId == playerId && it.holeNumber == holeNumber } ?: return@launch

            val hole = _activeHoles.value.find { it.holeNumber == holeNumber }
            val par = hole?.par ?: 4
            val strokesToGreen = existing.grossScore - newPutts
            val isGir = (existing.grossScore > 0 && strokesToGreen <= (par - 2))

            val updated = existing.copy(
                putts = newPutts.coerceAtLeast(0),
                gir = isGir
            )
            repository.updateHoleScore(updated)
        }
    }

    fun updateFairwayHit(
        playerId: String,
        holeNumber: Int,
        fairwayHit: String // "HIT", "LEFT", "RIGHT", "NA"
    ) {
        viewModelScope.launch {
            val existing = _activeHoleScores.value.find { it.playerId == playerId && it.holeNumber == holeNumber } ?: return@launch
            repository.updateHoleScore(existing.copy(fairwayHit = fairwayHit))
        }
    }

    fun toggleDotForPlayer(
        playerId: String,
        holeNumber: Int,
        dotId: String
    ) {
        viewModelScope.launch {
            val existing = _activeHoleScores.value.find { it.playerId == playerId && it.holeNumber == holeNumber } ?: return@launch
            val currentDots = existing.getDotsList().toMutableList()
            if (currentDots.contains(dotId)) {
                currentDots.remove(dotId)
            } else {
                currentDots.add(dotId)
            }
            repository.updateHoleScore(existing.withDots(currentDots))
        }
    }

    fun addPlayer(name: String, handicapIndex: Double) {
        viewModelScope.launch {
            val p = Player(
                name = name,
                handicapIndex = handicapIndex,
                avatarColorHex = listOf("#0F4D33", "#1B6B48", "#1E88E5", "#D32F2F", "#FFA000", "#7B1FA2").random()
            )
            repository.savePlayer(p)
        }
    }

    fun updatePlayerHandicap(player: Player, newHandicap: Double) {
        viewModelScope.launch {
            repository.updatePlayer(player.copy(handicapIndex = newHandicap))
        }
    }

    fun completeRound() {
        viewModelScope.launch {
            val current = _activeRound.value ?: return@launch
            val updated = current.copy(status = "COMPLETED")
            repository.updateRound(updated)
            _activeRound.value = updated
        }
    }

    fun toggleHandicapMode() {
        viewModelScope.launch {
            val current = _activeRound.value ?: return@launch
            val newHandicapState = !current.isHandicapEnabled
            val updatedRound = current.copy(isHandicapEnabled = newHandicapState)
            repository.updateRound(updatedRound)
            _activeRound.value = updatedRound

            // Re-evaluate net scores and allocated strokes for all existing scores
            val currentScores = _activeHoleScores.value
            val roundPlayers = _activeRoundPlayers.value
            val holes = _activeHoles.value

            val updatedScores = currentScores.map { hs ->
                val rp = roundPlayers.find { it.playerId == hs.playerId }
                val hole = holes.find { it.holeNumber == hs.holeNumber }
                val strokes = if (newHandicapState && rp != null && hole != null) {
                    HandicapCalculator.getStrokesForHole(rp.courseHandicap, hole.handicapRank)
                } else 0
                val net = if (hs.grossScore > 0) {
                    if (newHandicapState) HandicapCalculator.calculateNetScore(hs.grossScore, strokes) else hs.grossScore
                } else 0
                hs.copy(strokesAllocated = strokes, netScore = net)
            }
            updatedScores.forEach { repository.updateHoleScore(it) }
        }
    }

    // Helper: evaluate 6's current hole results
    fun getCurrentSixesResult(): SixesHoleResult? {
        val round = _activeRound.value ?: return null
        if (round.gameType != "SIXES" || _activeRoundPlayers.value.size < 4) return null
        val holeNum = _currentHoleNumber.value
        val scoresMap = _activeHoleScores.value.filter { it.holeNumber == holeNum }.associateBy { it.playerId }
        return GolfGameEngine.evaluateSixesHole(
            holeNumber = holeNum,
            roundPlayers = _activeRoundPlayers.value,
            scoresByPlayerId = scoresMap,
            useNet = round.isHandicapEnabled
        )
    }

    // Helper: evaluate Low Ball / Low Total 2v2 current hole results
    fun getCurrentLowBallLowTotalResult(): com.example.engine.LowBallLowTotalHoleResult? {
        val round = _activeRound.value ?: return null
        if (round.gameType != "LOW_BALL_LOW_TOTAL" || _activeRoundPlayers.value.size < 4) return null
        val holeNum = _currentHoleNumber.value
        val scoresMap = _activeHoleScores.value.filter { it.holeNumber == holeNum }.associateBy { it.playerId }
        return GolfGameEngine.evaluateLowBallLowTotalHole(
            holeNumber = holeNum,
            roundPlayers = _activeRoundPlayers.value,
            scoresByPlayerId = scoresMap,
            useNet = round.isHandicapEnabled
        )
    }

    // Helper: full 18 match summary for Low Ball / Low Total 2v2
    fun getLowBallLowTotalMatchSummary(): com.example.engine.LowBallLowTotalMatchSummary? {
        val round = _activeRound.value ?: return null
        if (round.gameType != "LOW_BALL_LOW_TOTAL" || _activeRoundPlayers.value.size < 4) return null
        return GolfGameEngine.evaluateLowBallLowTotalMatch(
            roundPlayers = _activeRoundPlayers.value,
            allScores = _activeHoleScores.value,
            useNet = round.isHandicapEnabled
        )
    }

    // Helper: get player standings
    fun getPlayerStandings(): List<PlayerDotStanding> {
        val round = _activeRound.value ?: return emptyList()
        return GolfGameEngine.computePlayerStandings(
            roundPlayers = _activeRoundPlayers.value,
            allScores = _activeHoleScores.value,
            useNet = round.isHandicapEnabled,
            gameType = round.gameType
        )
    }

    // Helper: evaluate Skins
    fun getSkinsResults(): List<SkinsHoleResult> {
        val round = _activeRound.value ?: return emptyList()
        return GolfGameEngine.evaluateSkins(
            roundPlayers = _activeRoundPlayers.value,
            allScores = _activeHoleScores.value,
            useNet = round.isHandicapEnabled
        )
    }

    fun searchRemoteCourses(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearchingApi.value = true
            repository.fetchAndCacheRemoteCourses(query)
            _isSearchingApi.value = false
        }
    }
}
