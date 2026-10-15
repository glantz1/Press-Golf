package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "players")
data class Player(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val handicapIndex: Double, // e.g., 8.4, 18.0, 0.0, -2.1 (plus handicap)
    val email: String = "",
    val avatarColorHex: String = "#0F4D33",
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val formattedHandicap: String
        get() = if (handicapIndex < 0) "+${String.format("%.1f", -handicapIndex)}"
                else String.format("%.1f", handicapIndex)
}
