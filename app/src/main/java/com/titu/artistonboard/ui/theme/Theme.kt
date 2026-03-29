package com.titu.artistonboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = AccentGold,
    secondary = Mint,
    tertiary = Rose,
    background = DeepInk,
    surface = CardSurface,
    onPrimary = Color(0xFF1B1B1B),
    onSecondary = Color(0xFF0B1410),
    onBackground = Color(0xFFF5F2FF),
    onSurface = Color(0xFFF5F2FF)
)

@Composable
fun ArtistOnboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}