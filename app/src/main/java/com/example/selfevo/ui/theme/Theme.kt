package com.example.selfevo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

import com.example.selfevo.util.theme.ThemeManager

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    secondary = Cyan,
    onSecondary = Black,
    background = Black, // This will be overridden
    onBackground = TextPrimary,
    surface = DarkGrey,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Black
)

@Composable
fun SelfEvoTheme(
    content: @Composable () -> Unit
) {
    val isAmoled = ThemeManager.isAmoledMode.value
    val colorScheme = if (isAmoled) {
        DarkColorScheme.copy(background = Black, surface = Black)
    } else {
        DarkColorScheme.copy(background = DarkGrey, surface = DarkGrey)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
