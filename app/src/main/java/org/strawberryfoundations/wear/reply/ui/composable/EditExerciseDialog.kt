package org.strawberryfoundations.wear.reply.ui.composable

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.input.RemoteInputIntentHelper
import org.strawberryfoundations.wear.reply.R
import org.strawberryfoundations.wear.reply.core.AppSettings
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.entities.ExerciseGroup
import org.strawberryfoundations.wear.reply.room.entities.getExerciseGroupEmoji
import org.strawberryfoundations.wear.reply.ui.theme.hexToColor

private const val KEY_NAME_INPUT = "name_input"
private const val KEY_ALT_NAME_INPUT = "alt_name_input"
private const val KEY_NOTE_INPUT = "note_input"

@Composable
fun EditExerciseDialog(
    exercise: Exercise,
    onSave: (Exercise) -> Unit,
    onDismiss: () -> Unit,
    settings: AppSettings
) {
    var isVisible by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(exercise.name) }
    var altName by remember { mutableStateOf(exercise.altName ?: "") }
    var note by remember { mutableStateOf(exercise.note) }
    var weight by remember { mutableStateOf(exercise.weight) }
    var selectedGroup by remember { mutableStateOf(exercise.group) }

    val haptic = LocalHapticFeedback.current

    val nameInputLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results: Bundle = RemoteInput.getResultsFromIntent(data)
            val newName = results.getCharSequence(KEY_NAME_INPUT)
            if (newName != null) {
                name = newName.toString()
            }
        }
    }

    val altNameInputLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results: Bundle = RemoteInput.getResultsFromIntent(data)
            val newAltName = results.getCharSequence(KEY_ALT_NAME_INPUT)
            if (newAltName != null) {
                altName = newAltName.toString()
            }
        }
    }

    val noteInputLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results: Bundle = RemoteInput.getResultsFromIntent(data)
            val newNote = results.getCharSequence(KEY_NOTE_INPUT)
            if (newNote != null) {
                note = newNote.toString()
            }
        }
    }

    Dialog(
        visible = isVisible,
        onDismissRequest = { isVisible = false; onDismiss() },
    ) {
        val listState = rememberScalingLazyListState()
        val rotaryFocusRequester = remember { FocusRequester() }

        ScreenScaffold(
            scrollState = listState,
            edgeButton = {
                EdgeButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSave(exercise.copy(
                            name = name, altName = altName,
                            note = note, weight = weight, group = selectedGroup
                        ))
                        isVisible = false
                    },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = stringResource(R.string.save)
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
                        RotaryScrollableDefaults.behavior(listState),
                        rotaryFocusRequester
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                autoCentering = null,
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.displayLarge,
                        )
                    }
                }

                item { Spacer(Modifier.height(4.dp)) }

                // Weight
                item {
                    Card(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.weight),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "%.1f ${exercise.weightUnit}".format(weight ?: 0.0),
                                style = MaterialTheme.typography.displayMedium,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                // Weight steps
                item {
                    val steps = remember { settings.weightSteps.sorted() }

                    Column(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        steps.chunked(2).forEach { rowSteps ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowSteps.forEach { step ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val newWeight = ((weight ?: 0.0) - step).coerceAtLeast(0.0)
                                                weight = newWeight
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                            text = if (step % 1.0 == 0.0) step.toInt().toString() else "%.1f".format(step),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.width(28.dp),
                                            textAlign = TextAlign.Center
                                        )
                                        IconButton(
                                            onClick = {
                                                weight = (weight ?: 0.0) + step
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Name
                item {
                    val n = stringResource(R.string.name)

                    Button(
                        onClick = {
                            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder(KEY_NAME_INPUT)
                                    .setLabel(n)
                                    .setChoices(arrayOf(name))
                                    .build()
                            )
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            nameInputLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth(0.92f),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.name),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                // Alt Name
                item {
                    val al = stringResource(R.string.alt_name)
                    Button(
                        onClick = {
                            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder(KEY_ALT_NAME_INPUT)
                                    .setLabel(al)
                                    .setChoices(if (altName.isNotBlank()) arrayOf(altName) else emptyArray())
                                    .build()
                            )
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            altNameInputLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth(0.92f),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.alt_name),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                            Text(
                                text = altName.ifBlank { "-" },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }


                // Group
                item {
                    Text(
                        text = stringResource(R.string.group),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(62.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExerciseGroup.entries.forEach { group ->
                            val isSelected = selectedGroup == group
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
                                    selectedGroup = group
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
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
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = getExerciseGroupEmoji(group),
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Color
                item {
                    Card(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(hexToColor(exercise.color))
                            )
                            Text(
                                text = stringResource(R.string.color),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Note
                item {
                    val n = stringResource(R.string.note)
                    Button(
                        onClick = {
                            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder(KEY_NOTE_INPUT)
                                    .setLabel(n)
                                    .setChoices(if (note.isNotBlank()) arrayOf(note) else emptyArray())
                                    .build()
                            )
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            noteInputLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth(0.92f),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.note),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                            Text(
                                text = note.ifBlank { "-" },
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2
                            )
                        }
                    }
                }

                /* Cancel
                item {
                    CompactButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isVisible = false
                            onDismiss()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }*/
            }
        }
    }
}