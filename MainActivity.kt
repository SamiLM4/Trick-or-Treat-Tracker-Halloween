package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {

                composable("home") {

                    AppScreen(navController, viewModel)
                }

                composable("candy/{houseId}") { backStackEntry ->

                    val houseId = backStackEntry.arguments?.getString("houseId")?.toInt() ?: 0

                    CandyScreen(viewModel, houseId, navController)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(navController: NavHostController, viewModel: MainViewModel) {

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var scare by remember { mutableStateOf("") }

    var ranking by remember { mutableStateOf(listOf<Pair<String, Int>>()) }

    // Observa diretamente o State do ViewModel
    val houses by viewModel.houses

    // Carrega casas iniciais
    LaunchedEffect(Unit) {
        viewModel.loadHouses()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background_halloween),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {
                Text(
                    text = "🏠 Trick-or-Treat Tracker",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFFA500)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // TextFields
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da casa", color = Color.White) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0x80000000),
                        focusedTextColor = Color(0xFFFF9800),
                        unfocusedTextColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        unfocusedLabelColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço", color = Color.White) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0x80000000),
                        focusedTextColor = Color(0xFFFF9800),
                        unfocusedTextColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        unfocusedLabelColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = scare,
                    onValueChange = { value ->
                        val newValue = when {
                            value.isEmpty() -> ""
                            value.toIntOrNull() in 0..5 -> value
                            else -> scare
                        }
                        scare = newValue
                    },
                    label = { Text("Nível de susto (0–5)", color = Color.White) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0x80000000),
                        focusedTextColor = Color(0xFFFF9800),
                        unfocusedTextColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        unfocusedLabelColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val scareInt = scare.toIntOrNull()
                        if (name.isNotEmpty() && scareInt != null) {
                            viewModel.addHouse(name, address, scareInt)
                            name = ""
                            address = ""
                            scare = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Adicionar casa", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { Text("Casas cadastradas:", color = Color(0xFFFFA500)) }

            // Lista de casas observa diretamente o State do ViewModel
            items(houses) { house ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x80000000))
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${house.name} (${house.scare_level}/5 de susto)", color = Color.White)
                        Text("Endereço: ${house.address ?: "—"}", color = Color.LightGray)
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { navController.navigate("candy/${house.id}") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("Ver doces 🍬", color = Color.Black)
                        }
                        Button(
                            onClick = { viewModel.deleteHouse(house.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Deletar casa 🗑️", color = Color.White)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.getCandyRanking { ranking = it } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Mostrar ranking 🎃", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Casas mais generosas 🍭:", color = Color.White)
            }

            items(ranking) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .background(Color(0x80000000), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏚️ ${item.first}: ${item.second} doces",
                        color = Color(0xFFF16A07),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (ranking.isEmpty()) {
                item {
                    Text("Nenhum doce cadastrado ainda 🎃", color = Color.LightGray)
                }
            }
        }
    }
}
