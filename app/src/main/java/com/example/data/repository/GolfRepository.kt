package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.GolfCourseApiService
import com.example.data.db.GolfDao
import com.example.data.db.GolfDatabase
import com.example.data.model.Course
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Player
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.data.model.Tee
import com.example.engine.HandicapCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class GolfRepository(
    private val dao: GolfDao,
    private val apiService: GolfCourseApiService = GolfCourseApiService.create()
) {

    // --- PLAYERS ---
    fun getAllPlayersFlow(): Flow<List<Player>> = dao.getAllPlayersFlow()

    suspend fun savePlayer(player: Player) = withContext(Dispatchers.IO) {
        dao.insertPlayer(player)
    }

    suspend fun updatePlayer(player: Player) = withContext(Dispatchers.IO) {
        dao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) = withContext(Dispatchers.IO) {
        dao.deletePlayer(player)
    }

    // --- COURSES ---
    fun getAllCoursesFlow(): Flow<List<Course>> = dao.getAllCoursesFlow()

    fun searchCoursesFlow(query: String): Flow<List<Course>> = dao.searchCoursesFlow(query)

    suspend fun getCourseById(courseId: String): Course? = withContext(Dispatchers.IO) {
        dao.getCourseById(courseId)
    }

    fun getTeesForCourseFlow(courseId: String): Flow<List<Tee>> = dao.getTeesForCourseFlow(courseId)

    suspend fun getTeeById(teeId: String): Tee? = withContext(Dispatchers.IO) {
        dao.getTeeById(teeId)
    }

    fun getHolesForTeeFlow(teeId: String): Flow<List<Hole>> = dao.getHolesForTeeFlow(teeId)

    suspend fun getHolesForTee(teeId: String): List<Hole> = withContext(Dispatchers.IO) {
        dao.getHolesForTeeSync(teeId)
    }

    suspend fun createCustomCourse(
        course: Course,
        tee: Tee,
        holes: List<Hole>
    ) = withContext(Dispatchers.IO) {
        dao.insertCourse(course)
        dao.insertTees(listOf(tee))
        dao.insertHoles(holes)
    }

    /**
     * Search global courses via remote API and seamlessly cache in Room DB.
     */
    suspend fun fetchAndCacheRemoteCourses(query: String): List<Course> = withContext(Dispatchers.IO) {
        try {
            val remoteResults = apiService.searchCourses(query)
            val newCourses = mutableListOf<Course>()

            remoteResults.forEach { item ->
                val course = Course(
                    id = "api_${item.id}",
                    name = item.name,
                    city = item.city,
                    state = item.state,
                    country = item.country,
                    holesCount = item.holesCount,
                    courseType = item.courseType,
                    apiId = item.id
                )
                val tee = Tee(
                    id = "tee_api_${item.id}",
                    courseId = course.id,
                    name = "Championship",
                    colorHex = "#1B6B48",
                    courseRating = item.rating,
                    slopeRating = item.slope,
                    parTotal = item.par,
                    yardageTotal = item.yardage
                )
                val holes = (1..18).map { holeNum ->
                    val par = item.pars?.getOrNull(holeNum - 1) ?: when (holeNum) {
                        3, 7, 12, 17 -> 3
                        5, 14, 18 -> 5
                        else -> 4
                    }
                    val hdcp = item.handicapRanks?.getOrNull(holeNum - 1) ?: holeNum
                    Hole(
                        id = "hole_api_${item.id}_$holeNum",
                        courseId = course.id,
                        teeId = tee.id,
                        holeNumber = holeNum,
                        par = par,
                        handicapRank = hdcp,
                        yardage = (item.yardage / 18)
                    )
                }
                dao.insertCourse(course)
                dao.insertTees(listOf(tee))
                dao.insertHoles(holes)
                newCourses.add(course)
            }
            newCourses
        } catch (e: Exception) {
            Log.d("GolfRepository", "Remote API unavailable or offline: ${e.message}")
            emptyList()
        }
    }

    // --- ROUNDS & SCORING ---
    fun getAllRoundsFlow(): Flow<List<Round>> = dao.getAllRoundsFlow()

    fun getRoundFlow(roundId: String): Flow<Round?> = dao.getRoundFlow(roundId)

    suspend fun getRoundById(roundId: String): Round? = withContext(Dispatchers.IO) {
        dao.getRoundById(roundId)
    }

    fun getRoundPlayersFlow(roundId: String): Flow<List<RoundPlayer>> = dao.getRoundPlayersFlow(roundId)

    suspend fun getRoundPlayersSync(roundId: String): List<RoundPlayer> = withContext(Dispatchers.IO) {
        dao.getRoundPlayersSync(roundId)
    }

    fun getHoleScoresForRoundFlow(roundId: String): Flow<List<HoleScore>> = dao.getHoleScoresForRoundFlow(roundId)

    suspend fun getHoleScoresForRoundSync(roundId: String): List<HoleScore> = withContext(Dispatchers.IO) {
        dao.getHoleScoresForRoundSync(roundId)
    }

    /**
     * Initializes a new round with selected course, tee, game type, and players.
     * Computes WHS Course Handicaps and pre-allocates hole handicap strokes!
     */
    suspend fun createRound(
        course: Course,
        tee: Tee,
        gameType: String,
        selectedPlayers: List<Player>,
        isHandicapEnabled: Boolean,
        dotsEnabled: Boolean,
        betPerUnit: Double
    ): Round = withContext(Dispatchers.IO) {
        val roundId = UUID.randomUUID().toString()
        val round = Round(
            id = roundId,
            courseId = course.id,
            teeId = tee.id,
            courseName = course.name,
            teeName = tee.name,
            dateMillis = System.currentTimeMillis(),
            gameType = gameType,
            isHandicapEnabled = isHandicapEnabled,
            dotsEnabled = dotsEnabled,
            betPerUnit = betPerUnit,
            status = "IN_PROGRESS"
        )
        dao.insertRound(round)

        val holes = dao.getHolesForTeeSync(tee.id)

        // Create RoundPlayer entries with calculated Course Handicaps
        val roundPlayers = selectedPlayers.mapIndexed { idx, player ->
            val courseHdcp = if (isHandicapEnabled) {
                HandicapCalculator.calculateCourseHandicap(
                    handicapIndex = player.handicapIndex,
                    slopeRating = tee.slopeRating,
                    courseRating = tee.courseRating,
                    parTotal = tee.parTotal
                )
            } else {
                0
            }

            RoundPlayer(
                id = UUID.randomUUID().toString(),
                roundId = roundId,
                playerId = player.id,
                playerName = player.name,
                handicapIndexAtRound = player.handicapIndex,
                courseHandicap = courseHdcp,
                playerSlot = idx
            )
        }
        dao.insertRoundPlayers(roundPlayers)

        // Initialize empty hole scores for all players for 18 holes
        val initialHoleScores = mutableListOf<HoleScore>()
        roundPlayers.forEach { rp ->
            for (holeNum in 1..18) {
                val hole = holes.find { it.holeNumber == holeNum }
                val hdcpRank = hole?.handicapRank ?: holeNum
                val strokesAllocated = if (isHandicapEnabled) {
                    HandicapCalculator.getStrokesForHole(rp.courseHandicap, hdcpRank)
                } else 0

                initialHoleScores.add(
                    HoleScore(
                        id = UUID.randomUUID().toString(),
                        roundId = roundId,
                        playerId = rp.playerId,
                        holeNumber = holeNum,
                        grossScore = 0,
                        netScore = 0,
                        strokesAllocated = strokesAllocated,
                        putts = 0,
                        fairwayHit = "NA",
                        gir = false,
                        sandShots = 0,
                        penalties = 0,
                        dotsAwarded = ""
                    )
                )
            }
        }
        dao.insertOrUpdateHoleScores(initialHoleScores)

        round
    }

    suspend fun updateHoleScore(holeScore: HoleScore) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateHoleScore(holeScore)
    }

    suspend fun updateRound(round: Round) = withContext(Dispatchers.IO) {
        dao.updateRound(round)
    }

    suspend fun deleteRound(roundId: String) = withContext(Dispatchers.IO) {
        dao.deleteRound(roundId)
    }

    companion object {
        fun create(context: Context): GolfRepository {
            val db = GolfDatabase.getInstance(context)
            return GolfRepository(db.golfDao())
        }
    }
}
