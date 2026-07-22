package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiKidClient
import com.example.data.database.AppDatabase
import com.example.data.entity.SavedStoryEntity
import com.example.data.repository.BaraemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class AiResponseState {
    object Idle : AiResponseState()
    object Loading : AiResponseState()
    data class Success(val text: String) : AiResponseState()
    data class Error(val message: String) : AiResponseState()
}

class AiTutorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaraemRepository
    
    private val _aiResponseState = MutableStateFlow<AiResponseState>(AiResponseState.Idle)
    val aiResponseState: StateFlow<AiResponseState> = _aiResponseState.asStateFlow()

    private val _generatedStory = MutableStateFlow<SavedStoryEntity?>(null)
    val generatedStory: StateFlow<SavedStoryEntity?> = _generatedStory.asStateFlow()

    private val _savedStories = MutableStateFlow<List<SavedStoryEntity>>(emptyList())
    val savedStories: StateFlow<List<SavedStoryEntity>> = _savedStories.asStateFlow()

    init {
        val dao = AppDatabase.getInstance(application).baraemDao()
        repository = BaraemRepository(dao)

        viewModelScope.launch {
            repository.savedStories.collectLatest { stories ->
                _savedStories.value = stories
            }
        }
    }

    fun askQuestion(question: String) {
        if (question.isBlank()) return
        _aiResponseState.value = AiResponseState.Loading
        viewModelScope.launch {
            val response = GeminiKidClient.generateKidResponse(question)
            _aiResponseState.value = AiResponseState.Success(response)
            repository.logActivity("سؤال للذكاء الاصطناعي: $question", "الذكاء الاصطناعي", 2)
        }
    }

    fun generateStory(topic: String, characterName: String) {
        if (topic.isBlank()) return
        _aiResponseState.value = AiResponseState.Loading
        viewModelScope.launch {
            val storyContent = GeminiKidClient.generateCustomStory(topic, characterName.ifBlank { "سِمْسِم" })
            val title = "قصة $topic الممتعة"
            val moral = "العلم والفضول يفتحان أبواب النجاح والمرح! 🌟"
            val newStory = SavedStoryEntity(
                title = title,
                storyContent = storyContent,
                moralLesson = moral,
                category = topic
            )
            _generatedStory.value = newStory
            _aiResponseState.value = AiResponseState.Success(storyContent)
            repository.saveStory(title, storyContent, moral, topic)
            repository.logActivity("إنشاء قصة جديدة: $title", "القصص التفاعلية", 3)
        }
    }

    fun deleteStory(id: Long) {
        viewModelScope.launch {
            repository.deleteStory(id)
        }
    }

    fun resetState() {
        _aiResponseState.value = AiResponseState.Idle
    }
}
