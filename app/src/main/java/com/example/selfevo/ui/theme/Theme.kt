package com.example.selfevo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.selfevo.util.theme.ThemeManager

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    secondary = Cyan,
    onSecondary = Black,
    background = DarkGrey,
    onBackground = TextPrimary,
    surface = SurfaceVariant,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD4AF37),
    onPrimary = TextPrimary,
    secondary = Cyan,
    onSecondary = TextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun SelfEvoTheme(
    content: @Composable () -> Unit
) {
    val isDarkMode = ThemeManager.isDarkMode.value
    val isAmoled = ThemeManager.isAmoledMode.value

    val colorScheme = if (isDarkMode) {
        if (isAmoled) {
            DarkColorScheme.copy(background = Black, surface = Black, surfaceVariant = SurfaceVariant)
        } else {
            DarkColorScheme.copy(background = DarkGrey, surface = DarkGrey, surfaceVariant = SurfaceVariant)
        }
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
