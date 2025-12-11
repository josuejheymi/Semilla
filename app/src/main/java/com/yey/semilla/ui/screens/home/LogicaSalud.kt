package com.yey.semilla.ui.screens.home

// Clase 1: Lógica pura
// Esta clase decide qué mensaje mostrar según el número de UV
class RecomendadorSalud {
    fun obtenerMensajeUv(indiceUv: Double): String {
        return when {
            indiceUv < 0 -> "Error: Valor inválido"
            indiceUv <= 2.0 -> "Riesgo Bajo: No necesitas protección extra."
            indiceUv <= 5.0 -> "Riesgo Moderado: Usa sombrero."
            else -> "Riesgo Alto: ¡Usa protector solar y evita el sol!"
        }
    }
}

// Interfaz para la API (Contrato)
interface ApiClima {
    fun obtenerIndiceUvActual(): Double
}

// Clase 2: Repositorio
// Esta clase usa la API para tomar decisiones
class RepositorioClima(private val api: ApiClima) {
    fun deboUsarBloqueador(): Boolean {
        val uvActual = api.obtenerIndiceUvActual()
        // Si el índice es mayor a 3, devuelve verdadero (usar bloqueador)
        return uvActual > 3.0
    }
}