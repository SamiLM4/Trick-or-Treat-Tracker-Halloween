package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.Candy

@Dao
interface CandyDao {

    @Insert
    suspend fun insert(candy: Candy)

    @Query("SELECT * FROM Candy WHERE house_id = :houseId")
    suspend fun getByHouse(houseId: Int): List<Candy>

    @Query("SELECT house_id AS houseId, SUM(qty) AS totalCandy FROM Candy GROUP BY house_id")
    suspend fun getCandyRanking(): List<CandyRanking>

    // 🗑️ Deleta todos os doces ligados a uma casa
    @Query("DELETE FROM Candy WHERE house_id = :houseId")
    suspend fun deleteByHouse(houseId: Int)
}
