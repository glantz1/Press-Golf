package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LightFairway,
    onPrimary = PineDark,
    primaryContainer = AugustaGreen,
    onPrimaryContainer = Color.White,
    secondary = BunkerGold,
    onSecondary = PineDark,
    secondaryContainer = BunkerGoldDark,
    onSecondaryContainer = BunkerGoldLight,
    tertiary = FlagRed,
    background = FairwaySurfaceDark,
    onBackground = TextPrimaryDark,
    surface = CardBackgroundDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1E3229),
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = AugustaGreen,
    onPrimary = Color.White,
    primaryContainer = FairwaySurface,
    onPrimaryContainer = AugustaGreen,
    secondary = BunkerGold,
    onSecondary = Color.White,
    secondaryContainer = BunkerGoldLight,
    onSecondaryContainer = BunkerGoldDark,
    tertiary = FlagRed,
    background = FairwaySurface,
    onBackground = TextPrimaryLight,
    surface = CardBackgroundLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2EFE7),
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun GolfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep golf branding cohesive by default
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
        typography = Typography,
        content = content
    )
}
