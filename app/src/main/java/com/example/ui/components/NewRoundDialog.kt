package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CourseSeedData
import com.example.data.model.Course
import com.example.data.model.GolfGamesCatalog
import com.example.data.model.Player
import com.example.data.model.Tee
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwayGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRoundDialog(
    courses: List<Course>,
    availablePlayers: List<Player>,
    preselectedGameId: String,
    onDismiss: () -> Unit,
    onStartRound: (
        course: Course,
        tee: Tee,
        gameType: String,
        selectedPlayers: List<Player>,
        isHandicapEnabled: Boolean,
        dotsEnabled: Boolean,
        betPerUnit: Double
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val allBundles = remember { CourseSeedData.getInitialCourses() }

    var selectedCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: allBundles.first().course.id) }
    val currentBundle = allBundles.find { it.course.id == selectedCourseId } ?: allBundles.first()
    val availableTees = currentBundle.tees
    var selectedTeeId by remember { mutableStateOf(availableTees.first().id) }

    var selectedGameId by remember { mutableStateOf(preselectedGameId) }
    var isHandicapEnabled by remember { mutableStateOf(true) }
    var dotsEnabled by remember { mutableStateOf(true) }
    var selectedBet by remember { mutableStateOf(1.0) }

    // Selected Players: up to 4 golfers
    var selectedPlayerIds by remember {
        mutableStateOf(
            availablePlayers.take(4).map { it.id }.toMutableList()
        )
    }

    LaunchedEffect(availablePlayers) {
        if (selectedPlayerIds.isEmpty() && availablePlayers.isNotEmpty()) {
            selectedPlayerIds = availablePlayers.take(4).map { it.id }.toMutableList()
        }
    }

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
                .padding(bottom = 36.dp)
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Setup New Round",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_new_round_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. SELECT COURSE
            Text(
                text = "1. Select Golf Course",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AugustaGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allBundles.take(3).forEach { bundle ->
                    val isSelected = bundle.course.id == selectedCourseId
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCourseId = bundle.course.id
                            selectedTeeId = bundle.tees.first().id
                        },
                        label = { Text(bundle.course.name.split(" ").take(2).joinToString(" ")) }
                    )
                }
            }
            if (allBundles.size > 3) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allBundles.drop(3).take(3).forEach { bundle ->
                        val isSelected = bundle.course.id == selectedCourseId
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCourseId = bundle.course.id
                                selectedTeeId = bundle.tees.first().id
                            },
                            label = { Text(bundle.course.name.split(" ").take(2).joinToString(" ")) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. SELECT TEE BOX
            Text(
                text = "2. Select Tee Box",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AugustaGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableTees.forEach { tee ->
                    val isSelected = tee.id == selectedTeeId
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTeeId = tee.id }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AugustaGreen else Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AugustaGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = tee.name.split(" ").first(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${tee.yardageTotal} yds • Par ${tee.parTotal}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Rating ${tee.courseRating} / ${tee.slopeRating}",
                                fontSize = 10.sp,
                                color = BunkerGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. SELECT GAME FORMAT
            Text(
                text = "3. Select Game Format",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AugustaGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("LOW_BALL_LOW_TOTAL", "SIXES", "SKINS").forEach { gId ->
                    val gameDef = GolfGamesCatalog.getGameById(gId)
                    val isSelected = gId == selectedGameId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGameId = gId },
                        label = { Text(gameDef.shortName) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("NASSAU", "WOLF", "VEGAS", "STABLEFORD").forEach { gId ->
                    val gameDef = GolfGamesCatalog.getGameById(gId)
                    val isSelected = gId == selectedGameId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGameId = gId },
                        label = { Text(gameDef.shortName) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("FOUR_BALL", "BINGO_BANGO_BONGO", "QUOTA").forEach { gId ->
                    val gameDef = GolfGamesCatalog.getGameById(gId)
                    val isSelected = gId == selectedGameId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGameId = gId },
                        label = { Text(gameDef.shortName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. SELECT PLAYERS
            val selectedGame = GolfGamesCatalog.getGameById(selectedGameId)
            val isSixes = selectedGameId == "SIXES"
            val isLowBallTotal = selectedGameId == "LOW_BALL_LOW_TOTAL"
            val isFourPlayerRequired = isSixes || isLowBallTotal

            Text(
                text = when {
                    isLowBallTotal -> "4. Select 4 Players (Fixed 2v2 Match: T1 vs T2)"
                    isSixes -> "4. Select 4 Players (Rotation Slots A, B, C, D)"
                    else -> "4. Select Players (${selectedPlayerIds.size} selected)"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AugustaGreen
            )
            if (isLowBallTotal) {
                Text(
                    text = "Team 1: Player A & Player B  •  Team 2: Player C & Player D (Fixed for 18 holes)",
                    fontSize = 11.sp,
                    color = AugustaGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else if (isSixes) {
                Text(
                    text = "A & B vs C & D (1–6) • A & C vs B & D (7–12) • A & D vs B & C (13–18)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            availablePlayers.forEachIndexed { pIdx, player ->
                val isSelected = selectedPlayerIds.contains(player.id)
                val slotIndex = selectedPlayerIds.indexOf(player.id)
                val slotLabel = if (slotIndex >= 0) {
                    when {
                        isLowBallTotal -> if (slotIndex < 2) "T1-${listOf("A", "B")[slotIndex]}" else "T2-${listOf("C", "D")[slotIndex - 2]}"
                        isSixes -> listOf("A", "B", "C", "D").getOrElse(slotIndex) { "#${slotIndex + 1}" }
                        else -> "#${slotIndex + 1}"
                    }
                } else ""

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            val list = selectedPlayerIds.toMutableList()
                            if (isSelected) {
                                list.remove(player.id)
                            } else {
                                if (list.size < 4) list.add(player.id)
                            }
                            selectedPlayerIds = list
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AugustaGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isFourPlayerRequired && isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isLowBallTotal && slotIndex >= 2) BunkerGold else AugustaGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slotLabel,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Column {
                                Text(
                                    text = player.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Handicap Index: ${player.formattedHandicap}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = AugustaGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. SCORING MODE: NET VS GROSS (ALL GAMES)
            Text(
                text = "5. Scoring Mode (All Games Support Net & Gross)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AugustaGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Net Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isHandicapEnabled = true }
                        .border(
                            width = if (isHandicapEnabled) 2.dp else 1.dp,
                            color = if (isHandicapEnabled) AugustaGreen else Color.LightGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHandicapEnabled) AugustaGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "🎯 Net (Handicap)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isHandicapEnabled) AugustaGreen else MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Auto-allocates WHS strokes by hole handicap rank",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Gross Option
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isHandicapEnabled = false }
                        .border(
                            width = if (!isHandicapEnabled) 2.dp else 1.dp,
                            color = if (!isHandicapEnabled) AugustaGreen else Color.LightGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isHandicapEnabled) AugustaGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "⚡ Gross (Scratch)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (!isHandicapEnabled) AugustaGreen else MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pure raw scores without handicap strokes",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. JUNK / DOTS & BETS
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Track Side Dots / Trash",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Greenies, Sandies, Barkies, Polies, Ferrets, Snake",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = dotsEnabled,
                            onCheckedChange = { dotsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AugustaGreen, checkedTrackColor = AugustaGreen.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bet per Dot / Point:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.5, 1.0, 2.0, 5.0).forEach { bet ->
                                val isBetSelected = selectedBet == bet
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isBetSelected) AugustaGreen else Color.LightGray.copy(alpha = 0.3f))
                                        .clickable { selectedBet = bet }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$${bet.toInt()}",
                                        color = if (isBetSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // START BUTTON
            val selectedCourse = currentBundle.course
            val selectedTee = availableTees.find { it.id == selectedTeeId } ?: availableTees.first()
            val playersToStart = availablePlayers.filter { selectedPlayerIds.contains(it.id) }
            val canStart = playersToStart.isNotEmpty() && (!isFourPlayerRequired || playersToStart.size == 4)

            Button(
                onClick = {
                    if (canStart) {
                        onStartRound(
                            selectedCourse,
                            selectedTee,
                            selectedGameId,
                            playersToStart,
                            isHandicapEnabled,
                            dotsEnabled,
                            selectedBet
                        )
                    }
                },
                enabled = canStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_start_round_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
            ) {
                Text(
                    text = if (isFourPlayerRequired && playersToStart.size != 4) "Select 4 Players" else "Tee Off & Start Scoring",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
