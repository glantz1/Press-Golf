package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.db.CourseSeedData
import com.example.data.model.Course
import com.example.ui.theme.AugustaGreen
import com.example.ui.theme.BunkerGold
import com.example.ui.theme.FairwaySurface
import com.example.ui.viewmodel.GolfViewModel

@Composable
fun CoursesScreen(
    viewModel: GolfViewModel,
    onSelectCourseToPlay: (Course) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val isSearchingApi by viewModel.isSearchingApi.collectAsState()

    val initialBundles = remember { CourseSeedData.getInitialCourses() }

    val filteredBundles = remember(searchQuery, initialBundles) {
        if (searchQuery.isBlank()) {
            initialBundles
        } else {
            initialBundles.filter { bundle ->
                bundle.course.name.contains(searchQuery, ignoreCase = true) ||
                    bundle.course.city.contains(searchQuery, ignoreCase = true) ||
                    bundle.course.state.contains(searchQuery, ignoreCase = true)
            }
        }
    }

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
                text = "U.S. & Global Golf Course Database",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Scorecards, slopes, ratings, and 18-hole stroke handicap ranks for automatic handicap calculation.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Search Bar with API Search trigger
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("course_search_input"),
                    placeholder = { Text("Search course by name, city, state...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AugustaGreen,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                Button(
                    onClick = { viewModel.searchRemoteCourses(searchQuery) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen),
                    modifier = Modifier.testTag("api_course_search_button")
                ) {
                    if (isSearchingApi) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Search Online API", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Course List
        items(filteredBundles) { bundle ->
            CourseItemCard(
                bundle = bundle,
                onPlayClick = { onSelectCourseToPlay(bundle.course) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CourseItemCard(
    bundle: CourseSeedData.CourseBundle,
    onPlayClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val course = bundle.course
    val primaryTee = bundle.tees.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AugustaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GolfCourse,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = course.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${course.city}, ${course.state} • ${course.country}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (primaryTee != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FairwaySurface
                    ) {
                        Text(
                            text = "Rating ${primaryTee.courseRating} / Slope ${primaryTee.slopeRating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AugustaGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BunkerGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Par ${primaryTee.parTotal} • ${primaryTee.yardageTotal} yds",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BunkerGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Expanded Hole-by-Hole Scorecard Data
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                        text = "18-Hole Par & Handicap Stroke Allocations:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AugustaGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // 18-hole grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Holes 1–9", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            val f9 = bundle.holes.filter { it.holeNumber in 1..9 }
                            Text(
                                text = f9.joinToString(" ") { "H${it.holeNumber}(P${it.par}/#${it.handicapRank})" },
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Holes 10–18", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            val b9 = bundle.holes.filter { it.holeNumber in 10..18 }
                            Text(
                                text = b9.joinToString(" ") { "H${it.holeNumber}(P${it.par}/#${it.handicapRank})" },
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AugustaGreen)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Play Round at ${course.name.split(" ").take(2).joinToString(" ")}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
