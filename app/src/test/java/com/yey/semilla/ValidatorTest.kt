package com.yey.semilla

import com.yey.semilla.utils.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorTest {

    // --- TEST DE EMAIL ---
    @Test
    fun `email valido retorna true`() {
        val email = "usuario@ejemplo.com"
        // Ahora llamamos a la función real 'isEmailValid'
        val resultado = Validators.isEmailValid(email)
        assertTrue("El email debería ser válido", resultado)
    }

    @Test
    fun `email sin arroba retorna false`() {
        val email = "usuarioejemplo.com"
        val resultado = Validators.isEmailValid(email)
        assertFalse("El email sin @ debería ser inválido", resultado)
    }

    @Test
    fun `email vacio retorna false`() {
        val email = ""
        val resultado = Validators.isEmailValid(email)
        assertFalse("El email vacío debería ser inválido", resultado)
    }

    // --- TEST DE CONTRASEÑA ---
    @Test
    fun `password corta retorna false`() {
        val pass = "12345" // Menos de 6
        val resultado = Validators.isPasswordValid(pass)
        assertFalse("La contraseña corta debería ser inválida", resultado)
    }

    @Test
    fun `password correcta retorna true`() {
        val pass = "123456"
        val resultado = Validators.isPasswordValid(pass)
        assertTrue("La contraseña correcta debería ser válida", resultado)
    }

    // --- TEST DE DATOS FÍSICOS (NUEVO) ---
    @Test
    fun `peso y altura validos retornan true`() {
        val peso = "70.5"
        val altura = "175"
        val resultado = Validators.arePhysicalStatsValid(peso, altura)
        assertTrue("Peso y altura numéricos deberían ser válidos", resultado)
    }

    @Test
    fun `peso con letras retorna false`() {
        val peso = "70kg" // Esto fallará al convertir a Double
        val altura = "175"
        val resultado = Validators.arePhysicalStatsValid(peso, altura)
        assertFalse("No se deberían aceptar letras en el peso", resultado)
    }

    @Test
    fun `altura cero o negativa retorna false`() {
        val peso = "70"
        val altura = "0"
        val resultado = Validators.arePhysicalStatsValid(peso, altura)
        assertFalse("La altura no puede ser 0", resultado)
    }

    // --- TEST DE NOMBRE (NUEVO) ---
    @Test
    fun `nombre vacio retorna false`() {
        val nombre = "   " // Solo espacios
        val resultado = Validators.isNameValid(nombre)
        assertFalse("El nombre en blanco no es válido", resultado)
    }
}