package org.strawberryfoundations.wear.reply.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.room.entities.SessionStatus
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSet
import org.strawberryfoundations.wear.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.wear.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.wear.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.wear.reply.services.WorkoutService
import org.strawberryfoundations.wear.reply.ui.composable.RepsInputDialog
import org.strawberryfoundations.wear.reply.ui.composable.StopActiveExercise

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActiveExerciseScreen(
    exerciseId: Long,
    sessionViewModel: WorkoutSessionViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel(),
    settings: AppSettings,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val page0ListState = rememberScalingLazyListState()
    val page1ListState = rememberScalingLazyListState()
    val page0FocusRequester = remember { FocusRequester() }
    val page1FocusRequester = remember { FocusRequester() }

    val exercises by exerciseViewModel.trainings.collectAsState()
    val exercise = exercises.firstOrNull { it.id == exerciseId }

    val activeSession by sessionViewModel.activeSession.collectAsState()
    var currentWeight by remember { mutableDoubleStateOf(0.0) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    var showRepsDialog by remember { mutableStateOf(false) }
    var showStopDialog by remember { mutableStateOf(false) }

    LaunchedEffect(activeSession, exercise) {
        currentWeight = activeSession?.currentWeight ?: exercise?.weight ?: 0.0
    }

    LaunchedEffect(activeSession) {
        val session = activeSession
        if (session == null) {
            elapsedSeconds = 0L
            return@LaunchedEffect
        }

        elapsedSeconds = when (session.status) {
            SessionStatus.ACTIVE -> ((System.currentTimeMillis() - session.startedAt) / 1000).coerceAtLeast(0)
            SessionStatus.PAUSED -> session.elapsedSeconds
            else -> session.elapsedSeconds
        }

        while (activeSession?.status == SessionStatus.ACTIVE) {
            elapsedSeconds = ((System.currentTimeMillis() - session.startedAt) / 1000).coerceAtLeast(0)
            delay(1000)
        }
    }

    if (exercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Exercise not found")
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val activeListState = if (pagerState.currentPage == 0) page0ListState else page1ListState

    ScreenScaffold(scrollState = activeListState) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                key = { it },
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        ScalingLazyColumn(
                            state = page0ListState,
                            contentPadding = paddingValues,
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(page0FocusRequester)
                                .requestFocusOnHierarchyActive()
                                .rotaryScrollable(
                                    behavior = RotaryScrollableDefaults.behavior(page0ListState),
                                    focusRequester = page0FocusRequester
                                ),
                            autoCentering = null
                        ) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(43.dp)
                                            .border(
                                                width = 1.5.dp,
                                                color = MaterialTheme.colorScheme.outline,
                                                shape = MaterialShapes.Cookie9Sided.toShape()
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            shape = MaterialShapes.Cookie9Sided.toShape(),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = getExerciseGroupEmoji(exercise.group),
                                                    fontSize = 18.sp
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceContainer,
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                                24.dp
                                            )
                                        )
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = formatTime(elapsedSeconds),
                                            style = MaterialTheme.typography.displayLarge,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Timer,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = stringResource(R.string.training_time),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 12.sp,
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${activeSession?.setsCompleted ?: 0}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.sets),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                val interactionSource1 = remember { MutableInteractionSource() }
                                val interactionSource2 = remember { MutableInteractionSource() }

                                ButtonGroup(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val session = activeSession ?: return@Button
                                            val isPaused = session.status == SessionStatus.PAUSED
                                            val now = System.currentTimeMillis()
                                            val currentElapsed = when (session.status) {
                                                SessionStatus.ACTIVE -> ((now - session.startedAt) / 1000).coerceAtLeast(0)
                                                SessionStatus.PAUSED -> session.elapsedSeconds
                                                else -> session.elapsedSeconds
                                            }
                                            val newStatus =
                                                if (isPaused) SessionStatus.ACTIVE else SessionStatus.PAUSED

                                            val updatedSession = if (isPaused) {
                                                session.copy(
                                                    status = newStatus,
                                                    startedAt = now - (currentElapsed * 1000),
                                                    updatedAt = now
                                                )
                                            } else {
                                                session.copy(
                                                    status = newStatus,
                                                    elapsedSeconds = currentElapsed,
                                                    updatedAt = now
                                                )
                                            }
                                            sessionViewModel.update(updatedSession)
                                            WorkoutService.updateSession(context, updatedSession)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .animateWidth(interactionSource1),
                                        colors = ButtonDefaults.filledTonalButtonColors(),
                                        interactionSource = interactionSource1,
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val isPaused =
                                                activeSession?.status == SessionStatus.PAUSED
                                            Icon(
                                                imageVector = if (isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                                                contentDescription = stringResource(if (isPaused) R.string.s_continue else R.string.pause)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (settings.useHapticFeedback) {
                                                haptic.performHapticFeedback(HapticFeedbackType.Reject)
                                            }

                                            showStopDialog = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .animateWidth(interactionSource2),
                                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        ),
                                        interactionSource = interactionSource2,
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Stop,
                                                contentDescription = stringResource(R.string.stop)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        ScreenScaffold(
                            scrollState = activeListState,
                            edgeButton = {
                                EdgeButton(
                                    onClick = {
                                        if (settings.useHapticFeedback) {
                                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                        }
                                        showRepsDialog = true
                                    },
                                    buttonSize = EdgeButtonSize.Medium
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.complete_set),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Icon(
                                            imageVector = MaterialSymbols.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )

                                    }
                                }
                            }
                        ) { paddingValues ->
                            ScalingLazyColumn(
                                state = page1ListState,
                                contentPadding = paddingValues,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .focusRequester(page1FocusRequester)
                                    .requestFocusOnHierarchyActive()
                                    .rotaryScrollable(
                                        behavior = RotaryScrollableDefaults.behavior(page1ListState),
                                        focusRequester = page1FocusRequester
                                    ),
                                autoCentering = null,
                            ) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "%.1f kg".format(currentWeight),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                        )
                                        Text(
                                            text = stringResource(R.string.weight),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                item { Spacer(modifier = Modifier.height(8.dp)) }

                                item {
                                    val steps = remember { settings.weightSteps.sorted() }

                                    Column(
                                        modifier = Modifier.fillMaxWidth(0.92f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        steps.chunked(2).forEach { rowSteps ->
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    4.dp,
                                                    Alignment.CenterHorizontally
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                rowSteps.forEach { step ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                currentWeight =
                                                                    maxOf(0.0, currentWeight - step)
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.TextHandleMove
                                                                )
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Remove,
                                                                contentDescription = null,
                                                                tint = Color(0xFFFF5252),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        Text(
                                                            text = if (step % 1.0 == 0.0) step.toInt()
                                                                .toString() else "%.1f".format(step),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.width(28.dp),
                                                            textAlign = TextAlign.Center
                                                        )
                                                        IconButton(
                                                            onClick = {
                                                                currentWeight =
                                                                    maxOf(0.0, currentWeight + step)
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.TextHandleMove
                                                                )
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Rounded.Add,
                                                                contentDescription = null,
                                                                tint = Color(0xFF66BB6A),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalPageIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedColor = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (showRepsDialog) {
            RepsInputDialog(
                initialReps = 10,
                onDismiss = { showRepsDialog = false },
                onConfirm = { reps ->
                    val session = activeSession ?: return@RepsInputDialog
                    val sets = try {
                        Json.decodeFromString<List<WorkoutSet>>(session.setsHistory)
                    } catch (_: Exception) {
                        emptyList()
                    }
                    val newSet = WorkoutSet(
                        setNumber = sets.size + 1,
                        weight = currentWeight,
                        reps = reps
                    )
                    val updatedSets = sets + newSet
                    val updatedSession = session.copy(
                        setsCompleted = session.setsCompleted + 1,
                        setsHistory = Json.encodeToString(updatedSets),
                        currentWeight = currentWeight,
                        updatedAt = System.currentTimeMillis()
                    )
                    sessionViewModel.update(updatedSession)
                    WorkoutService.updateSession(context, updatedSession)
                    showRepsDialog = false
                }
            )
        }
        if (showStopDialog) {
            StopActiveExercise(
                onDismiss = { showStopDialog = false },
                onConfirm = {
                    val session = activeSession

                    if (session != null) {
                        val now = System.currentTimeMillis()
                        val elapsed = when (session.status) {
                            SessionStatus.PAUSED -> session.elapsedSeconds
                            else -> ((now - session.startedAt) / 1000).coerceAtLeast(0)
                        }
                        val completedSession = session.copy(
                            status = SessionStatus.COMPLETED,
                            endedAt = now,
                            elapsedSeconds = elapsed,
                            updatedAt = now
                        )
                        sessionViewModel.update(completedSession)
                        WorkoutService.stopService(context)
                        onComplete()
                    }
                    showStopDialog = false
                }
            )
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, secs)
    } else {
        "%d:%02d".format(minutes, secs)
    }
}
