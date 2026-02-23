package com.examencivique.data.repository

import android.content.Context
import com.examencivique.data.model.ExamResult
import com.examencivique.data.model.ProgressData
import com.examencivique.data.model.QuestionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class ProgressRepository(context: Context) {

    private val prefs = context.getSharedPreferences("ec_progress", Context.MODE_PRIVATE)
    private val json  = Json { ignoreUnknownKeys = true }

    private val _progress = MutableStateFlow(load())
    val progress: StateFlow<ProgressData> = _progress.asStateFlow()

    val current: ProgressData get() = _progress.value

    // MARK: - Record

    fun recordAnswer(questionId: String, isCorrect: Boolean) {
        val data = _progress.value
        val existing = data.questionResults[questionId]
        val updated = QuestionResult(
            attempts = (existing?.attempts ?: 0) + 1,
            correctAttempts = (existing?.correctAttempts ?: 0) + if (isCorrect) 1 else 0,
            lastAttemptTimestamp = System.currentTimeMillis()
        )
        _progress.value = data.copy(
            questionResults = data.questionResults + (questionId to updated)
        )
        save()
    }

    fun recordExam(result: ExamResult) {
        val data = _progress.value
        _progress.value = data.copy(
            examResults = listOf(result) + data.examResults   // Most recent first
        )
        save()
    }

    // MARK: - Reset

    fun reset() {
        _progress.value = ProgressData()
        prefs.edit().clear().apply()
    }

    // MARK: - Persistence

    private fun save() {
        val encoded = json.encodeToString(ProgressData.serializer(), _progress.value)
        prefs.edit().putString("data", encoded).apply()
    }

    private fun load(): ProgressData {
        val raw = prefs.getString("data", null) ?: return ProgressData()
        return try {
            json.decodeFromString(ProgressData.serializer(), raw)
        } catch (e: Exception) {
            ProgressData()
        }
    }
}
