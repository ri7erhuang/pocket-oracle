package com.pocketoracle.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Paper = Color(0xFFE9E7DE)
val PaperDeep = Color(0xFFDFDDD3)
val Ink = Color(0xFF15150F)
val InkSoft = Color(0xFF4A4A42)
val InkFaint = Color(0xFF8C8C82)

private val EInkColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    secondary = Ink,
    onSecondary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkSoft,
    outline = Ink,
    outlineVariant = InkFaint,
    error = Ink,
    onError = Paper,
)

private val mono = FontFamily.Monospace

private val eInkTypography = Typography(
    displayLarge = TextStyle(fontFamily = mono, fontWeight = FontWeight.Bold, fontSize = 48.sp),
    headlineMedium = TextStyle(fontFamily = mono, fontWeight = FontWeight.Bold, fontSize = 26.sp),
    titleLarge = TextStyle(fontFamily = mono, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = mono, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = mono, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = mono, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = mono, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = mono, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = mono, fontSize = 11.sp),
)

@Composable
fun PocketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EInkColorScheme,
        typography = eInkTypography,
        content = content,
    )
}
