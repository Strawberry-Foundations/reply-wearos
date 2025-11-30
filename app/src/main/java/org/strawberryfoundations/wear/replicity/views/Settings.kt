package org.strawberryfoundations.wear.replicity.views

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.requestFocusOnHierarchyActive
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import org.strawberryfoundations.wear.replicity.R
import org.strawberryfoundations.wear.replicity.core.AppSettings
import kotlin.toString

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (_: PackageManager.NameNotFoundException) {
        "N/A"
    }.toString()
}


@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings.() -> AppSettings) -> Unit
) {
    val listState = rememberScalingLazyListState()
    val rotaryFocusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current
    var showWeightStepInput by remember { mutableStateOf(false) }

    if (showWeightStepInput) {
        WeightStepDialog(
            currentSteps = settings.weightSteps,
            onDismiss = { showWeightStepInput = false },
            onAdd = { newStep ->
                if (settings.useHapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                }
                onSettingsChange { copy(weightSteps = weightSteps + newStep) }
                showWeightStepInput = false
            },
        )
    }

    ScreenScaffold(scrollState = listState) { paddingValues ->
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = stringResource(R.string.settings),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                }
            }

            // Appearance Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = stringResource(R.string.settings_section_appearance),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.settings_section_appearance),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item {
                SwitchButton(
                    checked = settings.useDynamicColors,
                    onCheckedChange = { checked -> onSettingsChange {
                            copy(useDynamicColors = checked)
                        } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.dynamic_colors_title),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = stringResource(R.string.dynamic_colors_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Interaction Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = stringResource(R.string.settings_section_interaction),
                        modifier = Modifier.size(18.dp).padding(bottom = 2.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.settings_section_interaction),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item {
                SwitchButton(
                    checked = settings.useHapticFeedback,
                    onCheckedChange = { checked -> onSettingsChange {
                        if (checked) haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        copy(useHapticFeedback = checked)
                    } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.haptic_feedback_title),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = stringResource(R.string.haptic_feedback_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Weight Steps Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = stringResource(R.string.settings_section_weight_steps),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.settings_section_weight_steps),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    settings.weightSteps.sorted().forEach { step ->
                        FilledTonalButton(
                            onClick = {
                                if (settings.useHapticFeedback) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSettingsChange { copy(weightSteps = weightSteps - step) }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$step",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.delete),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = {
                            if (settings.useHapticFeedback) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            showWeightStepInput = true
                        },
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // About Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.settings_section_about),
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.settings_section_about),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item {
                val context = LocalContext.current
                val appVersion = remember { getAppVersion(context) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.splash),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Replicity for WearOS",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version $appVersion",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2025 Juliandev02",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.settings_all_rights_reserved),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.stbfnds),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = "#stbfnds",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

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
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.new_step)
                        )
                        Text(
                            text = stringResource(R.string.new_step),
                            modifier = Modifier.weight(1f, fill = false)
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
                        Text(
                            text = stringResource(R.string.edit_weight_steps),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color(0xFFFFFFFF)
                        )
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
                        Text("$step kg")
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
                        // try to add when edge button pressed
                        val parsed = text.replace(',', '.').toDoubleOrNull()
                        val rounded = parsed?.let { (kotlin.math.round(it * 1000) / 1000.0) }
                        if (rounded != null && rounded > 0.0 && (rounded !in currentSteps.map { (kotlin.math.round(it * 1000) / 1000.0) })) {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            onAdd(rounded)
                        }
                    },
                    buttonSize = EdgeButtonSize.Large,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add)
                        )
                        Text(
                            text = stringResource(R.string.add),
                            modifier = Modifier.weight(1f, fill = false)
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
                        Text(
                            text = stringResource(R.string.new_step),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color(0xFFFFFFFF)
                        )
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
                                textStyle = MaterialTheme.typography.labelLarge.copy(color = Color(0xFFFFFFFF)),
                            ) { innerTextField ->
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)) {
                                    if (text.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.weight_kg),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    innerTextField()
                                }
                            }

                            if (text.isNotEmpty()) {
                                Text(
                                    text = "kg",
                                    style = MaterialTheme.typography.labelLarge,
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



