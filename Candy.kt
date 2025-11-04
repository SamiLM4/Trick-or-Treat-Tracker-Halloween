package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candy")
data class Candy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val house_id: Int,
    val type: String,
    val qty: Int
)




