package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.House

@Dao
interface HouseDao {
    @Insert
    suspend fun insert(house: House)

    @Query("SELECT * FROM House")
    suspend fun getAll(): List<House>

    // 🗑️ Deleta uma casa específica
    @Query("DELETE FROM House WHERE id = :houseId")
    suspend fun deleteById(houseId: Int)
}
