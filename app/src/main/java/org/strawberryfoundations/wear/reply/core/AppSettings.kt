package org.strawberryfoundations.wear.reply.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val useDynamicColors: Boolean = true,
    val useHapticFeedback: Boolean = false,
    val weightSteps: List<Double> = listOf(2.5, 5.0, 10.0, 15.0),
    val lastSync: Long = 0
)

object SettingsKeys {
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    val WEIGHT_STEPS = stringPreferencesKey("weight_steps")
    val LAST_SYNC = longPreferencesKey("last_sync")
}

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val dynamicColor = prefs[SettingsKeys.DYNAMIC_COLOR]
        val hapticFeedback = prefs[SettingsKeys.HAPTIC_FEEDBACK]
        val lastSync = prefs[SettingsKeys.LAST_SYNC]
        
        AppSettings(
            useDynamicColors = dynamicColor ?: true,
            useHapticFeedback = hapticFeedback ?: false,
            weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                ?.split(",")
                ?.mapNotNull { it.toDoubleOrNull() }
                ?: listOf(2.5, 5.0, 10.0, 15.0),
            lastSync = lastSync ?: 0
        )
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                useDynamicColors = prefs[SettingsKeys.DYNAMIC_COLOR] ?: true,
                useHapticFeedback = prefs[SettingsKeys.HAPTIC_FEEDBACK] ?: false,
                weightSteps = prefs[SettingsKeys.WEIGHT_STEPS]
                    ?.split(",")
                    ?.mapNotNull { it.toDoubleOrNull() }
                    ?: listOf(2.5, 5.0, 10.0, 15.0),
                lastSync = prefs[SettingsKeys.LAST_SYNC] ?: 0
            )
            val new = update(current)
            prefs[SettingsKeys.DYNAMIC_COLOR] = new.useDynamicColors
            prefs[SettingsKeys.HAPTIC_FEEDBACK] = new.useHapticFeedback
            prefs[SettingsKeys.WEIGHT_STEPS] = new.weightSteps.joinToString(",")
            prefs[SettingsKeys.LAST_SYNC] = new.lastSync
        }
    }
}