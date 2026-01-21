package org.strawberryfoundations.wear.reply.core.model

import kotlinx.serialization.Serializable
import org.strawberryfoundations.wear.reply.room.entities.Exercise
import org.strawberryfoundations.wear.reply.room.entities.WorkoutSession

@Serializable
data class DbSnapshot(
    val exercises: List<Exercise>,
    val workoutSessions: List<WorkoutSession>
)