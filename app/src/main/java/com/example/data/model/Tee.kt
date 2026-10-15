package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tees",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class Tee(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val name: String, // e.g., "Championship (Black)", "Tournament (Blue)", "Member (White)", "Forward (Red)"
    val colorHex: String = "#1E88E5",
    val courseRating: Double, // e.g. 74.2
    val slopeRating: Int, // e.g. 138 (standard is 113)
    val parTotal: Int = 72,
    val yardageTotal: Int = 6700,
    val gender: String = "M" // M, F, or All
)
