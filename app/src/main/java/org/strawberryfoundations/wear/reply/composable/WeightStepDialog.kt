package org.strawberryfoundations.wear.reply.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import org.strawberryfoundations.material.symbols.MaterialSymbols
import org.strawberryfoundations.material.symbols.filled.Weight
import org.strawberryfoundations.wear.reply.R

@Composable
fun WeightStepDialog(
    currentSteps: List<Double>,
    onDismiss: () -> Unit,
    onAdd: (Double) -> Unit
) {
    val commonSteps = listOf(0.25, 0.5, 1.0, 2.5, 5.0, 10.0, 15.0)

    var showCustomDialog by remember { mutableStateOf(false) }

    Dialog(
        visible = true,
        onDismissRequest = onDismiss,
    ) {
        val listState = rememberScalingLazyListState()
        val rotaryFocusRequester = remember { FocusRequester() }

        ScreenScaffold(
            scrollState = listState,
            edgeButton = {
                EdgeButton(
                    onClick = { showCustomDialog = true },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.new_step)
                        )
                        Text(
                            text = stringResource(R.string.new_step),
                            modifier = Modifier.weight(1f, fill = false),
                            style = MaterialTheme.typography.displayMedium
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
                autoCentering = null,
            ) {
                item {
                    ListHeader {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = MaterialSymbols.Filled.Weight,
                                contentDescription = stringResource(R.string.new_step),
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = stringResource(R.string.edit_weight_steps),
                                style = MaterialTheme.typography.displayMedium,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                    }
                }

                items(commonSteps.size) { index ->
                    val step = commonSteps[index]
                    val alreadyExists = step in currentSteps

                    Button(
                        onClick = {
                            if (!alreadyExists) {
                                onAdd(step)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !alreadyExists,
                        colors = if (alreadyExists) {
                            ButtonDefaults.filledTonalButtonColors()
                        } else {
                            ButtonDefaults.buttonColors()
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Text(
                                text = "$step kg",
                                style = MaterialTheme.typography.displaySmall,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomWeightStepDialog(
            currentSteps = currentSteps,
            onDismiss = { showCustomDialog = false },
            onAdd = { value ->
                onAdd(value)
                showCustomDialog = false
            }
        )
    }
}