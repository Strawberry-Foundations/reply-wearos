package org.strawberryfoundations.wear.replicity.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


val Context.userPreferencesDataStore: DataStore<Preferences> by dataStore(
    fileName = "user_prefs.json",
    serializer = UserPreferencesSerializer
)

suspend fun saveUserData(context: Context, user: Preferences) {
    println("[::debug]: saveUserData() called with: $user")
    try {
        context.userPreferencesDataStore.updateData { user }
        println("[::debug]: Saved data successfully in DataStore")

        val saved = context.userPreferencesDataStore.data.first()
        println("[::debug]: Verification - saved data: $saved")
    } catch (e: Exception) {
        println("[::debug]:: Error while saving: ${e.message}")
    }
}

fun getUserDataFlow(context: Context): Flow<Preferences> = context.userPreferencesDataStore.data