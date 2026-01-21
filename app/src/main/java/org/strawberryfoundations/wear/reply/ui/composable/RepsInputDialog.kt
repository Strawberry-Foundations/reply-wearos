package org.strawberryfoundations.wear.reply.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.default.Check
import org.strawberryfoundations.wear.reply.R

@Composable
fun RepsInputDialog(
    initialReps: Int = 10,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var reps by remember { mutableIntStateOf(initialReps) }
    var isVisible by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }

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
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        onConfirm(reps)
                        isVisible = false
                    },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Icon(
                        MaterialSymbols.Default.Check,
                        contentDescription = stringResource(R.string.confirm)
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
                        RotaryScrollableDefaults.behavior(listState),
                        rotaryFocusRequester
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                autoCentering = null,
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                item {
                    Text(
                        text = stringResource(R.string.enter_reps),
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.how_many_reps),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Reps picker
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (reps > 1) {
                                    reps--
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Remove,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = reps.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                reps++
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = null,
                                tint = Color(0xFF66BB6A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.reps_unit),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Quick select buttons
                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                    ) {
                        listOf(8, 10, 12, 15).forEach { quickRep ->
                            FilledTonalButton(
                                onClick = {
                                    reps = quickRep
                                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                },
                                modifier = Modifier.size(width = 42.dp, height = 32.dp),
                                colors = if (reps == quickRep) {
                                    ButtonDefaults.buttonColors()
                                } else {
                                    ButtonDefaults.filledTonalButtonColors()
                                }
                            ) {
                                Text(
                                    text = quickRep.toString(),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

