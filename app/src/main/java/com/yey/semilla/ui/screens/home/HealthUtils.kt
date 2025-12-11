package com.yey.semilla.ui.screens.home

import kotlin.math.pow

// Calcula IMC usando altura en centímetros
fun calculateImc(pesoKg: Double, alturaCm: Double): Double {
    val alturaMetros = alturaCm / 100.0
    return pesoKg / alturaMetros.pow(2)
}

// Devuelve la categoría de IMC con los mismos cortes que usas en la UI
fun imcCategory(imc: Double): String = when {
    imc < 18.5 -> "Bajo peso"
    imc < 25.0 -> "Normal"
    imc < 30.0 -> "Sobrepeso"
    else -> "Obesidad"
}
