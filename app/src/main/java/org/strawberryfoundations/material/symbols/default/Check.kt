package org.strawberryfoundations.material.symbols.default

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.strawberryfoundations.material.symbols.MaterialSymbols

val MaterialSymbols.Default.Check: ImageVector
    get() {
        val current = _check
        if (current != null) return current

        return ImageVector.Builder(
            name = "Default.Check",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // m382 -373.98 333.93 -333.93 q17.05 -16.96 40.18 -16.96 t40.09 16.75 16.95 40.13 -16.95 40.34 L421.63 -253.33 q-16.77 16.96 -39.54 16.96 t-39.72 -16.96 l-176.8 -176.56 q-16.96 -16.93 -16.58 -40.12 t17.13 -40.14 q16.98 -17.2 40.25 -17.2 t40.22 17.2z
            path(
                fill = SolidColor(Color(0xFFE3E3E3)),
            ) {
                // M 382 586.02
                moveTo(x = 382.0f, y = 586.02f)
                // l 333.93 -333.93
                lineToRelative(dx = 333.93f, dy = -333.93f)
                // q 17.05 -16.96 40.18 -16.96
                quadToRelative(
                    dx1 = 17.05f,
                    dy1 = -16.96f,
                    dx2 = 40.18f,
                    dy2 = -16.96f,
                )
                // t 40.09 16.75
                reflectiveQuadToRelative(
                    dx1 = 40.09f,
                    dy1 = 16.75f,
                )
                // t 16.95 40.13
                reflectiveQuadToRelative(
                    dx1 = 16.95f,
                    dy1 = 40.13f,
                )
                // t -16.95 40.34
                reflectiveQuadToRelative(
                    dx1 = -16.95f,
                    dy1 = 40.34f,
                )
                // L 421.63 706.67
                lineTo(x = 421.63f, y = 706.67f)
                // q -16.77 16.96 -39.54 16.96
                quadToRelative(
                    dx1 = -16.77f,
                    dy1 = 16.96f,
                    dx2 = -39.54f,
                    dy2 = 16.96f,
                )
                // t -39.72 -16.96
                reflectiveQuadToRelative(
                    dx1 = -39.72f,
                    dy1 = -16.96f,
                )
                // l -176.8 -176.56
                lineToRelative(dx = -176.8f, dy = -176.56f)
                // q -16.96 -16.93 -16.58 -40.12
                quadToRelative(
                    dx1 = -16.96f,
                    dy1 = -16.93f,
                    dx2 = -16.58f,
                    dy2 = -40.12f,
                )
                // t 17.13 -40.14
                reflectiveQuadToRelative(
                    dx1 = 17.13f,
                    dy1 = -40.14f,
                )
                // q 16.98 -17.2 40.25 -17.2
                quadToRelative(
                    dx1 = 16.98f,
                    dy1 = -17.2f,
                    dx2 = 40.25f,
                    dy2 = -17.2f,
                )
                // t 40.22 17.2z
                reflectiveQuadToRelative(
                    dx1 = 40.22f,
                    dy1 = 17.2f,
                )
                close()
            }
        }.build().also { _check = it }
    }

@Suppress("ObjectPropertyName")
private var _check: ImageVector? = null
