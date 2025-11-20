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
    primary = Turquoise500,
    secondary = Turquoise700,
    background = Turquoise50,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF00332F),
    onSurface = Color(0xFF00332F)
)

private val DarkColorScheme = darkColorScheme(
    primary = TurquoiseDark,
    onPrimary = TextLight,

    secondary = MintSoft,

    background = BackgroundDark,
    onBackground = TextLight,

    surface = SurfaceDark,
    onSurface = TextLight,

    error = ErrorRed
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