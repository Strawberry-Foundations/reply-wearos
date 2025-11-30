package org.strawberryfoundations.wear.replicity.views

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
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
import androidx.compose.ui.text.font.FontWeight
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
import org.strawberryfoundations.wear.replicity.R
import org.strawberryfoundations.wear.replicity.core.AppSettings
import org.strawberryfoundations.wear.replicity.database.TrainingViewModel
import org.strawberryfoundations.wear.replicity.theme.contrastColor
import org.strawberryfoundations.wear.replicity.theme.darkenColor
import org.strawberryfoundations.wear.replicity.theme.hexToColor


@SuppressLint("UnrememberedMutableState")
@Composable
fun TrainingScreen(
    viewModel: TrainingViewModel = viewModel(),
    settings: AppSettings
) {
    // Basic variable initialization
    val haptic = LocalHapticFeedback.current

    val allStr = stringResource(R.string.all)
    val upperBodyStr = stringResource(R.string.upper_body)
    val legsStr = stringResource(R.string.legs)
    val otherStr = stringResource(R.string.other)

    val categories = listOf(
        allStr,
        upperBodyStr,
        legsStr,
        otherStr
    )

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val exercises by viewModel.trainings.collectAsState()

    val filteredExercises by derivedStateOf {
        when (selectedCategoryIndex) {
            1 -> exercises.filter { it.group == upperBodyStr }
            2 -> exercises.filter { it.group == legsStr }
            3 -> exercises.filter { it.group == otherStr }
            else -> exercises
        }
    }

    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }

    // Local state for exercise list (must be in a @Composable scope)
    var expandedIndex by remember { mutableIntStateOf(-1) }
    var bobIndex by remember { mutableIntStateOf(-1) }

    // Short bob effect: when `bobIndex` is set, reset after a short delay
    LaunchedEffect(bobIndex) {
        if (bobIndex != -1) {
            delay(160)
            bobIndex = -1
        }
    }

    // Formatter for weights (up to 3 decimal places, no trailing zeros)
    val df = DecimalFormat("#.###")
    val symbols = DecimalFormatSymbols(Locale.US)
    df.decimalFormatSymbols = symbols

    val smallestStep = settings.weightSteps.minOrNull() ?: 1.0

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
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_training)
                    )
                    Text(
                        text = stringResource(R.string.add_training),
                        modifier = Modifier.padding(start = 4.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = stringResource(R.string.training),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = stringResource(R.string.training),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color(0xFFFFFFFF),
                        )
                    }
                }
            }

            // Category name
            item {
                val emoji = when (selectedCategoryIndex) {
                    1 -> "💪"
                    2 -> "🦵"
                    3 -> "🧩"
                    else -> "🏋"
                }
                Text(
                    text = "$emoji ${categories[selectedCategoryIndex]}",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Category filter buttons
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 6.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(space = 6.dp)
                ) {
                    categories.forEachIndexed { index, name ->
                        val isSelected = selectedCategoryIndex == index
                        val emoji = when (name) {
                            allStr -> "🏋"
                            upperBodyStr -> "💪"
                            legsStr -> "🦵"
                            otherStr -> "🧩"
                            else -> "🧩"
                        }

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
                                selectedCategoryIndex = index 
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
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                        ) {
                            if (isSelected) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(emoji)
                                }
                            } else {
                                Text(text = emoji)
                            }
                        }
                    }
                }
            }

            // Exercise list
            items(filteredExercises.size) { index ->
                val exercise = filteredExercises[index]
                val isExpanded = index == expandedIndex
                val buttonColor = hexToColor(exercise.color)

                val scale by animateFloatAsState(
                    targetValue = if (bobIndex == index) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "exerciseScale"
                )

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
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                maxLines = if (isExpanded) 2 else 1,
                                overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                            )

                            val bgColor = darkenColor(buttonColor)
                            val textColor = contrastColor(bgColor)

                            Box(
                                modifier = Modifier
                                    .background(color = bgColor, shape = CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "${exercise.weight} kg",
                                    color = textColor,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.size(6.dp))

                            // Weight adjust row (uses smallest configured step)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(onClick = {
                                    val current = exercise.weight.replace(',', '.').toDoubleOrNull() ?: 0.0
                                    val newW = (current - smallestStep).coerceAtLeast(0.0)
                                    val updated = exercise.copy(weight = df.format(newW))
                                    viewModel.update(updated)
                                    if (settings.useHapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }) {
                                    Text(text = "-")
                                }

                                val displayWeight = exercise.weight.replace(',', '.').toDoubleOrNull() ?: 0.0
                                Text(
                                    text = "${df.format(displayWeight)} kg",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Button(onClick = {
                                    val current = exercise.weight.replace(',', '.').toDoubleOrNull() ?: 0.0
                                    val newW = current + smallestStep
                                    val updated = exercise.copy(weight = df.format(newW))
                                    viewModel.update(updated)
                                    if (settings.useHapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }) {
                                    Text(text = "+")
                                }
                            }

                            Spacer(modifier = Modifier.size(6.dp))

                            Text(
                                text = exercise.note.ifBlank { "No notes" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Category: ${exercise.group}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}