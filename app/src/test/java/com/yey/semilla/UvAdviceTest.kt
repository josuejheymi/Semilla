package com.yey.semilla

import com.yey.semilla.ui.screens.home.ApiClima
import com.yey.semilla.ui.screens.home.RecomendadorSalud
import com.yey.semilla.ui.screens.home.RepositorioClima
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecomendadorTest {

    // --- PRUEBA 1: Lógica simple ---
    // Probamos que el RecomendadorSalud diga "Riesgo Bajo" si el UV es 1.0
    @Test
    fun `dado UV bajo, obtenerMensaje retorna consejo tranquilo`() {
        // 1. GIVEN (Preparamos los datos)
        val logica = RecomendadorSalud()
        val uvBajo = 1.0

        // 2. WHEN (Ejecutamos)
        val resultado = logica.obtenerMensajeUv(uvBajo)

        // 3. THEN (Verificamos)
        assertEquals("Riesgo Bajo: No necesitas protección extra.", resultado)
    }

    // --- PRUEBA 2: Lógica condicional ---
    // Probamos que diga "Riesgo Alto" si el UV es 8.5
    @Test
    fun `dado UV extremo, obtenerMensaje retorna advertencia alta`() {
        val logica = RecomendadorSalud()
        val uvAlto = 8.5

        val resultado = logica.obtenerMensajeUv(uvAlto)

        assertEquals("Riesgo Alto: ¡Usa protector solar y evita el sol!", resultado)
    }

    // --- PRUEBA 3: MockK (Simulación de API) ---
    // Simulamos que la API devuelve 6.0 sin conectarnos a internet real
    @Test
    fun `dado que API devuelve UV alto, Repositorio recomienda bloqueador`() {
        // 1. GIVEN: Creamos el "doble" de la API
        val apiFalsa = mockk<ApiClima>()

        // Entrenamos al robot: "Si te preguntan el índice, di 6.0"
        every { apiFalsa.obtenerIndiceUvActual() } returns 6.0

        // Le pasamos la API falsa al repositorio
        val repo = RepositorioClima(apiFalsa)

        // 2. WHEN: Preguntamos si debemos usar bloqueador
        val resultado = repo.deboUsarBloqueador()

        // 3. THEN: Debería ser TRUE porque 6.0 > 3.0
        assertTrue(resultado)

        // Verificamos que el repositorio realmente llamó a la API
        verify { apiFalsa.obtenerIndiceUvActual() }
    }
}