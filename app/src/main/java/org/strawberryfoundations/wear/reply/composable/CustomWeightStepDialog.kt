package org.strawberryfoundations.wear.reply.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scale
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
import org.strawberryfoundations.wear.reply.R

@Composable
fun CustomWeightStepDialog(
    currentSteps: List<Double>,
    onDismiss: () -> Unit,
    onAdd: (Double) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    Dialog(
        visible = true,
        onDismissRequest = onDismiss,
    ) {
        ScreenScaffold(
            scrollState = listState,
            edgeButton = {
                EdgeButton(
                    onClick = {
                        val parsed = text.replace(',', '.').toDoubleOrNull()
                        val rounded = parsed?.let { (kotlin.math.round(it * 1000) / 1000.0) }
                        if (rounded != null && rounded > 0.0 && (rounded !in currentSteps.map { (kotlin.math.round(it * 1000) / 1000.0) })) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            onAdd(rounded)
                        }
                    },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.add),
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.add)
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
                                imageVector = Icons.Default.Scale,
                                contentDescription = stringResource(R.string.new_step),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.new_step),
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
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = text,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() || it == '.' || it == ',' }
                                    val sepCount = filtered.count { it == '.' || it == ',' }

                                    text = if (sepCount <= 1) {
                                        filtered
                                    } else {
                                        val firstIdx = filtered.indexOfFirst { it == '.' || it == ',' }
                                        val before = filtered.take(firstIdx + 1)
                                        val after = filtered.substring(firstIdx + 1).replace(".", "").replace(",", "")
                                        before + after
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.displayMedium.copy(color = Color(0xFFFFFFFF)),
                            ) { innerTextField ->
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)) {
                                    if (text.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.weight),
                                            style = MaterialTheme.typography.displaySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    innerTextField()
                                }
                            }

                            if (text.isNotEmpty()) {
                                Text(
                                    text = "kg",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}