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
    primary = IndigoPrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = OpticsEmerald,
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = RayAmber,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = OpticsEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFDF5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = RayAmber,
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our bespoke high-contrast optics palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
