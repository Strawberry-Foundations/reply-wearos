package org.strawberryfoundations.wear.reply.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.delay
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.material.symbols.filled.Exercise
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.core.model.ExerciseGroup
import org.strawberryfoundations.wear.reply.core.model.getExerciseGroupEmoji
import org.strawberryfoundations.wear.reply.core.model.getExerciseGroupStringResource
import org.strawberryfoundations.wear.reply.database.ExerciseViewModel
import org.strawberryfoundations.wear.reply.theme.contrastColor
import org.strawberryfoundations.wear.reply.theme.darkenColor
import org.strawberryfoundations.wear.reply.theme.hexToColor


@SuppressLint("UnrememberedMutableState")
@Composable
fun TrainingScreen(
    viewModel: ExerciseViewModel = viewModel(),
    settings: AppSettings
) {
    // Basic variable initialization
    val haptic = LocalHapticFeedback.current
    val exercises by viewModel.trainings.collectAsState()
    val exercisesUi by viewModel.trainingsUi.collectAsState(initial = emptyList())
    val addTrainingText = stringResource(R.string.add_training)
    val workoutText = stringResource(R.string.workout)
    val noteText = stringResource(R.string.note)
    val noNoteText = stringResource(R.string.no_note)
    val allText = stringResource(R.string.all)
    val exerciseGroups = remember { listOf(null) + ExerciseGroup.entries }
    var selectedGroupIndex by remember { mutableIntStateOf(0) }

    val filteredExercises by remember(exercises, selectedGroupIndex) {
        derivedStateOf {
            val selectedGroup = exerciseGroups[selectedGroupIndex]
            if (selectedGroup == null) {
                exercises.toList()
            } else {
                exercises.filter { it.group == selectedGroup }
            }
        }
    }

    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }

    var expandedIndex by remember { mutableIntStateOf(-1) }
    var bobIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(bobIndex) {
        if (bobIndex != -1) {
            delay(160)
            bobIndex = -1
        }
    }

    // Screen Scaffold
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = {
                    if (settings.useHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                buttonSize = EdgeButtonSize.Large,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = addTrainingText,
                    )
                    Text(
                        text = addTrainingText,
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
            // Title
            item {
                ListHeader {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = MaterialSymbols.Filled.Exercise,
                                contentDescription = workoutText,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(20.dp),
                                tint = Color(0xFFFFFFFF)
                            )
                            Text(
                                text = workoutText,
                                style = MaterialTheme.typography.displayLarge,
                                color = Color(0xFFFFFFFF),
                            )
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                        
                        // Category name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        )
                        {
                            val selectedGroup = exerciseGroups[selectedGroupIndex]
                            val categoryText = if (selectedGroup == null) {
                                "🏋 $allText"
                            } else {
                                "${getExerciseGroupEmoji(selectedGroup)} ${getExerciseGroupStringResource(selectedGroup)}"
                            }
                            
                            Text(
                                text = categoryText,
                                style = MaterialTheme.typography.displaySmall,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }


            item { }

            // Category filter buttons
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 6.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(space = 6.dp)
                ) {
                    exerciseGroups.forEachIndexed { index, group ->
                        val isSelected = selectedGroupIndex == index
                        val emoji = if (group == null) "🏋" else getExerciseGroupEmoji(group)

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "scale"
                        )

                        Button(
                            onClick = {
                                if (settings.useHapticFeedback) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                selectedGroupIndex = index
                            },
                            colors = if (isSelected) {
                                ButtonDefaults.buttonColors()
                            } else {
                                ButtonDefaults.filledTonalButtonColors()
                            },
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            },
                            modifier = Modifier
                                .size(
                                    width = if (isSelected) 72.dp else 50.dp,
                                    height = 50.dp
                                )
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                        ) {
                            if (isSelected) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = MaterialSymbols.Default.Check,
                                        contentDescription = "selected",
                                    )
                                    Text(
                                        text = emoji,
                                        fontSize = 14.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = emoji,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Exercise list
            items(
                count = filteredExercises.size,
                key = { filteredExercises[it].id }
            ) { index ->
                val exercise = filteredExercises[index]
                val isExpanded = index == expandedIndex
                val uiById = remember(exercisesUi) { exercisesUi.associateBy { it.exercise.id } }
                val ui = uiById[exercise.id]

                val buttonColor = ui?.buttonColor ?: remember(exercise.id, exercise.color) { hexToColor(exercise.color) }
                val fgColor = ui?.fgColor ?: remember(exercise.id, buttonColor) { contrastColor(buttonColor) }
                val secondaryBgColor = ui?.secondaryBgColor ?: remember(exercise.id, buttonColor) { darkenColor(buttonColor) }
                val secondaryTextColor = ui?.secondaryTextColor ?: remember(exercise.id, secondaryBgColor) { contrastColor(secondaryBgColor) }

                val scale by animateFloatAsState(
                    targetValue = if (bobIndex == index) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "exerciseScale"
                )

                // Exercise card
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .animateContentSize()
                        .heightIn(min = 48.dp),
                    onClick = {
                        val willExpand = !isExpanded
                        expandedIndex = if (isExpanded) -1 else index
                        if (willExpand) bobIndex = index
                        if (settings.useHapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = exercise.name,
                                color = fgColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = if (isExpanded) 2 else 1,
                                overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                            )

                            Box(
                                modifier = Modifier
                                    .background(color = secondaryBgColor, shape = CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "${exercise.weight} kg",
                                    color = secondaryTextColor,
                                    style = MaterialTheme.typography.numeralMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Expanded card
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(),
                            exit = shrinkVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeOut()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 2.dp,
                                            end = 8.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val emoji = getExerciseGroupEmoji(exercise.group)

                                    if (selectedGroupIndex == 0) {
                                        Row {
                                            Text(
                                                text = emoji,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = fgColor,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .padding(end = 6.dp)
                                            )

                                            Text(
                                                text = getExerciseGroupStringResource(exercise.group),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 13.sp,
                                                lineHeight = 14.sp,
                                                color = fgColor,
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.Notes,
                                                contentDescription = noteText,
                                                tint = fgColor,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .padding(end = 6.dp)
                                            )

                                            Text(
                                                text = exercise.note.ifBlank { noNoteText },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontSize = 13.sp,
                                                lineHeight = 14.sp,
                                                color = fgColor,
                                                maxLines = 4,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        /*
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(expBg, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    if (settings.useHapticFeedback) haptic.performHapticFeedback(
                                                        HapticFeedbackType.LongPress
                                                    )
                                                    bobIndex = index
                                                }
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.edit),
                                                tint = fgColor,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }*/
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