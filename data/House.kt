package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "house")
data class House(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String?,
    val scare_level: Int,
    val visited_at: Long = System.currentTimeMillis()
)
