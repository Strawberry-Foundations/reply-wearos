package org.strawberryfoundations.wear.replicity.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme


private val colorPalette = ColorScheme(
    primary = Color(0xFFE57373), // sanftes Rot
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB75D5D), // dunkleres Rot
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF8A65), // sanftes Orange-Rot
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB26A5E), // dunkleres Orange-Rot
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFF06292), // sanftes Pink
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB2557A), // dunkleres Pink
    onTertiaryContainer = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    background = Color.Black, // OLED-freundlich
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    outline = Color(0xFFBCAAA4), // sanftes Grau
    outlineVariant = Color(0xFF8D6E63), // dunkleres Braun-Grau
    surfaceContainerLow = Color(0xFF2D2323), // sehr dunkles Rot-Braun
    surfaceContainer = Color(0xFF2D2323),
    surfaceContainerHigh = Color(0xFF2D2323),
)

@Composable
fun GymscribeTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            dynamicColorScheme(context) ?: colorPalette
        }
        else -> colorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
