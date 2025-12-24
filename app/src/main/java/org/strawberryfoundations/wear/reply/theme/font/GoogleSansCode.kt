package org.strawberryfoundations.wear.reply.theme.font

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.theme.font.GoogleSansFlex.DisplayLargeVFConfig


class GoogleSansCode {
    @OptIn(ExperimentalTextApi::class)
    val numeralMedium =
        FontFamily(
            Font(
                R.font.google_sans_code,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(DisplayLargeVFConfig.WEIGHT),
                    FontVariation.width(DisplayLargeVFConfig.WIDTH),
                )
            )
        )
}