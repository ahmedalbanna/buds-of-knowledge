package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BaraemDao
import com.example.data.entity.ActivityLogEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedStoryEntity
import com.example.data.entity.UserProgressEntity

@Database(
    entities = [
        UserProgressEntity::class,
        ParentSettingsEntity::class,
        SavedStoryEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun baraemDao(): BaraemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baraem_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
