package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.AlphabetItem
import com.example.data.model.LearningDataProvider
import com.example.data.model.MathQuestion
import com.example.data.model.NumberItem
import com.example.data.model.VocabularyCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LearningViewModel : ViewModel() {

    val alphabets: List<AlphabetItem> = LearningDataProvider.arabicAlphabets
    val numbers: List<NumberItem> = LearningDataProvider.numbersList
    val mathQuestions: List<MathQuestion> = LearningDataProvider.mathQuestions
    val vocabularyCards: List<VocabularyCard> = LearningDataProvider.vocabularyCards

    private val _selectedLetter = MutableStateFlow(alphabets.first())
    val selectedLetter: StateFlow<AlphabetItem> = _selectedLetter.asStateFlow()

    private val _currentMathIndex = MutableStateFlow(0)
    val currentMathIndex: StateFlow<Int> = _currentMathIndex.asStateFlow()

    private val _selectedVocabCategory = MutableStateFlow("الكل")
    val selectedVocabCategory: StateFlow<String> = _selectedVocabCategory.asStateFlow()

    fun selectLetter(letter: AlphabetItem) {
        _selectedLetter.value = letter
    }

    fun nextMathQuestion() {
        if (_currentMathIndex.value < mathQuestions.size - 1) {
            _currentMathIndex.value += 1
        } else {
            _currentMathIndex.value = 0
        }
    }

    fun setVocabCategory(category: String) {
        _selectedVocabCategory.value = category
    }
}
