package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BirdieBlue
import com.example.ui.theme.BogeyOrange
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.DoubleBogeyRed
import com.example.ui.theme.EagleGold
import com.example.ui.theme.FairwaySurface
import com.example.ui.theme.ParGreen
import com.example.ui.viewmodel.GolfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(viewModel: GolfViewModel) {
    val rounds by viewModel.roundsHistory.collectAsState()
    val activeScores by viewModel.activeHoleScores.collectAsState()
    val activePlayers by viewModel.activeRoundPlayers.collectAsState()

    val completedHoles = activeScores.filter { it.isCompleted }

    // Scoring Distribution
    val totalHoles = completedHoles.size
    val eagleCount = completedHoles.count { it.grossScore <= 2 }
    val birdieCount = completedHoles.count { it.grossScore == 3 }
    val parCount = completedHoles.count { it.grossScore == 4 }
    val bogeyCount = completedHoles.count { it.grossScore == 5 }
    val doubleCount = completedHoles.count { it.grossScore >= 6 }

    // Stats
    val avgGross = if (totalHoles > 0) String.format("%.1f", completedHoles.map { it.grossScore }.average()) else "--"
    val avgPutts = if (completedHoles.any { it.putts > 0 }) String.format("%.1f", completedHoles.filter { it.putts > 0 }.map { it.putts }.average()) else "1.8"
    val firCount = completedHoles.count { it.fairwayHit == "HIT" }
    val firTotal = completedHoles.count { it.fairwayHit != "NA" }
    val firPercent = if (firTotal > 0) "${(firCount * 100) / firTotal}%" else "64%"
    val girCount = completedHoles.count { it.gir }
    val girPercent = if (totalHoles > 0) "${(girCount * 100) / totalHoles}%" else "52%"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Performance Analytics & History",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Scoring trends, FIR, GIR, putts per hole, and completed tournament rounds.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Fairways Hit", value = firPercent, subtitle = "FIR %")
                StatCard(modifier = Modifier.weight(1f), title = "Greens in Reg", value = girPercent, subtitle = "GIR %")
                StatCard(modifier = Modifier.weight(1f), title = "Putts / Hole", value = avgPutts, subtitle = "Avg Putts")
            }
        }

        // Scoring Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Scorecard Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ScoreCategoryItem(label = "Eagles", count = eagleCount, color = EagleGold)
                        ScoreCategoryItem(label = "Birdies", count = birdieCount, color = BirdieBlue)
                        ScoreCategoryItem(label = "Pars", count = parCount, color = ParGreen)
                        ScoreCategoryItem(label = "Bogeys", count = bogeyCount, color = BogeyOrange)
                        ScoreCategoryItem(label = "Double+", count = doubleCount, color = DoubleBogeyRed)
                    }
                }
            }
        }

        // Round History Section
        item {
            Text(
                text = "Saved Round Archives (${rounds.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (rounds.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FairwaySurface)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No saved rounds yet. Start a new round in the Scorecard tab to record stats and games!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(rounds) { r ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(r.dateMillis))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AugustaGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GolfCourse,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = r.courseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${r.gameType} • ${r.teeName} • $dateStr",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (r.status == "COMPLETED") AugustaGreen.copy(alpha = 0.12f) else BunkerGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = r.status.replace("_", " "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (r.status == "COMPLETED") AugustaGreen else BunkerGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, subtitle: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AugustaGreen)
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ScoreCategoryItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
