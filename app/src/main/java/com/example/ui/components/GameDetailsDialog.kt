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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.GolfGameDefinition
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwaySurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsDialog(
    game: GolfGameDefinition,
    onDismiss: () -> Unit,
    onStartGame: (String) -> Unit
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AugustaGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = game.category.displayName,
                            color = AugustaGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_game_dialog")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.tagline,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player count badge
            Card(
                colors = CardDefaults.cardColors(containerColor = FairwaySurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BunkerGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (game.minPlayers == game.maxPlayers) "${game.minPlayers}" else "${game.minPlayers}-${game.maxPlayers}",
                            fontWeight = FontWeight.Bold,
                            color = AugustaGreen,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Players Required: ${if (game.minPlayers == game.maxPlayers) "${game.minPlayers} Golfers" else "${game.minPlayers} to ${game.maxPlayers} Golfers"}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = game.handicapUsage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overview Section
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Partner Rotations Section (Crucial for 6's / Hollywood)
            if (game.rotationRules.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Partner Rotation Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AugustaGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = game.rotationRules,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            // Scoring Rules
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Scoring Mechanics & Dots",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = game.scoringRules,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Dots & Payouts
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Settlements & Travelling Dots",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = game.dotsAndPayouts,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Game Button
            Button(
                onClick = {
                    onDismiss()
                    onStartGame(game.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_game_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Play ${game.shortName} Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
