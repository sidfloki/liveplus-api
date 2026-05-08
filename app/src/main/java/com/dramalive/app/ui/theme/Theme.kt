package com.dramalive.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Netflix-inspired Premium Dark Colors
val NetflixRed = Color(0xFFE50914)
val NetflixDarkRed = Color(0xFFB20710)
val DeepBlack = Color(0xFF0A0A0A)
val SurfaceDark = Color(0xFF141414)
val CardDark = Color(0xFF1C1C1C)
val CardHover = Color(0xFF2A2A2A)
val AccentGold = Color(0xFFFFD700)
val AccentCyan = Color(0xFF00D9FF)
val PureWhite = Color(0xFFFFFFFF)
val SubtextGray = Color(0xFFB3B3B3)
val MutedGray = Color(0xFF707070)
val DimGray = Color(0xFF404040)
val OverlayBlack = Color(0xCC000000)
val TransparentBlack = Color(0x80000000)

private val DarkColorScheme = darkColorScheme(
    primary = NetflixRed,
    secondary = NetflixDarkRed,
    tertiary = AccentGold,
    background = DeepBlack,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = DeepBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    onSurfaceVariant = SubtextGray,
    error = NetflixRed,
    outline = DimGray
)

private val NetflixTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.sp
    )
)

@Composable
fun DramaLiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = NetflixTypography,
        content = content
    )
}
