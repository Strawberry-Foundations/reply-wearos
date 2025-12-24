package org.strawberryfoundations.wear.replicity.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.wear.replicity.R
import org.strawberryfoundations.wear.replicity.theme.ascenderHeight
import org.strawberryfoundations.wear.replicity.theme.counterWidth

class GoogleSansFlex {
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    object HeadlineSmallVFConfig {
        const val WEIGHT = 1000
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 515
    }

    @OptIn(ExperimentalTextApi::class)
    val displayLargeFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                    FontVariation.slant(DisplayLargeVFConfig.SLANT),
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val titleMediumFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    settings = arrayOf(
                        FontVariation.weight(1000),
                        FontVariation.width(100f),
                        FontVariation.grade(0),
                        FontVariation.Setting("ROND", 100f)
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val displayFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    settings = arrayOf(
                        FontVariation.weight(1000),
                        FontVariation.width(110f),
                        FontVariation.grade(0),
                        FontVariation.Setting("ROND", 100f)
                    )
                )
            )
        )


    @OptIn(ExperimentalTextApi::class)
    val headlineSmallFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(HeadlineSmallVFConfig.WEIGHT),
                    FontVariation.width(HeadlineSmallVFConfig.WIDTH),
                    FontVariation.slant(HeadlineSmallVFConfig.SLANT),
                    FontVariation.Setting("ROND", 100f)
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val labelLargeFontFamily = FontFamily(
        Font(
            R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(600),
                FontVariation.width(100f),
                FontVariation.Setting("ROND", 100f)
            )
        )
    )

    @OptIn(ExperimentalTextApi::class)
    val labelFontFamily = FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    settings = arrayOf(
                        FontVariation.weight(800),
                        FontVariation.width(100f),
                        FontVariation.grade(0),
                        FontVariation.Setting("ROND", 100f)
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val arcFontFamily = FontFamily(
        Font(
            R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                settings = arrayOf(
                    FontVariation.weight(900),
                    FontVariation.width(105f),
                    FontVariation.grade(0),
                    FontVariation.Setting("ROND", 100f)
                )
            )
        )
    )
}