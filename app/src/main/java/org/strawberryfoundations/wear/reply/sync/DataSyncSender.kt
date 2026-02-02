package org.strawberryfoundations.wear.reply.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.strawberryfoundations.wear.reply.core.model.DbSnapshot
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSession
import java.io.OutputStream


object DataSyncSender {
    fun sendDbSnapshot(
        context: Context,
        exercises: List<Exercise>,
        workoutSessions: List<WorkoutSession>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log connected nodes for debugging
                Wearable.getNodeClient(context).connectedNodes
                    .addOnSuccessListener { nodes ->
                        Log.i("DataSyncSender", "Connected nodes before send: ${nodes.map { it.id + ':' + it.displayName }}")
                        // Notify nodes via MessageClient to trigger fast delivery / diagnostics
                        nodes.forEach { node ->
                            Wearable.getMessageClient(context).sendMessage(
                                node.id,
                                "/sync/notify",
                                "snapshot".toByteArray()
                            ).addOnSuccessListener {
                                Log.i("DataSyncSender", "Notify sent to ${node.id}")
                            }.addOnFailureListener { e ->
                                Log.w("DataSyncSender", "Notify failed to ${node.id}", e)
                            }
                        }
                    }

                val snapshot = DbSnapshot(exercises = exercises, workoutSessions = workoutSessions)
                val json = Json.encodeToString(DbSnapshot.serializer(), snapshot)
                val bytes = json.toByteArray(Charsets.UTF_8)
                val asset = Asset.createFromBytes(bytes)

                val putDataMapReq = PutDataMapRequest.create("/db-sync")
                putDataMapReq.dataMap.putLong("syncTime", System.currentTimeMillis())
                putDataMapReq.dataMap.putAsset("dbAsset", asset)
                val request = putDataMapReq.asPutDataRequest().setUrgent()

                Wearable.getDataClient(context).putDataItem(request)
                    .addOnSuccessListener { _ ->
                        Log.i("DataSyncSender", "Successfully sent DB snapshot: ${exercises.size} exercises, ${workoutSessions.size} sessions")
                    }
                    .addOnFailureListener { e ->
                        Log.w("DataSyncSender", "putDataItem failed, attempting channel fallback", e)
                        // try channel fallback for connected nodes
                        sendViaChannelFallback(context, bytes)
                    }
            } catch (e: Exception) {
                Log.e("DataSyncSender", "Failed to serialize/send snapshot", e)
            }
        }
    }

    // Legacy overload for backward compatibility
    @Deprecated("Use sendDbSnapshot with workoutSessions parameter")
    fun sendDbSnapshot(context: Context, trainings: List<Exercise>) {
        sendDbSnapshot(context, trainings, emptyList())
    }

    private fun sendViaChannelFallback(context: Context, bytes: ByteArray) {
        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                nodes.forEach { node ->
                    Wearable.getChannelClient(context).openChannel(node.id, "db-sync-channel")
                        .addOnSuccessListener { channel ->
                            Wearable.getChannelClient(context).getOutputStream(channel)
                                .addOnSuccessListener { outStream: OutputStream ->
                                    try {
                                        outStream.write(bytes)
                                        outStream.flush()
                                        outStream.close()
                                        Log.i("DataSyncSender", "Sent snapshot via channel to ${node.id}")
                                    } catch (e: Exception) {
                                        Log.e("DataSyncSender", "Failed writing to channel for ${node.id}", e)
                                    }
                                }
                                .addOnFailureListener { e: Exception ->
                                    Log.e("DataSyncSender", "Failed getting channel output for ${node.id}", e)
                                }
                        }
                        .addOnFailureListener { e: Exception ->
                            Log.e("DataSyncSender", "Failed to open channel for ${node.id}", e)
                        }
                }
            }
            .addOnFailureListener { e -> Log.e("DataSyncSender", "Failed to list nodes for channel fallback", e) }
    }
}
