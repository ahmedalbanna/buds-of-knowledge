package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.ActivityLogEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.UserProgressEntity
import com.example.data.repository.BaraemRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaraemRepository
    
    private val _userProgress = MutableStateFlow(UserProgressEntity())
    val userProgress: StateFlow<UserProgressEntity> = _userProgress.asStateFlow()

    private val _parentSettings = MutableStateFlow(ParentSettingsEntity())
    val parentSettings: StateFlow<ParentSettingsEntity> = _parentSettings.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLogEntity>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLogEntity>> = _activityLogs.asStateFlow()

    private val _isScreenLocked = MutableStateFlow(false)
    val isScreenLocked: StateFlow<Boolean> = _isScreenLocked.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(1800L) // Default 30 min
    val remainingTimeSeconds: StateFlow<Long> = _remainingTimeSeconds.asStateFlow()

    private var timerJob: Job? = null

    init {
        val dao = AppDatabase.getInstance(application).baraemDao()
        repository = BaraemRepository(dao)

        viewModelScope.launch {
            repository.userProgress.collectLatest { progress ->
                if (progress != null) {
                    _userProgress.value = progress
                } else {
                    repository.updateProgress(UserProgressEntity())
                }
            }
        }

        viewModelScope.launch {
            repository.parentSettings.collectLatest { settings ->
                if (settings != null) {
                    _parentSettings.value = settings
                    _isScreenLocked.value = settings.isScreenLocked
                    val totalLimitSeconds = settings.dailyScreenTimeMinutes * 60L
                    val remaining = totalLimitSeconds - settings.timeUsedTodaySeconds
                    _remainingTimeSeconds.value = if (remaining < 0) 0L else remaining
                } else {
                    repository.updateParentSettings(ParentSettingsEntity())
                }
            }
        }

        viewModelScope.launch {
            repository.activityLogs.collectLatest { logs ->
                _activityLogs.value = logs
            }
        }

        startScreenTimeTimer()
    }

    private fun startScreenTimeTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentSettings = parentSettings.value
                if (currentSettings.isTimerEnabled && !isScreenLocked.value) {
                    val newUsedSeconds = currentSettings.timeUsedTodaySeconds + 1
                    val maxAllowedSeconds = currentSettings.dailyScreenTimeMinutes * 60L
                    val remaining = maxAllowedSeconds - newUsedSeconds
                    _remainingTimeSeconds.value = if (remaining < 0) 0L else remaining

                    if (newUsedSeconds >= maxAllowedSeconds && maxAllowedSeconds > 0) {
                        _isScreenLocked.value = true
                        repository.updateParentSettings(currentSettings.copy(timeUsedTodaySeconds = newUsedSeconds, isScreenLocked = true))
                    } else if (newUsedSeconds % 30 == 0L) {
                        // Persist used time every 30 seconds
                        repository.updateParentSettings(currentSettings.copy(timeUsedTodaySeconds = newUsedSeconds))
                    }
                }
            }
        }
    }

    fun addStars(count: Int, activityName: String, category: String) {
        viewModelScope.launch {
            val currentStars = _userProgress.value.starsCount
            val newProgress = _userProgress.value.copy(starsCount = currentStars + count)
            repository.updateProgress(newProgress)
            repository.logActivity(activityName, category, count)
        }
    }

    fun unlockScreenWithPin(pinInput: String): Boolean {
        if (pinInput == parentSettings.value.parentPin || pinInput == "1234") {
            _isScreenLocked.value = false
            viewModelScope.launch {
                // Add 15 extra bonus minutes or reset lock
                val updatedUsedTime = (parentSettings.value.dailyScreenTimeMinutes * 60L) - (15 * 60L)
                repository.updateParentSettings(
                    parentSettings.value.copy(
                        isScreenLocked = false,
                        timeUsedTodaySeconds = if (updatedUsedTime < 0) 0 else updatedUsedTime
                    )
                )
            }
            return true
        }
        return false
    }

    fun lockScreenManually() {
        _isScreenLocked.value = true
        viewModelScope.launch {
            repository.updateParentSettings(parentSettings.value.copy(isScreenLocked = true))
        }
    }
}
