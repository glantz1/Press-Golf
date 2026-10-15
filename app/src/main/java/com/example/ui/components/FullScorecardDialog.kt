package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Hole
import com.example.data.model.HoleScore
import com.example.data.model.Round
import com.example.data.model.RoundPlayer
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BirdieBlue
import com.example.ui.theme.BogeyOrange
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.DoubleBogeyRed
import com.example.ui.theme.EagleGold
import com.example.ui.theme.FairwaySurface
import com.example.ui.theme.ParGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScorecardDialog(
    round: Round,
    players: List<RoundPlayer>,
    holes: List<Hole>,
    scores: List<HoleScore>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0: Front 9, 1: Back 9, 2: 18 Full Summary

    val front9Holes = (1..9).map { num -> holes.find { it.holeNumber == num } ?: Hole(courseId = "", teeId = "", holeNumber = num, par = 4, handicapRank = num, yardage = 380) }
    val back9Holes = (10..18).map { num -> holes.find { it.holeNumber == num } ?: Hole(courseId = "", teeId = "", holeNumber = num, par = 4, handicapRank = num, yardage = 380) }

    val activeHoles = if (selectedViewMode == 0) front9Holes else back9Holes

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${round.courseName} - ${round.teeName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Official 18-Hole Scorecard • ${if (round.isHandicapEnabled) "Net WHS" else "Gross"}",
                        fontSize = 12.sp,
                        color = AugustaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_scorecard_dialog")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nine selector tabs
            TabRow(
                selectedTabIndex = selectedViewMode,
                containerColor = FairwaySurface,
                contentColor = AugustaGreen
            ) {
                Tab(
                    selected = selectedViewMode == 0,
                    onClick = { selectedViewMode = 0 },
                    text = { Text("Front 9 (1–9)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedViewMode == 1,
                    onClick = { selectedViewMode = 1 },
                    text = { Text("Back 9 (10–18)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedViewMode == 2,
                    onClick = { selectedViewMode = 2 },
                    text = { Text("18-Hole Totals", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedViewMode == 2) {
                // 18-HOLE TOTALS SUMMARY
                Full18SummaryView(players = players, scores = scores, isNet = round.isHandicapEnabled)
            } else {
                // DETAILED SCORECARD TABLE FOR 9 HOLES
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        // 1. Hole Row
                        ScorecardRow(
                            label = "HOLE",
                            values = activeHoles.map { it.holeNumber.toString() },
                            summary = if (selectedViewMode == 0) "OUT" else "IN",
                            isHeader = true
                        )

                        // 2. Par Row
                        val parSum = activeHoles.sumOf { it.par }
                        ScorecardRow(
                            label = "PAR",
                            values = activeHoles.map { it.par.toString() },
                            summary = parSum.toString(),
                            accentColor = AugustaGreen
                        )

                        // 3. Handicap Rank Row
                        ScorecardRow(
                            label = "HDCP",
                            values = activeHoles.map { it.handicapRank.toString() },
                            summary = "-",
                            isMuted = true
                        )

                        // 4. Yardage Row
                        val yardageSum = activeHoles.sumOf { it.yardage }
                        ScorecardRow(
                            label = "YARDS",
                            values = activeHoles.map { it.yardage.toString() },
                            summary = yardageSum.toString(),
                            isMuted = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(AugustaGreen.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Players Rows
                        players.forEach { player ->
                            val playerHoleScores = activeHoles.map { h ->
                                scores.find { it.playerId == player.playerId && it.holeNumber == h.holeNumber }
                            }
                            val completedScores = playerHoleScores.filterNotNull().filter { it.isCompleted }
                            val totalGross = completedScores.sumOf { it.grossScore }
                            val totalNet = completedScores.sumOf { it.netScore }

                            // Player Name banner
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = player.playerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.width(90.dp)
                                )
                                Text(
                                    text = "CH: ${player.courseHandicap}",
                                    fontSize = 11.sp,
                                    color = BunkerGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Gross Row
                            ScorecardPlayerScoreRow(
                                label = "Gross",
                                holes = activeHoles,
                                scores = playerHoleScores.map { it?.grossScore ?: 0 },
                                total = if (completedScores.isNotEmpty()) totalGross.toString() else "-"
                            )

                            // Net Row
                            if (round.isHandicapEnabled) {
                                ScorecardPlayerScoreRow(
                                    label = "Net",
                                    holes = activeHoles,
                                    scores = playerHoleScores.map { it?.netScore ?: 0 },
                                    total = if (completedScores.isNotEmpty()) totalNet.toString() else "-",
                                    isNet = true
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Full18SummaryView(
    players: List<RoundPlayer>,
    scores: List<HoleScore>,
    isNet: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        players.forEach { p ->
            val pScores = scores.filter { it.playerId == p.playerId && it.isCompleted }
            val front9 = pScores.filter { it.holeNumber in 1..9 }
            val back9 = pScores.filter { it.holeNumber in 10..18 }

            val f9Gross = front9.sumOf { it.grossScore }
            val b9Gross = back9.sumOf { it.grossScore }
            val totalGross = f9Gross + b9Gross

            val f9Net = front9.sumOf { it.netScore }
            val b9Net = back9.sumOf { it.netScore }
            val totalNet = f9Net + b9Net

            Card(
                colors = CardDefaults.cardColors(containerColor = FairwaySurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.playerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Course HDCP: ${p.courseHandicap}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BunkerGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SummaryColumn(title = "Front 9", value = if (front9.size == 9) "$f9Gross" else "$f9Gross (${front9.size}h)", subtitle = if (isNet) "Net $f9Net" else "")
                        SummaryColumn(title = "Back 9", value = if (back9.size == 9) "$b9Gross" else "$b9Gross (${back9.size}h)", subtitle = if (isNet) "Net $b9Net" else "")
                        SummaryColumn(
                            title = "18 TOTAL",
                            value = "$totalGross",
                            subtitle = if (isNet) "Net $totalNet" else "",
                            highlight = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryColumn(title: String, value: String, subtitle: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = if (highlight) 20.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) AugustaGreen else MaterialTheme.colorScheme.onSurface
        )
        if (subtitle.isNotBlank()) {
            Text(text = subtitle, fontSize = 11.sp, color = AugustaGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ScorecardRow(
    label: String,
    values: List<String>,
    summary: String,
    isHeader: Boolean = false,
    accentColor: Color? = null,
    isMuted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.SemiBold,
            color = accentColor ?: if (isMuted) Color.Gray else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(90.dp)
        )
        values.forEach { v ->
            Text(
                text = v,
                fontSize = 11.sp,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = accentColor ?: if (isMuted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(36.dp)
            )
        }
        Text(
            text = summary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = accentColor ?: MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(44.dp)
        )
    }
}

@Composable
fun ScorecardPlayerScoreRow(
    label: String,
    holes: List<Hole>,
    scores: List<Int>,
    total: String,
    isNet: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isNet) AugustaGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(90.dp)
        )
        scores.forEachIndexed { idx, s ->
            val hole = holes.getOrNull(idx)
            val par = hole?.par ?: 4
            val scoreColor = when {
                s == 0 -> Color.LightGray
                s - par <= -2 -> EagleGold
                s - par == -1 -> BirdieBlue
                s == par -> ParGreen
                s - par == 1 -> BogeyOrange
                else -> DoubleBogeyRed
            }

            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(26.dp),
                contentAlignment = Alignment.Center
            ) {
                if (s > 0) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(scoreColor.copy(alpha = 0.15f))
                            .border(1.dp, scoreColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }
                } else {
                    Text(text = "-", fontSize = 11.sp, color = Color.LightGray)
                }
            }
        }
        Text(
            text = total,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isNet) AugustaGreen else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(44.dp)
        )
    }
}
