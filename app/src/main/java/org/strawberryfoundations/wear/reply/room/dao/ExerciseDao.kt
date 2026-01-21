package org.strawberryfoundations.wear.reply.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.wear.reply.room.entities.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM trainings")
    fun getAll(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(training: Exercise)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(trainings: List<Exercise>)

    @Update
    suspend fun update(training: Exercise)

    @Delete
    suspend fun delete(training: Exercise)

    @Query("SELECT * FROM trainings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Exercise?
}