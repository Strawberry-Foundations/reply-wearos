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

object DataSyncSenderFromWearable {
    fun sendDbSnapshot(
        context: Context,
        exercises: List<Exercise>,
        workoutSessions: List<WorkoutSession>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = DbSnapshot(exercises = exercises, workoutSessions = workoutSessions)
                val json = Json.encodeToString(DbSnapshot.serializer(), snapshot)
                val bytes = json.toByteArray(Charsets.UTF_8)
                val asset = Asset.createFromBytes(bytes)

                val putDataMapReq = PutDataMapRequest.create("/db-sync-from-wearable")
                putDataMapReq.dataMap.putLong("syncTime", System.currentTimeMillis())
                putDataMapReq.dataMap.putAsset("dbAsset", asset)
                val request = putDataMapReq.asPutDataRequest().setUrgent()

                Wearable.getDataClient(context).putDataItem(request)
                    .addOnSuccessListener { _ ->
                        Log.i("DataSyncSenderFromWearable", "Successfully sent DB snapshot to phone: ${exercises.size} exercises, ${workoutSessions.size} sessions")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DataSyncSenderFromWearable", "Failed to send snapshot to phone", e)
                    }
            } catch (e: Exception) {
                Log.e("DataSyncSenderFromWearable", "Failed to serialize/send snapshot", e)
            }
        }
    }
}