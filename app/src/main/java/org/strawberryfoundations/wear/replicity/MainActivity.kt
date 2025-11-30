@file:OptIn(ExperimentalFoundationApi::class)

package org.strawberryfoundations.wear.replicity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.foundation.hierarchicalFocusGroup
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch
import org.strawberryfoundations.wear.replicity.core.AppSettings
import org.strawberryfoundations.wear.replicity.core.SettingsDataStore
import org.strawberryfoundations.wear.replicity.theme.ReplicityTheme
import org.strawberryfoundations.wear.replicity.views.DeviceScreen
import org.strawberryfoundations.wear.replicity.views.SettingsScreen
import org.strawberryfoundations.wear.replicity.views.TrainingScreen

val previewSettings = AppSettings(
    useDynamicColors = true,
    useHapticFeedback = true
)

class MainActivity : ComponentActivity() {
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            MainViewWithPersistence(settingsDataStore)
        }
    }
}

@Composable
fun MainViewWithPersistence(settingsDataStore: SettingsDataStore) {
    val settings by settingsDataStore.settingsFlow.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()

    ReplicityTheme(dynamicColor = settings.useDynamicColors) {
        MainView(
            settings = settings,
            onSettingsChange = { update ->
                scope.launch {
                    settingsDataStore.updateSettings(update)
                }
            }
        )
    }

}

@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    val pageIndicatorState: PageIndicatorState = remember(pagerState) {
        object : PageIndicatorState {
            override val pageCount: Int
                get() = pagerState.pageCount
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageOffset: Float
                get() = pagerState.currentPageOffsetFraction
        }
    }

    AppScaffold(
        timeText = {
            TimeText(
                timeTextStyle = MaterialTheme.typography.labelLarge
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            key = { it },
            beyondViewportPageCount = 0
        ) { page ->
            val isCurrentPage = pagerState.currentPage == page
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hierarchicalFocusGroup(active = isCurrentPage)
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
            pageIndicatorState = pageIndicatorState
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun TrainingScreenPreview() {
    ReplicityTheme {
        TrainingScreen(
            settings = previewSettings
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DeviceScreenPreview() {
    ReplicityTheme {
        DeviceScreen(
            settings = previewSettings
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SettingsScreenPreview() {
    ReplicityTheme {
        SettingsScreen(
            settings = previewSettings,
            onSettingsChange = {}
        )
    }
}
