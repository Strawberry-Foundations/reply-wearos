package org.strawberryfoundations.wear.replicity.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.strawberryfoundations.wear.replicity.core.model.Training


@Database(entities = [Training::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "training_db"
                )
                    .addMigrations()
                    .build().also { INSTANCE = it }
            }
    }
}