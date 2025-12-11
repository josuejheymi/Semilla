package com.yey.semilla.ui.screens.home

// Convierte "HH:mm" a minutos totales desde medianoche.
// Si el formato es inválido, devuelve 0.
fun minutesFromTime(time: String): Int {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour * 60 + minute
}
