package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameCategory
import com.example.data.model.GolfGameDefinition
import com.example.data.model.GolfGamesCatalog
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwayGreen
import com.example.ui.theme.FairwaySurface
import com.example.ui.theme.PineDark

@Composable
fun GamesCatalogScreen(
    onSelectGameInfo: (GolfGameDefinition) -> Unit,
    onStartGame: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }

    val filteredGames = remember(searchQuery, selectedCategory) {
        GolfGamesCatalog.ALL_GAMES.filter { game ->
            val matchesSearch = searchQuery.isBlank() ||
                game.name.contains(searchQuery, ignoreCase = true) ||
                game.tagline.contains(searchQuery, ignoreCase = true) ||
                game.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || game.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AugustaGreen, FairwayGreen, PineDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BunkerGold
                        ) {
                            Text(
                                text = "GOLF GAMES DIRECTORY",
                                color = PineDark,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BunkerGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "All Golf Games & Scoring Formats",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Every single, team, and partner-rotation game you can play. Supports automatic handicap calculations, 6's travelling dots, skins carryovers, and US course scorecards.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_games_field"),
                placeholder = { Text("Search games (e.g. 6's, Skins, Wolf, Nassau, Vegas...)") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AugustaGreen,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Games (${GolfGamesCatalog.ALL_GAMES.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AugustaGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(GameCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text(cat.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AugustaGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Game Cards
        items(filteredGames) { game ->
            GolfGameCard(
                game = game,
                onInfoClick = { onSelectGameInfo(game) },
                onStartClick = { onStartGame(game.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GolfGameCard(
    game: GolfGameDefinition,
    onInfoClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val isFeatured = game.id == "SIXES"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInfoClick() }
            .testTag("game_card_${game.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isFeatured) FairwaySurface else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFeatured) 3.dp else 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Badge & Category
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
                            .background(if (isFeatured) AugustaGreen else BunkerGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.shortName.take(3),
                            color = if (isFeatured) Color.White else AugustaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = game.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = game.category.displayName,
                            fontSize = 11.sp,
                            color = AugustaGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (game.minPlayers == game.maxPlayers) "${game.minPlayers}P" else "${game.minPlayers}-${game.maxPlayers}P",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text = game.tagline,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Snippet
            Text(
                text = game.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 3
            )

            if (isFeatured) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AugustaGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "✨ Featured Rotation: 1-6 (A&B vs C&D) • 7-12 (A&C vs B&D) • 13-18 (A&D vs B&C) with Travelling Dots!",
                        color = AugustaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Rules & Rotations", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Play Game", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
