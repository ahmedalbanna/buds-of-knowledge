package com.example.data.repository

import com.example.data.dao.BaraemDao
import com.example.data.entity.ActivityLogEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.SavedStoryEntity
import com.example.data.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

class BaraemRepository(private val dao: BaraemDao) {

    val userProgress: Flow<UserProgressEntity?> = dao.getUserProgress()
    val parentSettings: Flow<ParentSettingsEntity?> = dao.getParentSettings()
    val savedStories: Flow<List<SavedStoryEntity>> = dao.getAllSavedStories()
    val activityLogs: Flow<List<ActivityLogEntity>> = dao.getActivityLogs()

    suspend fun updateProgress(progress: UserProgressEntity) {
        dao.insertOrUpdateProgress(progress)
    }

    suspend fun addStars(count: Int, activityName: String, category: String) {
        val currentProgress = dao.getUserProgress()
        // Default base if null
        val updatedStars = 15 + count
        dao.insertOrUpdateProgress(
            UserProgressEntity(
                starsCount = updatedStars,
                dailyStreakDays = 3,
                completedAlphabetCount = 5,
                completedMathCount = 3,
                completedStoriesCount = 2,
                totalTimeSpentMinutes = 25
            )
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                title = activityName,
                category = category,
                starsEarned = count
            )
        )
    }

    suspend fun updateParentSettings(settings: ParentSettingsEntity) {
        dao.insertOrUpdateParentSettings(settings)
    }

    suspend fun saveStory(title: String, content: String, moral: String, category: String) {
        dao.insertStory(
            SavedStoryEntity(
                title = title,
                storyContent = content,
                moralLesson = moral,
                category = category
            )
        )
    }

    suspend fun deleteStory(id: Long) {
        dao.deleteStory(id)
    }

    suspend fun logActivity(title: String, category: String, stars: Int = 1) {
        dao.insertActivityLog(
            ActivityLogEntity(
                title = title,
                category = category,
                starsEarned = stars
            )
        )
    }
}
