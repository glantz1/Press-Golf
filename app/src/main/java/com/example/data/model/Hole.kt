package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "holes",
    foreignKeys = [
        ForeignKey(
            entity = Tee::class,
            parentColumns = ["id"],
            childColumns = ["teeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("teeId"), Index("courseId")]
)
data class Hole(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val teeId: String,
    val holeNumber: Int, // 1 to 18
    val par: Int, // 3, 4, 5
    val handicapRank: Int, // 1 (hardest) to 18 (easiest) stroke index
    val yardage: Int
)
