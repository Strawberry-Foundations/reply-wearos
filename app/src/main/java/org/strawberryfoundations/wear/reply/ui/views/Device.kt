package org.strawberryfoundations.wear.reply.ui.views

import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Watch
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.DevicesWearables
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.wear.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.wear.reply.sync.DataSyncSender
import org.strawberryfoundations.wear.reply.ui.composable.SyncConfirmDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun DeviceView(
    settings: AppSettings,
    viewModel: ExerciseViewModel = viewModel(),
    sessionViewModel: WorkoutSessionViewModel = viewModel(),
) {
    val listState = rememberScalingLazyListState()
    remember { FocusRequester() }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val deviceName = remember {
        try {
            Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
                ?: Build.MODEL
        } catch (_: Exception) {
            Build.MODEL
        }
    }

    val allSessions by sessionViewModel.allSessions.collectAsState()
    val workoutCount = viewModel.trainings.collectAsState().value.size
    val sessionCount = allSessions.size

    var showSyncConfirmDialog by remember { mutableStateOf(false) }

    val rotaryFocusRequester = remember { FocusRequester() }
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = {
                    if (settings.useHapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    showSyncConfirmDialog = true
                },
                buttonSize = EdgeButtonSize.Large,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.sync_with_smartphone),
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.displaySmall,
                    )
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = stringResource(R.string.sync_with_smartphone),
                    )
                }
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
            // Title
            item {
                ListHeader {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = MaterialSymbols.Filled.DevicesWearables,
                            contentDescription = stringResource(R.string.device),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = stringResource(R.string.device),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Watch,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = deviceName,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = stringResource(R.string.statistics),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.statistics),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.workouts),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = workoutCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.sessions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sessionCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.last_sync),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val lastUpdated = settings.lastSync
                        val lastSyncText = if (lastUpdated <= 0L) {
                            stringResource(R.string.never)
                        } else {
                            try {
                                SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
                                    .format(Date(lastUpdated))
                            } catch (_: Exception) {
                                stringResource(R.string.never)
                            }
                        }

                        Text(
                            text = lastSyncText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    if (showSyncConfirmDialog) {
        val exercises = viewModel.trainings.collectAsState().value
        val toastTextSuccess = stringResource(R.string.toast_sync_success, exercises.size, allSessions.size)
        val toastTextFailed = stringResource(R.string.toast_sync_failed)

        SyncConfirmDialog(
            exerciseCount = viewModel.trainings.collectAsState().value.size,
            sessionCount = allSessions.size,
            onConfirm = {
                if (exercises.isNotEmpty() || allSessions.isNotEmpty()) {
                    DataSyncSender.sendDbSnapshot(context, exercises, allSessions)
                    Toast.makeText(
                        context,
                        toastTextSuccess,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, toastTextFailed, Toast.LENGTH_SHORT).show()
                }
                showSyncConfirmDialog = false
            },
            onDismiss = { showSyncConfirmDialog = false }
        )
    }
}