package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsGolf
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.GolfCourse
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SportsGolf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.components.FullScorecardDialog
import com.example.ui.components.GameDetailsDialog
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.NewRoundDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.GamesCatalogScreen
import com.example.ui.screens.PlayersScreen
import com.example.ui.screens.ScorecardScreen
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.GolfTheme
import com.example.ui.viewmodel.GolfTab
import com.example.ui.viewmodel.GolfViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GolfViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GolfTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val selectedGameForInfo by viewModel.selectedGameForInfo.collectAsState()
                val showNewRoundDialog by viewModel.showNewRoundDialog.collectAsState()
                val showFullScorecard by viewModel.showFullScorecardDialog.collectAsState()
                val showLeaderboard by viewModel.showLeaderboardDialog.collectAsState()

                val courses by viewModel.courses.collectAsState()
                val players by viewModel.players.collectAsState()
                val preselectedGameId by viewModel.preselectedGameId.collectAsState()

                val activeRound by viewModel.activeRound.collectAsState()
                val activeRoundPlayers by viewModel.activeRoundPlayers.collectAsState()
                val activeHoles by viewModel.activeHoles.collectAsState()
                val activeHoleScores by viewModel.activeHoleScores.collectAsState()

                // BackHandler
                BackHandler(enabled = currentTab != GolfTab.GAMES) {
                    viewModel.setTab(GolfTab.GAMES)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Golf Games & Scorecards",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.showNewRoundDialog(true) },
                                    modifier = Modifier.testTag("action_new_round")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "New Round",
                                        tint = BunkerGold
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = AugustaGreen,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val items = listOf(
                                Triple(GolfTab.GAMES, Icons.Filled.Casino, Icons.Outlined.Casino),
                                Triple(GolfTab.SCORECARD, Icons.Filled.SportsGolf, Icons.Outlined.SportsGolf),
                                Triple(GolfTab.COURSES, Icons.Filled.Map, Icons.Outlined.Map),
                                Triple(GolfTab.PLAYERS, Icons.Filled.People, Icons.Outlined.People),
                                Triple(GolfTab.ANALYTICS, Icons.Filled.Assessment, Icons.Outlined.Assessment)
                            )

                            items.forEach { (tab, filledIcon, outlinedIcon) ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.setTab(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = { Text(text = tab.title, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = AugustaGreen,
                                        selectedTextColor = AugustaGreen,
                                        indicatorColor = AugustaGreen.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            GolfTab.GAMES -> GamesCatalogScreen(
                                onSelectGameInfo = { viewModel.selectGameForInfo(it) },
                                onStartGame = { gameId ->
                                    viewModel.openNewRoundWithGame(gameId)
                                }
                            )
                            GolfTab.SCORECARD -> ScorecardScreen(
                                viewModel = viewModel,
                                onStartNewRoundClick = { viewModel.showNewRoundDialog(true) }
                            )
                            GolfTab.COURSES -> CoursesScreen(
                                viewModel = viewModel,
                                onSelectCourseToPlay = {
                                    viewModel.openNewRoundWithGame("SIXES")
                                }
                            )
                            GolfTab.PLAYERS -> PlayersScreen(viewModel = viewModel)
                            GolfTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        }
                    }

                    // Game Details Dialog (Modal Bottom Sheet)
                    if (selectedGameForInfo != null) {
                        GameDetailsDialog(
                            game = selectedGameForInfo!!,
                            onDismiss = { viewModel.selectGameForInfo(null) },
                            onStartGame = { gameId ->
                                viewModel.selectGameForInfo(null)
                                viewModel.openNewRoundWithGame(gameId)
                            }
                        )
                    }

                    // New Round Setup Dialog
                    if (showNewRoundDialog) {
                        NewRoundDialog(
                            courses = courses,
                            availablePlayers = players,
                            preselectedGameId = preselectedGameId,
                            onDismiss = { viewModel.showNewRoundDialog(false) },
                            onStartRound = { course, tee, gameType, selectedPlayers, isHandicapEnabled, dotsEnabled, betPerUnit ->
                                viewModel.startNewRound(
                                    course = course,
                                    tee = tee,
                                    gameType = gameType,
                                    selectedPlayers = selectedPlayers,
                                    isHandicapEnabled = isHandicapEnabled,
                                    dotsEnabled = dotsEnabled,
                                    betPerUnit = betPerUnit
                                )
                            }
                        )
                    }

                    // 18-Hole Full Grid Scorecard Dialog
                    if (showFullScorecard && activeRound != null) {
                        FullScorecardDialog(
                            round = activeRound!!,
                            players = activeRoundPlayers,
                            holes = activeHoles,
                            scores = activeHoleScores,
                            onDismiss = { viewModel.showFullScorecardDialog(false) }
                        )
                    }

                    // Leaderboard Dialog
                    if (showLeaderboard && activeRound != null) {
                        LeaderboardDialog(
                            round = activeRound!!,
                            standings = viewModel.getPlayerStandings(),
                            skinsResults = viewModel.getSkinsResults(),
                            onDismiss = { viewModel.showLeaderboardDialog(false) }
                        )
                    }
                }
            }
        }
    }
}
