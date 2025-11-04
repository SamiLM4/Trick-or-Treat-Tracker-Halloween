package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Candy
import com.example.myapplication.data.House

//  ViewModel: faz a ponte entre a interface (Compose) e o banco (Room)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    //  Criação do banco de dados usando o Room
    private val _houses = mutableStateOf(listOf<House>())
    val houses: State<List<House>> get() = _houses

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "halloween.db"
    ).build()

    fun loadHouses() {
        viewModelScope.launch {
            val allHouses = db.houseDao().getAll()
            _houses.value = allHouses
        }
    }

    fun addHouse(name: String, address: String, scare: Int) {
        viewModelScope.launch {
            val house = House(
                name = name,
                address = address,
                scare_level = scare,
                visited_at = System.currentTimeMillis()
            )
            db.houseDao().insert(house)
            loadHouses() // atualiza a lista automaticamente
        }
    }

    //  Buscar todas as casas cadastradas
    fun getAllHouses(onResult: (List<House>) -> Unit) {
        viewModelScope.launch {
            val houses = db.houseDao().getAll()
            onResult(houses)
        }
    }

    //  Inserir um doce em uma casa específica
    fun addCandy(houseId: Int, type: String, qty: Int) {
        viewModelScope.launch {
            val candy = Candy(
                house_id = houseId,
                type = type,
                qty = qty
            )
            db.candyDao().insert(candy)
        }
    }

    //  Buscar todos os doces de uma casa
    fun getCandyByHouse(houseId: Int, onResult: (List<Candy>) -> Unit) {
        viewModelScope.launch {
            val candies = db.candyDao().getByHouse(houseId)
            onResult(candies)
        }
    }

    //  Calcular o total de doces por casa
    fun getTotalCandyByHouse(houseId: Int, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val candies = db.candyDao().getByHouse(houseId)
            val total = candies.sumOf { it.qty }
            onResult(total)
        }
    }

    // Retorna o ranking (nome da casa -> total de doces)
    fun getCandyRanking(onResult: (List<Pair<String, Int>>) -> Unit) {
        viewModelScope.launch {
            val rankingData = db.candyDao().getCandyRanking()
            val houses = db.houseDao().getAll()

            val result = rankingData.mapNotNull { r ->
                val houseName = houses.find { it.id == r.houseId }?.name
                houseName?.let { it to r.totalCandy }
            }.sortedByDescending { it.second } // Ordena do maior para o menor

            onResult(result)
        }
    }


    // 🗑️ Deletar uma casa pelo ID
    fun deleteHouse(houseId: Int, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            // Opcional: deletar doces vinculados a essa casa primeiro
            db.candyDao().deleteByHouse(houseId)

            // Agora deleta a casa
            db.houseDao().deleteById(houseId)

            loadHouses()
        }
    }


}
