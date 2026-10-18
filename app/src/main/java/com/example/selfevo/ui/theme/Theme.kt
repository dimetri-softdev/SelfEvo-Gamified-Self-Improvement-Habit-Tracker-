package com.example.selfevo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.selfevo.util.theme.ThemeManager

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    secondary = Cyan,
    onSecondary = Black,
    background = Black,
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
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isAmoled = ThemeManager.isAmoledMode.value

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (isAmoled) dynamic.copy(background = Black, surface = Black) else dynamic
        }
        isAmoled -> DarkColorScheme.copy(background = Black, surface = Black)
        else -> DarkColorScheme.copy(background = DarkGrey, surface = DarkGrey)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
