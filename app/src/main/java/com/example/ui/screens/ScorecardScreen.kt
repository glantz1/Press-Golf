package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DotCatalog
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.engine.GolfGameEngine
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BirdieBlue
import com.example.ui.theme.BogeyOrange
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.DoubleBogeyRed
import com.example.ui.theme.EagleGold
import com.example.ui.theme.FairwayGreen
import com.example.ui.theme.FairwaySurface
import com.example.ui.theme.ParGreen
import com.example.ui.viewmodel.GolfViewModel

@Composable
fun ScorecardScreen(
    viewModel: GolfViewModel,
    onStartNewRoundClick: () -> Unit
) {
    val activeRound by viewModel.activeRound.collectAsState()
    val activeRoundPlayers by viewModel.activeRoundPlayers.collectAsState()
    val activeHoles by viewModel.activeHoles.collectAsState()
    val activeHoleScores by viewModel.activeHoleScores.collectAsState()
    val currentHoleNumber by viewModel.currentHoleNumber.collectAsState()

    if (activeRound == null || activeRoundPlayers.isEmpty()) {
        // Empty State: Prompt to start a round
        NoActiveRoundState(onStartNewRoundClick = onStartNewRoundClick)
    } else {
        val currentRound = activeRound!!
        val currentHole = activeHoles.find { it.holeNumber == currentHoleNumber }
            ?: Hole(courseId = "", teeId = "", holeNumber = currentHoleNumber, par = 4, handicapRank = currentHoleNumber, yardage = 400)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Scrollable Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Round Header Info with Net vs Gross live switcher
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    ScorecardHeaderCard(
                        round = currentRound,
                        onStartNewRoundClick = onStartNewRoundClick,
                        onToggleHandicapMode = { viewModel.toggleHandicapMode() }
                    )
                }

                // Hole Carousel Selector (1..18)
                item {
                    HoleCarousel(
                        currentHoleNumber = currentHoleNumber,
                        scores = activeHoleScores,
                        playerCount = activeRoundPlayers.size,
                        onSelectHole = { viewModel.setCurrentHole(it) }
                    )
                }

                // Hole Details Card (Par, Yardage, Handicap Rank, and 2v2 / 6's Matchup)
                item {
                    HoleInfoBanner(
                        hole = currentHole,
                        round = currentRound,
                        players = activeRoundPlayers
                    )
                }

                // Low Ball / Low Total 2v2 Real-Time Result Banner
                if (currentRound.gameType == "LOW_BALL_LOW_TOTAL") {
                    item {
                        val lbResult = viewModel.getCurrentLowBallLowTotalResult()
                        if (lbResult != null) {
                            LowBallLowTotalHoleBanner(result = lbResult, players = activeRoundPlayers)
                        }
                    }
                }

                // 6's Real-Time Result Banner (if 6's game)
                if (currentRound.gameType == "SIXES") {
                    item {
                        val sixesResult = viewModel.getCurrentSixesResult()
                        if (sixesResult != null) {
                            SixesHoleBanner(result = sixesResult, players = activeRoundPlayers)
                        }
                    }
                }

                // Player Scoring Cards
                items(activeRoundPlayers) { player ->
                    val holeScore = activeHoleScores.find { it.playerId == player.playerId && it.holeNumber == currentHoleNumber }
                        ?: HoleScore(roundId = currentRound.id, playerId = player.playerId, holeNumber = currentHoleNumber, grossScore = 0, netScore = 0)

                    PlayerScoreCard(
                        player = player,
                        hole = currentHole,
                        holeScore = holeScore,
                        isHandicapEnabled = currentRound.isHandicapEnabled,
                        dotsEnabled = currentRound.dotsEnabled,
                        onUpdateScore = { newScore ->
                            viewModel.updateScore(player.playerId, currentHoleNumber, newScore)
                        },
                        onUpdatePutts = { newPutts ->
                            viewModel.updatePutts(player.playerId, currentHoleNumber, newPutts)
                        },
                        onUpdateFairway = { fairwayHit ->
                            viewModel.updateFairwayHit(player.playerId, currentHoleNumber, fairwayHit)
                        },
                        onToggleDot = { dotId ->
                            viewModel.toggleDotForPlayer(player.playerId, currentHoleNumber, dotId)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Navigation & Actions Bar
            ScorecardBottomBar(
                currentHole = currentHoleNumber,
                onPreviousHole = { viewModel.previousHole() },
                onNextHole = { viewModel.nextHole() },
                onShowFullScorecard = { viewModel.showFullScorecardDialog(true) },
                onShowLeaderboard = { viewModel.showLeaderboardDialog(true) }
            )
        }
    }
}

@Composable
fun NoActiveRoundState(onStartNewRoundClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AugustaGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GolfCourse,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Active Round in Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select a course and game format (such as 6's partner rotation with travelling dots, Skins, or Nassau) to begin scoring with automatic handicaps.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onStartNewRoundClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_new_round_empty_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tee Off New Round", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ScorecardHeaderCard(
    round: Round,
    onStartNewRoundClick: () -> Unit,
    onToggleHandicapMode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = round.courseName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${round.teeName} • ${round.gameType}",
                        fontSize = 12.sp,
                        color = AugustaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interactive Net vs Gross Toggle Pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, AugustaGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onToggleHandicapMode() }
                        .testTag("toggle_handicap_mode_chip"),
                    color = if (round.isHandicapEnabled) AugustaGreen.copy(alpha = 0.12f) else BunkerGold.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (round.isHandicapEnabled) "🎯 Net" else "⚡ Gross",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (round.isHandicapEnabled) AugustaGreen else BunkerGold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onStartNewRoundClick,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("change_round_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HoleCarousel(
    currentHoleNumber: Int,
    scores: List<HoleScore>,
    playerCount: Int,
    onSelectHole: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(18) { idx ->
            val holeNum = idx + 1
            val isSelected = holeNum == currentHoleNumber
            val completedCount = scores.filter { it.holeNumber == holeNum && it.isCompleted }.size
            val isComplete = playerCount > 0 && completedCount >= playerCount

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            isSelected -> AugustaGreen
                            isComplete -> AugustaGreen.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BunkerGold else Color.LightGray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectHole(holeNum) }
                    .testTag("hole_chip_$holeNum"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$holeNum",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (isComplete && !isSelected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(AugustaGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HoleInfoBanner(
    hole: Hole,
    round: Round,
    players: List<RoundPlayer>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FairwaySurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AugustaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${hole.holeNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hole ${hole.holeNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stroke Index / HDCP: ${hole.handicapRank}",
                            fontSize = 11.sp,
                            color = BunkerGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "PAR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${hole.par}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AugustaGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "YARDS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${hole.yardage}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Fixed 2v2 Matchup Banner for Low Ball / Low Total
            if (round.gameType == "LOW_BALL_LOW_TOTAL" && players.size == 4) {
                Spacer(modifier = Modifier.height(10.dp))
                val t1 = "${players[0].playerName.split(" ").first()} & ${players[1].playerName.split(" ").first()}"
                val t2 = "${players[2].playerName.split(" ").first()} & ${players[3].playerName.split(" ").first()}"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AugustaGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2v2 Match: $t1  vs  $t2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AugustaGreen
                        )
                        Text(
                            text = "Low Ball (1) + Low Total (1)",
                            fontSize = 11.sp,
                            color = BunkerGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 6's Rotation Leg Banner
            if (round.gameType == "SIXES" && players.size == 4) {
                Spacer(modifier = Modifier.height(10.dp))
                val pairing = GolfGameEngine.getSixesPairingForHole(hole.holeNumber)
                val t1 = pairing.team1PlayerIndices.map { players[it].playerName.split(" ").first() }.joinToString(" & ")
                val t2 = pairing.team2PlayerIndices.map { players[it].playerName.split(" ").first() }.joinToString(" & ")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AugustaGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "6's Matchup: $t1  vs  $t2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AugustaGreen
                        )
                        Text(
                            text = "2 Dots available",
                            fontSize = 11.sp,
                            color = BunkerGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LowBallLowTotalHoleBanner(
    result: com.example.engine.LowBallLowTotalHoleResult,
    players: List<RoundPlayer>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hole ${result.holeNumber} Points (2v2 Full Match)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AugustaGreen
                )
                Text(
                    text = "Team 1: +${result.team1PointsWon} pt • Team 2: +${result.team2PointsWon} pt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BunkerGold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val lowBallText = when (result.lowBallWinnerTeam) {
                    1 -> "Low Ball: Team 1 (${result.team1LowScore})"
                    2 -> "Low Ball: Team 2 (${result.team2LowScore})"
                    else -> "Low Ball: Halved (${result.team1LowScore})"
                }
                Text(text = lowBallText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                val lowTotalText = when (result.lowTotalWinnerTeam) {
                    1 -> "Low Total: Team 1 (${result.team1Total})"
                    2 -> "Low Total: Team 2 (${result.team2Total})"
                    else -> "Low Total: Halved (${result.team1Total})"
                }
                Text(text = lowTotalText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SixesHoleBanner(
    result: com.example.engine.SixesHoleResult,
    players: List<RoundPlayer>
) {
    val pairing = GolfGameEngine.getSixesPairingForHole(result.holeNumber)
    val t1Names = pairing.team1PlayerIndices.map { players[it].playerName.split(" ").first() }.joinToString(" & ")
    val t2Names = pairing.team2PlayerIndices.map { players[it].playerName.split(" ").first() }.joinToString(" & ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Hole ${result.holeNumber} Results (Low Ball & Low Total)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = AugustaGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val lowBallText = when (result.lowBallWinnerTeam) {
                    1 -> "Low Ball: $t1Names (${result.team1LowScore})"
                    2 -> "Low Ball: $t2Names (${result.team2LowScore})"
                    else -> "Low Ball: Halved/Tied (${result.team1LowScore})"
                }
                Text(text = lowBallText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                val lowTotalText = when (result.lowTotalWinnerTeam) {
                    1 -> "Low Total: $t1Names (${result.team1Total})"
                    2 -> "Low Total: $t2Names (${result.team2Total})"
                    else -> "Low Total: Halved/Tied"
                }
                Text(text = lowTotalText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PlayerScoreCard(
    player: RoundPlayer,
    hole: Hole,
    holeScore: HoleScore,
    isHandicapEnabled: Boolean,
    dotsEnabled: Boolean,
    onUpdateScore: (Int) -> Unit,
    onUpdatePutts: (Int) -> Unit,
    onUpdateFairway: (String) -> Unit,
    onToggleDot: (String) -> Unit
) {
    val gross = holeScore.grossScore
    val par = hole.par
    val net = holeScore.netScore
    val strokesAllocated = holeScore.strokesAllocated

    val strokeDotText = when {
        strokesAllocated == 1 -> "• 1 Stroke"
        strokesAllocated == 2 -> "•• 2 Strokes"
        strokesAllocated > 2 -> "••• $strokesAllocated Strokes"
        strokesAllocated < 0 -> "Giving 1 Stroke"
        else -> "Scratch (0)"
    }

    val scoreBadgeColor = when {
        gross == 0 -> Color.Gray
        gross - par <= -2 -> EagleGold
        gross - par == -1 -> BirdieBlue
        gross == par -> ParGreen
        gross - par == 1 -> BogeyOrange
        else -> DoubleBogeyRed
    }

    val scoreLabel = when {
        gross == 0 -> "No score"
        gross - par <= -2 -> "Eagle / Better"
        gross - par == -1 -> "Birdie"
        gross == par -> "Par"
        gross - par == 1 -> "Bogey"
        gross - par == 2 -> "Double Bogey"
        else -> "Triple+"
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Player Title & Handicap Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AugustaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.playerName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = player.playerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Course HDCP: ${player.courseHandicap}  ($strokeDotText)",
                            fontSize = 11.sp,
                            color = BunkerGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isHandicapEnabled && gross > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AugustaGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Net $net",
                            color = AugustaGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gross Score Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Score (${scoreLabel})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isHandicapEnabled && strokesAllocated > 0 && gross > 0) {
                        Text(
                            text = "Net: $net ($gross - $strokesAllocated)",
                            fontSize = 11.sp,
                            color = AugustaGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Minus Button
                    IconButton(
                        onClick = {
                            val newScore = if (gross <= 1) 0 else gross - 1
                            onUpdateScore(newScore)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("score_minus_${player.playerId}")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Score", modifier = Modifier.size(18.dp))
                    }

                    // Score Display Circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (gross > 0) scoreBadgeColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f))
                            .border(2.dp, if (gross > 0) scoreBadgeColor else Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (gross > 0) "$gross" else "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gross > 0) scoreBadgeColor else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Plus Button
                    IconButton(
                        onClick = {
                            val newScore = if (gross == 0) par else gross + 1
                            onUpdateScore(newScore)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AugustaGreen)
                            .testTag("score_plus_${player.playerId}")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Score", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Putts & Fairway Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Putts selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Putts:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(1, 2, 3, 4).forEach { pCount ->
                        val isPuttSelected = holeScore.putts == pCount
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPuttSelected) AugustaGreen else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onUpdatePutts(if (isPuttSelected) 0 else pCount) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$pCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPuttSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Fairway (only on par 4 or 5)
                if (hole.par >= 4) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "FIR:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        listOf("LEFT" to "L", "HIT" to "✓", "RIGHT" to "R").forEach { (key, lbl) ->
                            val isFirSelected = holeScore.fairwayHit == key
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            isFirSelected && key == "HIT" -> ParGreen
                                            isFirSelected -> BogeyOrange
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable { onUpdateFairway(if (isFirSelected) "NA" else key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lbl,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFirSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Side Junk / Dots Selector
            if (dotsEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                val playerDots = holeScore.getDotsList()

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val selectableDots = DotCatalog.ALL_DOTS.filter { !it.isGameDot }
                    items(selectableDots) { dot ->
                        val isSelected = playerDots.contains(dot.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleDot(dot.id) },
                            label = {
                                Text(
                                    text = "${dot.iconEmoji} ${dot.name}",
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (dot.id == "SNAKE") DoubleBogeyRed.copy(alpha = 0.2f) else BunkerGold.copy(alpha = 0.25f),
                                selectedLabelColor = if (dot.id == "SNAKE") DoubleBogeyRed else AugustaGreen
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScorecardBottomBar(
    currentHole: Int,
    onPreviousHole: () -> Unit,
    onNextHole: () -> Unit,
    onShowFullScorecard: () -> Unit,
    onShowLeaderboard: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Hole Button
            FilledTonalButton(
                onClick = onPreviousHole,
                enabled = currentHole > 1,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Hole", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Hole ${currentHole - 1}", fontSize = 11.sp)
            }

            // Full Scorecard Grid Modal Button
            IconButton(onClick = onShowFullScorecard, modifier = Modifier.testTag("full_scorecard_grid_button")) {
                Icon(imageVector = Icons.Default.TableChart, contentDescription = "Full Scorecard Grid", tint = AugustaGreen)
            }

            // Leaderboard Modal Button
            Button(
                onClick = onShowLeaderboard,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BunkerGold),
                modifier = Modifier.testTag("view_leaderboard_button")
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = AugustaGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Dots & Board", color = AugustaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Next Hole Button
            FilledTonalButton(
                onClick = onNextHole,
                enabled = currentHole < 18,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Hole ${currentHole + 1}", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Hole", modifier = Modifier.size(16.dp))
            }
        }
    }
}
