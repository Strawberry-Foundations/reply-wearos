package org.strawberryfoundations.wear.replicity.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme


private val colorPalette = ColorScheme(
    primary = Color(0xFFB39DDB),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF826F9A),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF64B5F6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF64B5F6),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFB39DDB),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB39DDB),
    onTertiaryContainer = Color.Black,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFCF6679),
    onErrorContainer = Color.Black,
    outline = Color.Gray,
    outlineVariant = Color.DarkGray,
    surfaceContainerLow = Color(0xFF35363A),
    surfaceContainer = Color(0xFF35363A),
    surfaceContainerHigh = Color(0xFF35363A),
)

@Composable
fun ReplicityTheme(
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
