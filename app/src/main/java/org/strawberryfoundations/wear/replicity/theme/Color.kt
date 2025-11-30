package org.strawberryfoundations.wear.replicity.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun hexToColor(hex: String): Color {
    return runCatching { Color(hex.toColorInt()) }.getOrDefault(Color.LightGray)
}

fun darkenColor(color: Color, factor: Float = 0.85f): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

fun contrastColor(bg: Color): Color {
    val r = bg.red
    val g = bg.green
    val b = bg.blue
    val lum = 0.299f * r + 0.587f * g + 0.114f * b
    return if (lum < 0.5f) Color.White else Color.Black
}