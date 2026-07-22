package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ActivityLogEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedStoryEntity
import com.example.data.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BaraemDao {

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM parent_settings WHERE id = 1")
    fun getParentSettings(): Flow<ParentSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateParentSettings(settings: ParentSettingsEntity)

    @Query("SELECT * FROM saved_stories ORDER BY createdTimestamp DESC")
    fun getAllSavedStories(): Flow<List<SavedStoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: SavedStoryEntity)

    @Query("DELETE FROM saved_stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: Long)

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT 20")
    fun getActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)
}
