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
import androidx.compose.ui.graphics.Color

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

    var start by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (start) 1f else 0.6f)

    LaunchedEffect(Unit) {
        start = true
        delay(2000)
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF66FFCC) // Cambia el fondo de pantalla
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value),
                tint = Color(0xFF000000)   // Cambia el color del icono - o blanco (0xFFFFFFFF)
            )
        }
    }

}

