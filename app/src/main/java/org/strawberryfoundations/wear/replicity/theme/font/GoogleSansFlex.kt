package org.strawberryfoundations.wear.replicity.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.wear.replicity.R

class GoogleSansFlex {
    object DisplayLargeVFConfig {
        const val WEIGHT = 950
        const val WIDTH = 120f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 500
    }

    object TitleMediumVFConfig {
        const val WEIGHT = 600
        const val WIDTH = 110f
        const val SLANT = 0f
        const val ASCENDER_HEIGHT = 800f
        const val COUNTER_WIDTH = 475
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
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.ascenderHeight(
                        DisplayLargeVFConfig.ASCENDER_HEIGHT
                    ),
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.counterWidth(
                        DisplayLargeVFConfig.COUNTER_WIDTH
                    )
                )
            )
        )

    @OptIn(ExperimentalTextApi::class)
    val titleMediumFontFamily =
        FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(TitleMediumVFConfig.WEIGHT),
                    FontVariation.width(TitleMediumVFConfig.WIDTH),
                    FontVariation.slant(TitleMediumVFConfig.SLANT),
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.ascenderHeight(
                        TitleMediumVFConfig.ASCENDER_HEIGHT
                    ),
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.counterWidth(
                        TitleMediumVFConfig.COUNTER_WIDTH
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
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.ascenderHeight(
                        HeadlineSmallVFConfig.ASCENDER_HEIGHT
                    ),
                    _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.counterWidth(
                        HeadlineSmallVFConfig.COUNTER_WIDTH
                    )
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
                FontVariation.slant(0f),
                _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.ascenderHeight(800f),
                _root_ide_package_.org.strawberryfoundations.wear.replicity.theme.counterWidth(500)
            )
        )
    )
}