package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "hole_scores",
    foreignKeys = [
        ForeignKey(
            entity = Round::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roundId"), Index("playerId"), Index(value = ["roundId", "playerId", "holeNumber"], unique = true)]
)
data class HoleScore(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val roundId: String,
    val playerId: String,
    val holeNumber: Int, // 1 to 18
    val grossScore: Int, // 0 if not played yet, else 1..15
    val netScore: Int, // Gross score minus handicap strokes received
    val strokesAllocated: Int = 0, // 0, 1, 2 strokes on this hole based on handicap rank
    val putts: Int = 0,
    val fairwayHit: String = "NA", // "HIT", "LEFT", "RIGHT", "NA" (par 3s)
    val gir: Boolean = false, // Green in regulation
    val sandShots: Int = 0,
    val penalties: Int = 0,
    val dotsAwarded: String = "" // Comma-separated dot IDs: "LOW_BALL,LOW_TOTAL,GREENIE,SANDIE"
) {
    val isCompleted: Boolean
        get() = grossScore > 0

    fun getDotsList(): List<String> = if (dotsAwarded.isBlank()) emptyList() else dotsAwarded.split(",").filter { it.isNotBlank() }

    fun withDots(dots: List<String>): HoleScore = copy(dotsAwarded = dots.distinct().joinToString(","))
}
