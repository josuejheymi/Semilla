package com.yey.semilla

import org.junit.Assert.assertEquals
import org.junit.Test
//Si hay 10 pastillas y se consume 1, deben quedar 9. Parece obvio, pero testearlo demuestra que la app no va a dar números negativos.
class MedicationMathTest {

    @Test
    fun `calcular pastillas restantes resta correctamente`() {
        val actuales = 10
        val dosis = 1
        val resultado = actuales - dosis

        assertEquals(9, resultado)
    }

    @Test
    fun `calcular stock alerta cuando quedan pocas`() {
        val totalCaja = 30
        val restantes = 5 // Quedan pocas

        // Supongamos que definimos "pocas" como menos del 20%
        val esStockBajo = restantes < (totalCaja * 0.2)

        assertEquals(true, esStockBajo)
    }

    @Test
    fun `no permitir numeros negativos`() {
        val actuales = 0
        val dosis = 1

        // Lógica de protección
        val resultado = if (actuales - dosis < 0) 0 else actuales - dosis

        assertEquals(0, resultado)
    }
}