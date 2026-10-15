package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val city: String,
    val state: String,
    val country: String = "USA",
    val holesCount: Int = 18,
    val courseType: String = "Public", // Resort, Private, Public, Championship
    val architect: String = "",
    val website: String = "",
    val isCustom: Boolean = false,
    val apiId: String? = null
)
