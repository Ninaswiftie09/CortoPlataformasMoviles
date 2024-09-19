package com.example.totito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.totito.ui.theme.TotitoTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val size = 3
            val logica = remember { mutableStateOf(TotitoLogic(size = size)) }
            TotitoGameScreen(logica.value)
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

    Column {
        Text(text = "Turno del jugador ${if (turno.value == 1) "X" else "O"}")

        
        for (i in 0 until matriz.value.size) {
            Row {
                for (j in 0 until matriz.value[i].size) {
                    Button(onClick = {
                        if (logica.realizarMovimiento(i, j)) {
                            matriz.value = logica.obtenerMatriz()
                            turno.value = logica.obtenerTurno()
                            ganador.value = logica.verificarGanador()
                        }
                    }, enabled = matriz.value[i][j] == 0) {
                        Text(text = when (matriz.value[i][j]) {
                            1 -> "X"
                            2 -> "O"
                            else -> ""
                        })
                    }
                }
            }
        }

        // Verificar si hay ganador
        if (ganador.value != 0) {
            Text(text = "El ganador es el jugador ${if (ganador.value == 1) "X" else "O"}")
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


