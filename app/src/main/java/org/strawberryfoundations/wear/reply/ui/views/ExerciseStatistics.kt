package org.strawberryfoundations.wear.reply.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.Exercise
import org.strawberryfoundations.wear.reply.ui.composable.ExerciseProgressGraph
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.room.entities.SessionStatus
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSet
import org.strawberryfoundations.wear.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.wear.reply.ui.composable.ExerciseVolumeGraph
import org.strawberryfoundations.wear.reply.ui.composable.StatCard

@Composable
fun ExerciseStatistics(
    exerciseId: Long,
    sessionViewModel: WorkoutSessionViewModel = viewModel()
) {
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }

    val workoutSessions by sessionViewModel.allSessions.collectAsState(initial = emptyList())
    val exerciseSessions = workoutSessions.filter { it.exerciseId == exerciseId }
    val completedSessions = workoutSessions.filter { it.exerciseId == exerciseId && it.status == SessionStatus.COMPLETED }

    val avgWeight = completedSessions.map { it.currentWeight }.average()
    val totalSets = completedSessions.sumOf { it.setsCompleted }
    val totalSessions = completedSessions.size

    // Calculate total volume (weight × reps × sets)
    val totalVolume = completedSessions.sumOf { session ->
        val sets = try {
            Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
        } catch (e: Exception) {
            emptyList()
        }
        sets.sumOf { workoutSet ->
            (workoutSet.weight * workoutSet.reps).toLong()
        }
    }.toDouble()
    val avgVolume = if (totalSessions > 0) totalVolume / totalSessions else 0.0

    val recentSessions =
        completedSessions.sortedByDescending { it.startedAt }.take(5)
    val trend = if (recentSessions.size >= 2) {
        val newest = recentSessions.first().currentWeight
        val oldest = recentSessions.last().currentWeight
        when {
            newest > oldest -> "📈 ${stringResource(R.string.trend_rising)}"
            newest < oldest -> "📉 ${stringResource(R.string.trend_falling)}"
            else -> "➡️ ${stringResource(R.string.trend_stable)}"
        }
    } else {
        "➖ ${stringResource(R.string.trend_insufficient_data)}"
    }


    ScreenScaffold(
        scrollState = listState
    ) { paddingValues ->
        ScalingLazyColumn(
            state = listState,
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(rotaryFocusRequester)
                .requestFocusOnHierarchyActive()
                .rotaryScrollable(
                    behavior = RotaryScrollableDefaults.behavior(listState),
                    focusRequester = rotaryFocusRequester
                ),
            autoCentering = null
        ) {
            item {
                ListHeader {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = stringResource(R.string.statistics),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = stringResource(R.string.statistics),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color(0xFFFFFFFF),
                        )
                    }
                }
            }

            if (completedSessions.isNotEmpty()) {
                // Average Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.average_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                StatCard(
                                    label = stringResource(R.string.average),
                                    value = "%.1f kg".format(avgWeight),
                                    icon = "📊",
                                    iconColor = Color(0xFF2196F3)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 2.dp
                                )
                                StatCard(
                                    label = stringResource(R.string.trend),
                                    value = trend.substringAfter(" "),
                                    icon = trend.substringBefore(" "),
                                    iconColor = Color(0xFF9C27B0)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Sets Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.sets),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                StatCard(
                                    label = stringResource(R.string.total_sessions),
                                    value = "$totalSessions",
                                    icon = "🎯",
                                    iconColor = Color(0xFFFF5722)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 2.dp
                                )
                                StatCard(
                                    label = stringResource(R.string.total),
                                    value = "$totalSets",
                                    icon = "💪",
                                    iconColor = Color(0xFF00BCD4)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Volume Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.volume_progress),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                StatCard(
                                    label = stringResource(R.string.total),
                                    value = "%.0f kg".format(totalVolume),
                                    icon = "📦",
                                    iconColor = Color(0xFFE91E63)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 2.dp
                                )
                                StatCard(
                                    label = stringResource(R.string.average),
                                    value = "%.0f kg".format(avgVolume),
                                    icon = "📊",
                                    iconColor = Color(0xFF3F51B5)
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(text = "No Data")
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Weight Progress Graph
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.weight_progress),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExerciseProgressGraph(
                        exerciseSessions = exerciseSessions
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Volume Progress Graph
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.volume_progress),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExerciseVolumeGraph(
                        exerciseSessions = exerciseSessions
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
