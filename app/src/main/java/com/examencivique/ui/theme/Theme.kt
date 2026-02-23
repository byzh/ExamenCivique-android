package com.examencivique.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val FrenchBlue = Color(0xFF002494)
val FrenchRed  = Color(0xFFED2939)

private val LightColorScheme = lightColorScheme(
    primary = FrenchBlue,
    onPrimary = Color.White,
    secondary = FrenchRed,
    onSecondary = Color.White,
    surface = Color.White,
    background = Color(0xFFF5F5F5)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF809FFF),
    onPrimary = Color(0xFF001354),
    secondary = Color(0xFFFF8A8A),
    onSecondary = Color(0xFF680014),
)

@Composable
fun ExamenCiviqueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
