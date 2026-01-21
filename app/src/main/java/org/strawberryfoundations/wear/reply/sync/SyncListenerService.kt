package org.strawberryfoundations.wear.reply.sync

import android.net.Uri
import android.util.Log
import androidx.room.withTransaction
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.AppDatabase
import org.strawberryfoundations.wear.reply.core.SettingsDataStore
import org.strawberryfoundations.wear.reply.core.model.DbSnapshot
import java.io.InputStream
import androidx.core.net.toUri


class SyncListenerService : WearableListenerService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val channelCallback = object : ChannelClient.ChannelCallback() {
        override fun onChannelOpened(channel: ChannelClient.Channel) {
            super.onChannelOpened(channel)
            try {
                if (channel.path == "db-sync-channel") {
                    Wearable.getChannelClient(this@SyncListenerService).getInputStream(channel)
                        .addOnSuccessListener { inputStream: InputStream ->
                            scope.launch {
                                try {
                                    inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                                        val json = reader.readText()
                                        processJsonAndStore(json)
                                    }
                                } catch (e: Exception) {
                                    Log.e("SyncListenerService", "Failed reading channel", e)
                                }
                            }
                        }.addOnFailureListener { e: Exception ->
                            Log.e("SyncListenerService", "Failed to open channel input", e)
                        }
                }
            } catch (e: Exception) {
                Log.e("SyncListenerService", "onChannelOpened error", e)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Wearable.getChannelClient(this).registerChannelCallback(channelCallback)
    }

    override fun onDestroy() {
        Wearable.getChannelClient(this).unregisterChannelCallback(channelCallback)
        super.onDestroy()
    }

    private suspend fun processJsonAndStore(json: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot: DbSnapshot = try {
                    Json.decodeFromString(json)
                } catch (e: Exception) {
                    // Fallback for legacy format (List<Exercise>)
                    val list: List<Exercise> = Json.decodeFromString(json)
                    DbSnapshot(exercises = list, workoutSessions = emptyList())
                }

                val db = AppDatabase.getInstance(applicationContext)
                db.withTransaction {
                    val dao = db.trainingDao()
                    try {
                        dao.insertAll(snapshot.exercises)
                    } catch (_: Exception) {
                        snapshot.exercises.forEach { t -> dao.insert(t) }
                    }

                    val sessionDao = db.workoutSessionDao()
                    snapshot.workoutSessions.forEach { s -> sessionDao.insert(s) }
                }
                try {
                    val settings = SettingsDataStore(applicationContext)
                    settings.updateSettings { it.copy(lastSync = System.currentTimeMillis()) }
                } catch (e: Exception) {
                    Log.w("SyncListenerService", "Failed to update last sync setting", e)
                }
                Log.i("SyncListenerService", "Applied ${snapshot.exercises.size} exercises and ${snapshot.workoutSessions.size} sessions from sync")
            } catch (e: Exception) {
                Log.e("SyncListenerService", "Failed to parse/store snapshot", e)
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/db-sync") {
                    val item = DataMapItem.fromDataItem(event.dataItem)
                    val asset = item.dataMap.getAsset("dbAsset") ?: continue

                    Wearable.getDataClient(this).getFdForAsset(asset)
                        .addOnSuccessListener { result ->
                            scope.launch {
                                try {
                                    result.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                                        val json = reader.readText()
                                        processJsonAndStore(json)
                                    }
                                } catch (e: Exception) {
                                    Log.e("SyncListenerService", "Failed to apply snapshot", e)
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("SyncListenerService", "Failed getting asset fd", e)
                        }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        try {
            Log.i("SyncListenerService", "onMessageReceived: ${messageEvent.path}")

            if (messageEvent.path == "/request-sync") {
                Log.i("SyncListenerService", "Received sync request from phone, sending data...")
                scope.launch {
                    try {
                        val db = AppDatabase.getInstance(applicationContext)
                        val exercises = db.trainingDao().getAll().first()
                        val sessions = db.workoutSessionDao().getAll().first()

                        DataSyncSenderFromWearable.sendDbSnapshot(applicationContext, exercises, sessions)
                        Log.i("SyncListenerService", "Sent ${exercises.size} exercises and ${sessions.size} sessions to phone")
                    } catch (e: Exception) {
                        Log.e("SyncListenerService", "Failed to send data to phone", e)
                    }
                }
            }

            if (messageEvent.path == "/sync/notify") {
                val uri = "wear://*/db-sync".toUri()
                Wearable.getDataClient(this).getDataItems(uri).addOnSuccessListener { dataItems ->
                    for (item in dataItems) {
                        val path = item.uri.path
                        if (path == "/db-sync") {
                            val dataMapItem = DataMapItem.fromDataItem(item)
                            val asset = dataMapItem.dataMap.getAsset("dbAsset") ?: continue
                            Wearable.getDataClient(this).getFdForAsset(asset)
                                .addOnSuccessListener { result ->
                                    scope.launch {
                                        result.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                                            val json = reader.readText()
                                            processJsonAndStore(json)
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("SyncListenerService", "onMessageReceived error", e)
        }
    }
}
