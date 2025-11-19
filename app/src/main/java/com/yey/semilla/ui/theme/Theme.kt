package com.yey.semilla.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- PALETA DE COLOR CLARO (Usando los colores sugeridos anteriormente) ---
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,          // 0xFF007AFF
    secondary = SecondaryGreen,     // 0xFF34C759
    background = BackgroundLight,   // 0xFFF9F9F9
    surface = SurfaceLight,         // 0xFFFFFFFF
    error = ErrorRed,               // 0xFFFF3B30
    onPrimary = Color.White,        // El color del texto sobre el PrimaryBlue
    onSurface = TextDark,           // Color de texto principal
    onBackground = TextDark         // Color de texto principal en el fondo

    /*
     * Puedes añadir más propiedades (onSecondary, onError, etc.) si es necesario,
     * pero las anteriores son suficientes para la base.
     */
)

// --- PALETA DE COLOR OSCURO (Dark Theme) ---
// **Nota:** Debes definir estos colores (PrimaryDark, BackgroundDark, etc.)
// en tu archivo Color.kt para que no dé error.
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,          // Usamos un azul vibrante similar, o quizás un tono más oscuro si lo deseas
    secondary = SecondaryGreen,
    background = Color(0xFF1C1C1E), // Negro casi puro, común en apps modernas
    surface = Color(0xFF2C2C2E),    // Ligeramente más claro para tarjetas
    error = ErrorRed,
    onPrimary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun SemillaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Configuración del Status Bar (Barra de estado) para un aspecto moderno
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb() // Color de fondo de la barra
            // Ajusta la iconografía de la barra de estado (Modo claro/oscuro)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asumiendo que 'Typography' está definido
        content = content
    )
}