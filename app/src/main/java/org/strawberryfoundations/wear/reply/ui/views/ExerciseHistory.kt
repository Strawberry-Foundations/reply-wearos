package org.strawberryfoundations.wear.reply.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.room.entities.SessionStatus
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSession
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSet
import org.strawberryfoundations.wear.reply.room.viewmodels.WorkoutSessionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExerciseHistory(
    exerciseId: Long,
    sessionViewModel: WorkoutSessionViewModel = viewModel()
) {
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }
    var selectedSession by remember { mutableStateOf<WorkoutSession?>(null) }

    val workoutSessions by sessionViewModel.allSessions.collectAsState(initial = emptyList())
    val exerciseSessions = workoutSessions.filter { it.exerciseId == exerciseId }.sortedByDescending { it.startedAt }

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
                            imageVector = Icons.Rounded.History,
                            contentDescription = stringResource(R.string.history),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = stringResource(R.string.history),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color(0xFFFFFFFF),
                        )
                    }
                }
            }

            if (exerciseSessions.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_data),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(exerciseSessions.size) { index ->
                    val session = exerciseSessions[index]
                    SessionListItem(session) {
                        selectedSession = session
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (selectedSession != null) {
        SessionDetailScreen(
            session = selectedSession!!,
            onDismiss = { selectedSession = null }
        )
    }
}

@Composable
private fun SessionListItem(session: WorkoutSession, onClick: () -> Unit) {
    val sdf = remember { SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()) }
    val dateStr = sdf.format(Date(session.startedAt))
    val isCompleted = session.status == SessionStatus.COMPLETED
    val statusColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFF44336)
    val statusText = if (isCompleted) "✓" else "✕"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${session.currentWeight} kg",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "${session.setsCompleted} Sätze",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SessionDetailScreen(session: WorkoutSession, onDismiss: () -> Unit) {
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }
    var isVisible by remember { mutableStateOf(true) }

    val sets = try {
        Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
    } catch (_: Exception) {
        emptyList()
    }
    val totalVolume = sets.sumOf { (it.weight * it.reps).toLong() }.toDouble()

    val sdf = remember { SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()) }
    val dateStr = sdf.format(Date(session.startedAt))

    val mins = session.elapsedSeconds / 60
    val secs = session.elapsedSeconds % 60
    val timeStr = "${mins}m ${secs}s"

    Dialog(
        visible = isVisible,
        onDismissRequest = {
            isVisible = false
            onDismiss()
        }
    ) {
        ScreenScaffold(
            scrollState = listState,
            edgeButton = {
                EdgeButton(
                    onClick = {
                        isVisible = false
                        onDismiss()
                    },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Icon(
                        MaterialSymbols.Default.Check,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
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
                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.session_details),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    DetailRowItem(
                        stringResource(R.string.status),
                        if (session.status == SessionStatus.COMPLETED) stringResource(R.string.completed) else stringResource(R.string.cancelled)
                    )
                }
                item { DetailRowItem(stringResource(R.string.weight), "${session.currentWeight} kg") }
                item { DetailRowItem(stringResource(R.string.sets), "${session.setsCompleted}") }
                item { DetailRowItem(stringResource(R.string.duration), timeStr) }
                item { DetailRowItem(stringResource(R.string.total_volume), "$totalVolume kg") }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                if (sets.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.sets_details),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(sets.size) { index ->
                        val s = sets[index]
                        val vol = s.weight * s.reps
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.set) + " ${s.setNumber}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = "${s.weight}kg x ${s.reps}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${vol}kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun DetailRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
