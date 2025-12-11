package com.yey.semilla


import com.yey.semilla.ui.screens.home.minutesFromTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeParsingTest {

    @Test
    fun `minutesFromTime convierte hora y minutos correctamente`() {
        val result = minutesFromTime("08:30")
        assertEquals(8 * 60 + 30, result)
    }

    @Test
    fun `minutesFromTime funciona con medianoche`() {
        val result = minutesFromTime("00:05")
        assertEquals(5, result)
    }

    @Test
    fun `minutesFromTime devuelve 0 cuando el formato es invalido`() {
        val result = minutesFromTime("no-es-hora")
        assertEquals(0, result)
    }
}
