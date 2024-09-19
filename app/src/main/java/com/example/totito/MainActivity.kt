package com.example.totito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.totito.ui.theme.TotitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TotitoApp()
        }
    }
}

@Composable
fun TotitoApp() {
    var startGame by remember { mutableStateOf(false) }
    var boardSize by remember { mutableStateOf(3) }

    if (startGame) {
        // Mostrar pantalla del juego
        val logica = remember { mutableStateOf(TotitoLogic(size = boardSize)) }
        TotitoGameScreen(logica.value)
    } else {
        // Mostrar pantalla de inicio
        StartScreen(onStartGame = { size ->
            boardSize = size
            startGame = true
        })
    }
}

@Composable
fun StartScreen(onStartGame: (Int) -> Unit) {
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var boardSize by remember { mutableStateOf(3) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Bienvenido a Totito", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Nombre del Jugador 1:")
        TextField(value = player1Name, onValueChange = { player1Name = it })

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Nombre del Jugador 2:")
        TextField(value = player2Name, onValueChange = { player2Name = it })

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tamaño del Tablero:")
        Row {
            Button(onClick = { boardSize = 3 }) { Text("3x3") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { boardSize = 4 }) { Text("4x4") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { boardSize = 5 }) { Text("5x5") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onStartGame(boardSize) }) {
            Text("Iniciar Juego")
        }
    }
}

class TotitoLogic(private val size: Int = 3) {
    private val matriz: Array<IntArray> = Array(size) { IntArray(size) { 0 } }
    private var turnoActual = 1

    fun realizarMovimiento(x: Int, y: Int): Boolean {
        if (matriz[x][y] == 0) {
            matriz[x][y] = turnoActual
            cambiarTurno()
            return true
        }
        return false
    }

    fun obtenerTurno(): Int {
        return turnoActual
    }

    private fun cambiarTurno() {
        turnoActual = if (turnoActual == 1) 2 else 1
    }

    fun verificarGanador(): Int {
        if (verificarFilas() || verificarColumnas() || verificarDiagonales()) {
            return if (turnoActual == 1) 2 else 1
        }
        return 0
    }

    private fun verificarFilas(): Boolean {
        for (i in 0 until size) {
            if (matriz[i].all { it == matriz[i][0] && it != 0 }) {
                return true
            }
        }
        return false
    }

    private fun verificarColumnas(): Boolean {
        for (i in 0 until size) {
            if ((0 until size).all { matriz[it][i] == matriz[0][i] && matriz[it][i] != 0 }) {
                return true
            }
        }
        return false
    }

    private fun verificarDiagonales(): Boolean {
        val diagonalPrincipal = (0 until size).all { matriz[it][it] == matriz[0][0] && matriz[it][it] != 0 }
        val diagonalSecundaria = (0 until size).all { matriz[it][size - it - 1] == matriz[0][size - 1] && matriz[it][size - it - 1] != 0 }

        return diagonalPrincipal || diagonalSecundaria
    }

    fun reiniciarJuego() {
        for (i in 0 until size) {
            for (j in 0 until size) {
                matriz[i][j] = 0
            }
        }
        turnoActual = 1
    }

    fun obtenerMatriz(): Array<IntArray> {
        return matriz
    }
}

@Composable
fun TotitoGameScreen(logica: TotitoLogic) {
    val matriz = remember { mutableStateOf(logica.obtenerMatriz()) }
    val turno = remember { mutableStateOf(logica.obtenerTurno()) }
    val ganador = remember { mutableStateOf(0) }

    Scaffold(


        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Turno del jugador ${if (turno.value == 1) "X" else "O"}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                for (i in 0 until matriz.value.size) {
                    Row {
                        for (j in 0 until matriz.value[i].size) {
                            val buttonColor = when (matriz.value[i][j]) {
                                1 -> ButtonDefaults.buttonColors(containerColor = Color.Red)
                                2 -> ButtonDefaults.buttonColors(containerColor = Color.Blue)
                                else -> ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            }

                            Button(
                                onClick = {
                                    if (logica.realizarMovimiento(i, j)) {
                                        matriz.value = logica.obtenerMatriz()
                                        turno.value = logica.obtenerTurno()
                                        ganador.value = logica.verificarGanador()
                                    }
                                },
                                enabled = matriz.value[i][j] == 0,
                                colors = buttonColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = when (matriz.value[i][j]) {
                                        1 -> "X"
                                        2 -> "O"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (ganador.value != 0) {
                    Text(
                        text = "El ganador es el jugador ${if (ganador.value == 1) "X" else "O"}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        logica.reiniciarJuego()
                        matriz.value = logica.obtenerMatriz()
                        ganador.value = 0
                    }) {
                        Text("Reiniciar Juego")
                    }
                }
            }
        }
    )
}
