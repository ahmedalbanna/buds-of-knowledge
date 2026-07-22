package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val starsCount: Int = 15,
    val dailyStreakDays: Int = 3,
    val completedAlphabetCount: Int = 5,
    val completedMathCount: Int = 3,
    val completedStoriesCount: Int = 2,
    val totalTimeSpentMinutes: Int = 25,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "parent_settings")
data class ParentSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val parentPin: String = "1234",
    val dailyScreenTimeMinutes: Int = 30,
    val timeUsedTodaySeconds: Long = 300, // 5 minutes used
    val isTimerEnabled: Boolean = true,
    val isScreenLocked: Boolean = false,
    val childName: String = "البطل الصغير",
    val childAgeGroup: String = "تمهيدي (4-6 سنوات)" // or ابتدائي (7-10 سنوات)
)

@Entity(tableName = "saved_stories")
data class SavedStoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val storyContent: String,
    val moralLesson: String,
    val category: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val starsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
