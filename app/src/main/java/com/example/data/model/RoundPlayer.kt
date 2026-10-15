package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "round_players",
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
    indices = [Index("roundId"), Index("playerId")]
)
data class RoundPlayer(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val roundId: String,
    val playerId: String,
    val playerName: String,
    val handicapIndexAtRound: Double,
    val courseHandicap: Int, // Calculated for this specific course & tee
    val playerSlot: Int = 0 // 0=Player A, 1=Player B, 2=Player C, 3=Player D (used for 6s rotation)
)
