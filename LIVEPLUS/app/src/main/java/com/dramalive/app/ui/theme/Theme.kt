package com.dramalive.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepBlack = Color(0xFF0F0F0F)
val SurfaceDark = Color(0xFF1A1A1A)
val CardDark = Color(0xFF242424)
val DeepRoyalBlue = Color(0xFF1E3A8A)
val RoyalBlueLight = Color(0xFF3B82F6)
val VibrantRed = Color(0xFFFF1744)
val AccentCyan = Color(0xFF00D9FF)
val PureWhite = Color(0xFFFFFFFF)
val SubtextGray = Color(0xFFAAAAAA)
val MutedGray = Color(0xFF707070)

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlueLight,
    secondary = DeepRoyalBlue,
    tertiary = AccentCyan,
    background = DeepBlack,
    surface = SurfaceDark,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = DeepBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
)

@Composable
fun DramaLiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
