package com.examencivique.ui.study

import androidx.lifecycle.ViewModel
import com.examencivique.data.model.Question
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StudyMode {
    ALL, CATEGORY, WEAK, UNANSWERED
}

data class StudyState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val showAnswer: Boolean = false,
    val sessionFinished: Boolean = false
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val totalCount: Int get() = questions.size
    val isLastQuestion: Boolean get() = currentIndex >= questions.size - 1
    val progressFraction: Float get() = if (totalCount > 0) (currentIndex + 1f) / totalCount else 0f
}

class StudyViewModel(
    private val questionRepo: QuestionRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyState())
    val state: StateFlow<StudyState> = _state.asStateFlow()

    private var mode: StudyMode = StudyMode.ALL
    private var category: QuestionCategory? = null

    fun startSession(mode: StudyMode, category: QuestionCategory? = null) {
        this.mode = mode
        this.category = category

        val questions = when (mode) {
            StudyMode.ALL -> questionRepo.allQuestions.shuffled()
            StudyMode.CATEGORY -> category?.let { questionRepo.questionsForCategory(it).shuffled() } ?: emptyList()
            StudyMode.WEAK -> questionRepo.weakQuestions(progressRepo.current)
            StudyMode.UNANSWERED -> questionRepo.unansweredQuestions(progressRepo.current).shuffled()
        }
        _state.value = StudyState(questions = questions)
    }

    fun selectOption(index: Int) {
        val s = _state.value
        if (s.showAnswer) return
        val q = s.currentQuestion ?: return
        progressRepo.recordAnswer(q.id, index == q.correctIndex)
        _state.value = s.copy(selectedOptionIndex = index, showAnswer = true)
    }

    fun nextQuestion() {
        val s = _state.value
        if (s.isLastQuestion) {
            _state.value = s.copy(sessionFinished = true)
        } else {
            _state.value = s.copy(
                currentIndex = s.currentIndex + 1,
                showAnswer = false,
                selectedOptionIndex = null
            )
        }
    }

    fun previousQuestion() {
        val s = _state.value
        if (s.currentIndex > 0) {
            _state.value = s.copy(
                currentIndex = s.currentIndex - 1,
                showAnswer = false,
                selectedOptionIndex = null
            )
        }
    }

    fun resetSession() {
        startSession(mode, category)
    }
}
