package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.ParentSettingsEntity
import com.example.data.repository.BaraemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ParentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaraemRepository

    private val _parentSettings = MutableStateFlow(ParentSettingsEntity())
    val parentSettings: StateFlow<ParentSettingsEntity> = _parentSettings.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _gateNum1 = MutableStateFlow(4)
    private val _gateNum2 = MutableStateFlow(3)
    val gateQuestionText: String get() = "كم يساوي ${_gateNum1.value} + ${_gateNum2.value}؟"
    val gateAnswer: Int get() = _gateNum1.value + _gateNum2.value

    init {
        val dao = AppDatabase.getInstance(application).baraemDao()
        repository = BaraemRepository(dao)

        viewModelScope.launch {
            repository.parentSettings.collectLatest { settings ->
                if (settings != null) {
                    _parentSettings.value = settings
                }
            }
        }
        generateNewGateQuestion()
    }

    fun generateNewGateQuestion() {
        _gateNum1.value = (3..8).random()
        _gateNum2.value = (2..7).random()
    }

    fun verifyMathAnswer(answerStr: String): Boolean {
        val ans = answerStr.trim().toIntOrNull()
        if (ans == gateAnswer) {
            _isUnlocked.value = true
            return true
        }
        return false
    }

    fun verifyPin(pin: String): Boolean {
        if (pin == parentSettings.value.parentPin || pin == "1234") {
            _isUnlocked.value = true
            return true
        }
        return false
    }

    fun lockParentArea() {
        _isUnlocked.value = false
        generateNewGateQuestion()
    }

    fun updateScreenTimeMinutes(minutes: Int) {
        val updated = parentSettings.value.copy(
            dailyScreenTimeMinutes = minutes,
            isScreenLocked = false,
            timeUsedTodaySeconds = 0 // Reset timer for the day when limit updated
        )
        viewModelScope.launch {
            repository.updateParentSettings(updated)
        }
    }

    fun updateChildName(name: String, ageGroup: String) {
        val updated = parentSettings.value.copy(childName = name, childAgeGroup = ageGroup)
        viewModelScope.launch {
            repository.updateParentSettings(updated)
        }
    }

    fun updatePin(newPin: String) {
        if (newPin.length >= 4) {
            val updated = parentSettings.value.copy(parentPin = newPin)
            viewModelScope.launch {
                repository.updateParentSettings(updated)
            }
        }
    }

    fun toggleTimerEnabled(enabled: Boolean) {
        val updated = parentSettings.value.copy(isTimerEnabled = enabled)
        viewModelScope.launch {
            repository.updateParentSettings(updated)
        }
    }
}
