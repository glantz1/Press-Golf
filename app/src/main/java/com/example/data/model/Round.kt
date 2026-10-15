package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "rounds")
data class Round(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val teeId: String,
    val courseName: String,
    val teeName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val gameType: String = "SIXES", // SIXES, SKINS, NASSAU, WOLF, VEGAS, STABLEFORD, FOUR_BALL, BINGO_BANGO, STROKE_PLAY
    val isHandicapEnabled: Boolean = true,
    val dotsEnabled: Boolean = true,
    val betPerUnit: Double = 1.0, // Bet amount per dot/point/skin in dollars
    val status: String = "IN_PROGRESS", // IN_PROGRESS, COMPLETED
    val notes: String = ""
)
