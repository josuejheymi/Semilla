package com.yey.semilla

import com.yey.semilla.ui.screens.home.calculateImc
import com.yey.semilla.ui.screens.home.imcCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class ImcUtilsTest {

    @Test
    fun `calculateImc calcula un IMC aproximado correcto`() {
        // Persona de 68 kg y 1,75 m -> IMC ~ 22.2
        val imc = calculateImc(pesoKg = 68.0, alturaCm = 175.0)

        assertEquals(22.2, imc, 0.2) // delta 0.2 para tolerancia
    }

    @Test
    fun `imcCategory devuelve Normal para IMC 23`() {
        val category = imcCategory(23.0)
        assertEquals("Normal", category)
    }

    @Test
    fun `imcCategory devuelve Obesidad para IMC 32`() {
        val category = imcCategory(32.0)
        assertEquals("Obesidad", category)
    }
}
