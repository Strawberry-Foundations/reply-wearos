package org.strawberryfoundations.wear.replicity.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.strawberryfoundations.wear.replicity.core.model.Training

@Dao
interface TrainingDao {
    @Query("SELECT * FROM Training")
    fun getAll(): Flow<List<Training>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Training)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trainings: List<Training>)

    @Update
    suspend fun update(training: Training)

    @Delete
    suspend fun delete(training: Training)
}