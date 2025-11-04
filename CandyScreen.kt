package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.data.Candy


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandyScreen(viewModel: MainViewModel, houseId: Int, navController: NavHostController) {

    var type by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var candies by remember { mutableStateOf(listOf<Candy>()) }
    var total by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf("") }

    // Função para atualizar candies e total
    fun refreshCandies() {
        viewModel.getCandyByHouse(houseId) { candies = it }
        viewModel.getTotalCandyByHouse(houseId) { total = it }
    }

    // Carrega doces iniciais
    LaunchedEffect(Unit) {
        refreshCandies()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_candys),
            contentDescription = "Fundo de doces",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A1B9A).copy(alpha = 0.9f)
                ),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("← Voltar", color = Color(0xFFFFC107))
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "🎃 Registro de Doces Assustadores 🍬",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                color = Color(0xFF36361C)
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E2B5F).copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    TextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Tipo de doce 🍭") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0x80000000),
                            focusedTextColor = Color(0xFFFF9800),
                            unfocusedTextColor = Color(0xFFFF9800),
                            cursorColor = Color(0xFFFF9800),
                            focusedLabelColor = Color(0xFFFF9800),
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = qty,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() } || value.isEmpty()) {
                                qty = value
                                errorMessage = ""
                            }
                        },
                        label = { Text("Quantidade 🎃") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0x80000000),
                            focusedTextColor = Color(0xFFFF9800),
                            unfocusedTextColor = Color(0xFFFF9800),
                            cursorColor = Color(0xFFFF9800),
                            focusedLabelColor = Color(0xFFFF9800),
                            unfocusedLabelColor = Color.White
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val qtyInt = qty.toIntOrNull()
                            if (type.isEmpty() || qtyInt == null) {
                                errorMessage = "Digite um tipo e uma quantidade válida!"
                                return@Button
                            }

                            // Usando callback para atualizar a lista após inserção
                            viewModel.addCandy(houseId, type, qtyInt)
                            // Atualiza imediatamente após inserir (ViewModel já executa em coroutine)
                            refreshCandies()

                            type = ""
                            qty = ""
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
                    ) {
                        Text("Adicionar Doce 🎃", color = Color.Black)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Total de doces: $total 🍬",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFFC107)
                    )

                    Spacer(Modifier.height(8.dp))

                    LazyColumn {
                        items(candies) { candy ->
                            Text("- 👻 ${candy.type}: ${candy.qty} unidades", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
