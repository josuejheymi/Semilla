package com.yey.semilla

import com.yey.semilla.utils.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorTest {

    @Test
    fun `email valido retorna true`() {
        val email = "usuario@ejemplo.com"
        val resultado = Validators.isEmailValidSimple(email)
        assertTrue("El email debería ser válido", resultado)
    }

    @Test
    fun `email sin arroba retorna false`() {
        val email = "usuarioejemplo.com"
        val resultado = Validators.isEmailValidSimple(email)
        assertFalse("El email sin @ debería ser inválido", resultado)
    }

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
}