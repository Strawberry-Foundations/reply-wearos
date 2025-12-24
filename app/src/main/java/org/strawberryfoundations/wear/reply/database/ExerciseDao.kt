package org.strawberryfoundations.wear.reply.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.wear.reply.core.model.Exercise


@Dao
interface ExerciseDao {
    @Query("SELECT * FROM trainings")
    fun getAll(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trainings: List<Exercise>)

    @Update
    suspend fun update(training: Exercise)

    @Delete
    suspend fun delete(training: Exercise)
}