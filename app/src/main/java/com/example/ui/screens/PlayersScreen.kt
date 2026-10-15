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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.Player
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwaySurface
import com.example.ui.viewmodel.GolfViewModel

@Composable
fun PlayersScreen(viewModel: GolfViewModel) {
    val players by viewModel.players.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPlayer by remember { mutableStateOf<Player?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Golfers & WHS Handicaps",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Player roster and handicap indices. Handicaps are automatically adjusted to Course Handicaps on every round.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(players) { player ->
                PlayerItemCard(
                    player = player,
                    onEditClick = { editingPlayer = player }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_golfer_fab"),
            containerColor = AugustaGreen,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Golfer")
        }
    }

    if (showAddDialog) {
        AddPlayerDialog(
            onDismiss = { showAddDialog = false },
            onAddPlayer = { name, hdcp ->
                viewModel.addPlayer(name, hdcp)
                showAddDialog = false
            }
        )
    }

    if (editingPlayer != null) {
        EditHandicapDialog(
            player = editingPlayer!!,
            onDismiss = { editingPlayer = null },
            onSaveHandicap = { newHdcp ->
                viewModel.updatePlayerHandicap(editingPlayer!!, newHdcp)
                editingPlayer = null
            }
        )
    }
}

@Composable
fun PlayerItemCard(
    player: Player,
    onEditClick: () -> Unit
) {
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AugustaGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (player.email.isNotBlank()) player.email else "Registered Golfer",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FairwaySurface
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "HDCP INDEX",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = player.formattedHandicap,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = AugustaGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Handicap", tint = BunkerGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onAddPlayer: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var handicapText by remember { mutableStateOf("12.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Golfer", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Golfer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = handicapText,
                    onValueChange = { handicapText = it },
                    label = { Text("Handicap Index (e.g. 8.4, 0.0)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hdcp = handicapText.toDoubleOrNull() ?: 10.0
                    if (name.isNotBlank()) {
                        onAddPlayer(name, hdcp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
            ) {
                Text("Add Golfer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditHandicapDialog(
    player: Player,
    onDismiss: () -> Unit,
    onSaveHandicap: (Double) -> Unit
) {
    var handicapText by remember { mutableStateOf(player.handicapIndex.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Handicap Index", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Golfer: ${player.name}", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = handicapText,
                    onValueChange = { handicapText = it },
                    label = { Text("WHS Handicap Index") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hdcp = handicapText.toDoubleOrNull() ?: player.handicapIndex
                    onSaveHandicap(hdcp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
