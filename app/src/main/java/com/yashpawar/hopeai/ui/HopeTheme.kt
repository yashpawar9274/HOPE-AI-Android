package com.yashpawar.hopeai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Midnight = Color(0xFF050816)
val SurfaceNavy = Color(0xFF0D1330)
val ElectricBlue = Color(0xFF39C6FF)
val HopeViolet = Color(0xFF8B6CFF)
val SoftWhite = Color(0xFFF4F6FF)
val MutedText = Color(0xFFADB6D8)

private val HopeColors = darkColorScheme(
    primary = ElectricBlue,
    secondary = HopeViolet,
    background = Midnight,
    surface = SurfaceNavy,
    onPrimary = Midnight,
    onSecondary = SoftWhite,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
)

@Composable
fun HopeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = HopeColors, content = content)
}

