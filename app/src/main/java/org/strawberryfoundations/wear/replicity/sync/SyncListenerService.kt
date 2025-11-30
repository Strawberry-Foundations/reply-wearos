package org.strawberryfoundations.wear.replicity.sync

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.strawberryfoundations.wear.replicity.core.model.Training
import org.strawberryfoundations.wear.replicity.database.AppDatabase
import java.io.InputStream


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
                val list: List<Training> = Json.decodeFromString(json)
                val db = AppDatabase.getInstance(applicationContext)
                db.withTransaction {
                    val dao = db.trainingDao()
                    try {
                        dao.insertAll(list)
                    } catch (_: Exception) {
                        list.forEach { t -> dao.insert(t) }
                    }
                }
                Log.i("SyncListenerService", "Applied ${list.size} trainings from sync")
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
            if (messageEvent.path == "/sync/notify") {
                // Try to explicitly fetch the DataItem (in case notification is used to wake the app)
                val uri = Uri.parse("wear://*/db-sync")
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
