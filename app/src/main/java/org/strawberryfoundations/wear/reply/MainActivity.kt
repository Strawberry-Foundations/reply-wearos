@file:OptIn(ExperimentalFoundationApi::class)

package org.strawberryfoundations.wear.reply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.core.SettingsDataStore
import org.strawberryfoundations.wear.reply.theme.AppTheme
import org.strawberryfoundations.wear.reply.views.DeviceScreen
import org.strawberryfoundations.wear.reply.views.SettingsScreen
import org.strawberryfoundations.wear.reply.views.TrainingScreen


// Class: MainActivity
class MainActivity : ComponentActivity() {
    private lateinit var appSettings: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettings = SettingsDataStore(applicationContext)

        setContent {
            MainViewWithPersistence(appSettings)
        }
    }
}

// Composable: MainViewWithPersistence
@Composable
fun MainViewWithPersistence(appSettings: SettingsDataStore) {
    val settings by appSettings.settingsFlow.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()

    AppTheme(
        dynamicColor = settings.useDynamicColors
    ) {
        MainView(
            settings = settings,
            onSettingsChange = { update ->
                scope.launch {
                    appSettings.updateSettings(update)
                }
            }
        )
    }
}

// Composable: MainView
@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    AppScaffold(
        timeText = { TimeText() }
    ) {
        HorizontalPager(
            state = pagerState,
            key = { it },
            beyondViewportPageCount = 1
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (page) {
                    0 -> DeviceScreen(
                        settings = settings
                    )
                    1 -> TrainingScreen(
                        settings = settings
                    )
                    2 -> SettingsScreen(
                        settings = settings,
                        onSettingsChange = onSettingsChange
                    )
                }
            }
        }

        HorizontalPageIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Previews

val previewSettings = AppSettings(
    useDynamicColors = true,
    useHapticFeedback = true
)


@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun TrainingScreenPreview() {
    AppTheme {
        TrainingScreen(
            settings = previewSettings
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DeviceScreenPreview() {
    AppTheme {
        DeviceScreen(
            settings = previewSettings
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(
            settings = previewSettings,
            onSettingsChange = {}
        )
    }
}
