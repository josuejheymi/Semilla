package com.yey.semilla.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yey.semilla.ui.navigation.Screen
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder

//import androidx.compose.material.icons.filled.MedicalServices

/**
 * ⏳ PANTALLA DE INICIO (SplashScreen)
 *
 * Propósito: Muestra el logo o marca de la aplicación por un breve período antes de
 * redirigir al usuario a la pantalla principal de autenticación (Login).
 *
 * @param navController Controlador de navegación para redirigir la aplicación.
 */
@Composable
fun SplashScreen(navController: NavController) {

    // Animación simple de escala del logo
    var startAnimation by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(targetValue = if (startAnimation) 1f else 0f)

    // Lanzamos animación y navegación
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000) // espera 3 segundos
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true } // elimina splash de backstack
        }
    }
    // INTERFAZ DE USUARIO
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary // Fondo con el color primario de la app.
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder, // Icono del logo.
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    // Aplica la escala animada al icono.
                    .scale(scale.value),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
