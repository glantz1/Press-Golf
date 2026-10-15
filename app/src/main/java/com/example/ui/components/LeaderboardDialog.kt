package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Round
import com.example.engine.PlayerDotStanding
import com.example.engine.SkinsHoleResult
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwaySurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardDialog(
    round: Round,
    standings: List<PlayerDotStanding>,
    skinsResults: List<SkinsHoleResult>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
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
                            .background(BunkerGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = BunkerGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${round.gameType} Leaderboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Live Standings & Payouts ($${String.format("%.2f", round.betPerUnit)}/unit)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_leaderboard_dialog")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOW BALL / LOW TOTAL 2v2 MATCH SCOREBOARD
            if (round.gameType == "LOW_BALL_LOW_TOTAL" && standings.size >= 4) {
                val t1Players = listOf(standings[0], standings[1])
                val t2Players = listOf(standings[2], standings[3])
                val t1Pts = t1Players.firstOrNull()?.sixesDots ?: 0
                val t2Pts = t2Players.firstOrNull()?.sixesDots ?: 0
                val diff = t1Pts - t2Pts
                val payout = kotlin.math.abs(diff) * round.betPerUnit

                Card(
                    colors = CardDefaults.cardColors(containerColor = FairwaySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2v2 Match Scoreboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AugustaGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AugustaGreen
                            ) {
                                Text(
                                    text = if (round.isHandicapEnabled) "Net Score Mode" else "Gross Score Mode",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Team 1
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TEAM 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AugustaGreen)
                                Text(text = "$t1Pts", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AugustaGreen)
                                Text(text = "Points", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Text(text = "VS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BunkerGold)

                            // Team 2
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TEAM 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BunkerGold)
                                Text(text = "$t2Pts", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = BunkerGold)
                                Text(text = "Points", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val statusText = when {
                            diff > 0 -> "Team 1 leads by $diff points (+$${String.format("%.2f", payout)})"
                            diff < 0 -> "Team 2 leads by ${-diff} points (+$${String.format("%.2f", payout)})"
                            else -> "All Square / Tied (0 point difference)"
                        }
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (diff != 0) AugustaGreen else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6'S ROTATION EXPLANATION CARD (if 6's)
            if (round.gameType == "SIXES") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FairwaySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "6's Partner Rotation & Travelling Dots",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AugustaGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Leg 1 (1–6): A & B vs C & D\n• Leg 2 (7–12): A & C vs B & D\n• Leg 3 (13–18): A & D vs B & C\n\nDots for Low Ball (1) & Low Total (1) stay with players and accumulate!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // STANDINGS TABLE
            Text(
                text = "Player Rankings & Dots Bank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val avgDots = if (standings.isNotEmpty()) standings.map { it.totalDots }.average() else 0.0

            standings.forEachIndexed { rank, player ->
                val rankLabel = "#${rank + 1}"
                val isLeader = rank == 0
                val netDifferentialFromAverage = player.totalDots - avgDots
                val estimatedPayout = netDifferentialFromAverage * round.betPerUnit

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLeader) AugustaGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isLeader) BunkerGold else AugustaGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = rankLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isLeader) Color.White else AugustaGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = player.playerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Gross: ${player.grossTotal} • Net: ${player.netTotal} • Junk Dots: ${player.junkDots}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AugustaGreen
                            ) {
                                Text(
                                    text = "${player.totalDots} Dots",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val payoutText = if (estimatedPayout >= 0) "+$${String.format("%.2f", estimatedPayout)}" else "-$${String.format("%.2f", -estimatedPayout)}"
                            Text(
                                text = payoutText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (estimatedPayout >= 0) AugustaGreen else Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }

            // SKINS DETAILS (if Skins)
            if (round.gameType == "SKINS" && skinsResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Skins & Carryovers Board",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val wonSkins = skinsResults.filter { it.winnerPlayerId != null }
                val carryoverSkins = skinsResults.filter { it.isTied }

                Text(
                    text = "${wonSkins.size} Skins Won • ${carryoverSkins.size} Carryovers recorded",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                wonSkins.forEach { skin ->
                    val winnerName = standings.find { it.playerId == skin.winnerPlayerId }?.playerName ?: "Winner"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hole ${skin.holeNumber} (${skin.skinsValue} Skins)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$winnerName (Score: ${skin.winningScore})",
                            fontSize = 12.sp,
                            color = AugustaGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
