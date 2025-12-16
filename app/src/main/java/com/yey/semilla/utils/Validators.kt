package com.yey.semilla.utils

import android.util.Patterns

object Validators {

    // ================================================================
    // 🟢 VALIDACIONES COMPARTIDAS (Sirven para Login y Register)
    // ================================================================

    fun isEmailValid(email: String): Boolean {
        // Verifica que no esté vacío Y que tenga formato de correo (a@b.c)
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // ================================================================
    // 🔵 VALIDACIONES DE REGISTRO (Más estrictas)
    // ================================================================

    fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }

    // En el registro exigimos seguridad (ej: mínimo 6 caracteres)
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun arePhysicalStatsValid(peso: String, altura: String): Boolean {
        // Valida que sean números y mayores a 0
        val p = peso.toDoubleOrNull() ?: 0.0
        val a = altura.toDoubleOrNull() ?: 0.0
        return p > 0 && a > 0
    }

    // ================================================================
    // 🟠 VALIDACIONES DE LOGIN (Más flexibles)
    // ================================================================

    // En el login, a veces solo queremos ver si escribió algo.
    // (Aunque podrías usar isPasswordValid si quieres ser estricto también aquí)
    fun isPasswordNotEmpty(password: String): Boolean {
        return password.isNotEmpty()
    }
}