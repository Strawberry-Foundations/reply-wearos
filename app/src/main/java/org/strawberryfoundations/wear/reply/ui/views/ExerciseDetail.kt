package org.strawberryfoundations.wear.reply.ui.views

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.outlined.Delete
import org.strawberryfoundations.material.symbols.outlined.Edit
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.entities.SessionStatus
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSession
import org.strawberryfoundations.wear.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.wear.reply.room.entities.getExerciseGroupStringResource
import org.strawberryfoundations.wear.reply.room.viewmodels.ExerciseViewModel
import org.strawberryfoundations.wear.reply.room.viewmodels.WorkoutSessionViewModel
import org.strawberryfoundations.wear.reply.ui.composable.DeleteExerciseDialog
import org.strawberryfoundations.wear.reply.ui.composable.EditExerciseDialog
import org.strawberryfoundations.wear.reply.ui.composable.StatCard
import org.strawberryfoundations.wear.reply.ui.theme.hexToColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExerciseDetail(
    exercise: Exercise,
    exerciseViewModel: ExerciseViewModel = viewModel(),
    sessionViewModel: WorkoutSessionViewModel = viewModel(),
    onStartTraining: (Exercise) -> Unit,
    onNavigateToStatistics: (Long) -> Unit,
    onNavigateToHistory: (Long) -> Unit,
    settings: AppSettings,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }

    val cardColor = hexToColor(exercise.color)
    val textColor = remember(cardColor) {
        if (cardColor.luminance() > 0.55f) Color.Black else Color.White
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Exercise?>(null) }

    val workoutSessions = sessionViewModel.allSessions.collectAsState(initial = emptyList())
    val exerciseSessions = workoutSessions.value.filter { it.exerciseId == exercise.id }
    val completedSessions = exerciseSessions.filter { it.status == SessionStatus.COMPLETED }

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = {
                    if (settings.useHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    val newSession = WorkoutSession(
                        exerciseId = exercise.id,
                        currentWeight = exercise.weight as Double,
                        status = SessionStatus.ACTIVE
                    )
                    sessionViewModel.insert(newSession) { sessionId ->
                        org.strawberryfoundations.wear.reply.services.WorkoutService.startService(
                            context,
                            newSession.copy(id = sessionId)
                        )
                    }
                    onStartTraining(exercise)
                },
                buttonSize = EdgeButtonSize.Large,
                colors = ButtonDefaults.filledTonalButtonColors().copy(
                    containerColor = cardColor,
                    contentColor = textColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.start_exercise),
                    )
                    Text(
                        text = stringResource(R.string.start_exercise),
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(start = 4.dp),
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
            // Group icon
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
                }
            }

            // Exercise name
            item {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 14.sp
                )
            }

            // Alternative name
            if (!exercise.altName.isNullOrEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Label,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = exercise.altName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Exercise group
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = getExerciseGroupStringResource(exercise.group),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (exercise.note.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = exercise.note,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            if (exercise.note.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (completedSessions.isNotEmpty()) {
                        val bestWeight = completedSessions.maxOf { it.currentWeight }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.weight),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                StatCard(
                                    label = stringResource(R.string.last_performance),
                                    value = "${exercise.weight} kg",
                                    icon = "⏱️",
                                    iconColor = Color(0xFF4CAF50)
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 2.dp
                                )

                                StatCard(
                                    label = stringResource(R.string.best_performance),
                                    value = "%.1f kg".format(bestWeight),
                                    icon = "🏆",
                                    iconColor = Color(0xFFD77F10)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.weight),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                StatCard(
                                    label = stringResource(R.string.last_performance),
                                    value = "${exercise.weight} kg",
                                    icon = "⏱️",
                                    iconColor = Color(0xFF4CAF50)
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 2.dp
                                )

                                StatCard(
                                    label = stringResource(R.string.best_performance),
                                    value = "- kg",
                                    icon = "🏆",
                                    iconColor = Color(0xFFD77F10)
                                )
                            }
                        }
                    }
                }
            }

            if (!completedSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                if (completedSessions.isNotEmpty()) {
                    Button(
                        onClick = { onNavigateToStatistics(exercise.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.BarChart,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.statistics))
                        }
                    }
                }
                else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.complete_two_trainings),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = stringResource(R.string.to_see_progress),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }

            item {
                if (completedSessions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            item {
                Button(
                    onClick = { onNavigateToHistory(exercise.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.history))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
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
                        onClick = { showEditDialog = true },
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
                            Icon(
                                imageVector = MaterialSymbols.Outlined.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }

                    Button(
                        onClick = {
                            showDeleteDialog = true
                            trainingToDelete = exercise
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
                                imageVector = MaterialSymbols.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditExerciseDialog(
            exercise = exercise,
            onSave = { updatedExercise ->
                exerciseViewModel.update(updatedExercise)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false },
            settings = settings
        )
    }

    if (showDeleteDialog) {
        DeleteExerciseDialog(
            exercise = trainingToDelete!!,
            onConfirm = {
                exerciseViewModel.delete(trainingToDelete!!)
            },
            onDismiss = {
                showDeleteDialog = false
                trainingToDelete = null
            }
        )
    }
}