package com.examencivique.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examencivique.data.model.ExamLevel
import com.examencivique.data.model.ExamResult
import com.examencivique.data.model.Question
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ExamState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),   // questionId → chosen index
    val timeRemaining: Int = 45 * 60,                     // seconds
    val isFinished: Boolean = false,
    val examResult: ExamResult? = null
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val answeredCount: Int get() = selectedAnswers.size
    val score: Int get() = questions.count { selectedAnswers[it.id] == it.correctIndex }
    val isTimeCritical: Boolean get() = timeRemaining <= 300

    val formattedTime: String get() {
        val m = timeRemaining / 60
        val s = timeRemaining % 60
        return String.format("%02d:%02d", m, s)
    }

    val progressFraction: Float get() = if (totalQuestions > 0) (currentIndex + 1f) / totalQuestions else 0f
}

class ExamViewModel(
    private val questionRepo: QuestionRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExamState())
    val state: StateFlow<ExamState> = _state.asStateFlow()

    private var level: ExamLevel = ExamLevel.CSP
    private var timerJob: Job? = null
    private var startTimeMs: Long = 0L

    // MARK: - Start

    fun startExam(level: ExamLevel) {
        this.level = level
        startTimeMs = System.currentTimeMillis()
        _state.value = ExamState(
            questions = questionRepo.generateExam(level)
        )
        startTimer()
    }

    // MARK: - Answer

    fun selectAnswer(index: Int) {
        val s = _state.value
        val q = s.currentQuestion ?: return
        if (s.isFinished) return
        _state.value = s.copy(
            selectedAnswers = s.selectedAnswers + (q.id to index)
        )
    }

    fun isAnswered(question: Question): Boolean =
        _state.value.selectedAnswers.containsKey(question.id)

    fun selectedIndex(question: Question): Int? =
        _state.value.selectedAnswers[question.id]

    // MARK: - Navigation

    fun goNext() {
        val s = _state.value
        if (s.currentIndex < s.totalQuestions - 1)
            _state.value = s.copy(currentIndex = s.currentIndex + 1)
    }

    fun goPrevious() {
        val s = _state.value
        if (s.currentIndex > 0)
            _state.value = s.copy(currentIndex = s.currentIndex - 1)
    }

    fun goTo(index: Int) {
        val s = _state.value
        if (index in 0 until s.totalQuestions)
            _state.value = s.copy(currentIndex = index)
    }

    // MARK: - Submit

    fun submitExam() {
        timerJob?.cancel()
        val s = _state.value
        recordProgress(s)
        val durationSec = (System.currentTimeMillis() - startTimeMs) / 1000
        val result = ExamResult(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            levelKey = level.key,
            score = s.score,
            totalQuestions = s.totalQuestions,
            isPassed = s.score >= 32,
            durationSeconds = durationSec
        )
        progressRepo.recordExam(result)
        _state.value = s.copy(isFinished = true, examResult = result)
    }

    fun reset() {
        timerJob?.cancel()
        _state.value = ExamState()
    }

    // MARK: - Timer

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeRemaining > 0 && !_state.value.isFinished) {
                delay(1000)
                val s = _state.value
                if (s.timeRemaining <= 1) {
                    submitExam()
                } else {
                    _state.value = s.copy(timeRemaining = s.timeRemaining - 1)
                }
            }
        }
    }

    private fun recordProgress(s: ExamState) {
        for (q in s.questions) {
            val chosen = s.selectedAnswers[q.id]
            progressRepo.recordAnswer(q.id, chosen == q.correctIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
