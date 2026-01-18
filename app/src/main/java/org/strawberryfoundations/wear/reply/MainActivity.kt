@file:OptIn(ExperimentalFoundationApi::class)

package org.strawberryfoundations.wear.reply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.core.SettingsDataStore
import org.strawberryfoundations.wear.reply.room.ExerciseViewModel
import org.strawberryfoundations.wear.reply.theme.AppTheme
import org.strawberryfoundations.wear.reply.theme.darkenColor
import org.strawberryfoundations.wear.reply.theme.hexToColor
import org.strawberryfoundations.wear.reply.views.DeviceView
import org.strawberryfoundations.wear.reply.views.ExerciseDetail
import org.strawberryfoundations.wear.reply.views.SettingsView
import org.strawberryfoundations.wear.reply.views.TrainingView


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

    AppTheme(dynamicColor = settings.useDynamicColors) {
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
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainView(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit,
    viewModel: ExerciseViewModel = viewModel(),
) {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val navController = rememberSwipeDismissableNavController()

    AppScaffold(
        timeText = { TimeText() }
    ) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "main"
        ) {
            // Navigation Route: main
            composable(route = "main") {
                HorizontalPager(
                    state = pagerState,
                    key = { it },
                    beyondViewportPageCount = 1
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Main pages
                        when (page) {
                            0 -> DeviceView(
                                settings = settings
                            )
                            1 -> TrainingView(
                                settings = settings,
                                onExerciseClick = { exerciseId ->
                                    navController.navigate("exerciseDetail/$exerciseId")
                                }
                            )
                            2 -> SettingsView(
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

            // Navigation Route: exerciseDetail/{exerciseId}
            composable(
                route = "exerciseDetail/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) { it ->
                val exerciseId = it.arguments?.getLong("exerciseId")
                val trainings by viewModel.trainings.collectAsState()
                val exercise = trainings.firstOrNull { it.id == exerciseId }

                val cardColor = darkenColor(hexToColor(exercise?.color ?: ""), 0.55f)
                val textColor = remember(cardColor) {
                    if (cardColor.luminance() > 0.55f) Color.Black else Color.White
                }

                if (exercise == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ContainedLoadingIndicator(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    return@composable
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ExerciseDetail(
                        exercise = exercise,
                        onStartTraining = {},
                        onBack = {},
                        settings = settings,
                    )
                }
            }
        }
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
fun TrainingViewPreview() {
    AppTheme {
        TrainingView(
            settings = previewSettings,
            onExerciseClick = {}
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DeviceViewPreview() {
    AppTheme {
        DeviceView(
            settings = previewSettings
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun SettingsViewPreview() {
    AppTheme {
        SettingsView(
            settings = previewSettings,
            onSettingsChange = {}
        )
    }
}
