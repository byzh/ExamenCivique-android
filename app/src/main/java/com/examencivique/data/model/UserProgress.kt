package com.examencivique.data.model

import kotlinx.serialization.Serializable

// MARK: - Per-question result

@Serializable
data class QuestionResult(
    val attempts: Int = 0,
    val correctAttempts: Int = 0,
    val lastAttemptTimestamp: Long = 0L
) {
    val accuracy: Double get() = if (attempts > 0) correctAttempts.toDouble() / attempts else 0.0
    val isMastered: Boolean get() = correctAttempts >= 3 && accuracy >= 0.8
}

// MARK: - Exam result

@Serializable
data class ExamResult(
    val id: String,
    val timestamp: Long,
    val levelKey: String,
    val score: Int,
    val totalQuestions: Int,
    val isPassed: Boolean,
    val durationSeconds: Long
) {
    val level: ExamLevel get() = ExamLevel.fromKey(levelKey)
    val scorePercentage: Double get() = score.toDouble() / totalQuestions

    val formattedDuration: String
        get() {
            val m = durationSeconds / 60
            val s = durationSeconds % 60
            return String.format("%02d min %02d s", m, s)
        }
}

// MARK: - Aggregated progress data

@Serializable
data class ProgressData(
    val questionResults: Map<String, QuestionResult> = emptyMap(),
    val examResults: List<ExamResult> = emptyList()
) {
    val totalAttempts: Int get() = questionResults.values.sumOf { it.attempts }
    val totalCorrect: Int get() = questionResults.values.sumOf { it.correctAttempts }
    val overallAccuracy: Double get() = if (totalAttempts > 0) totalCorrect.toDouble() / totalAttempts else 0.0
    val masteredCount: Int get() = questionResults.values.count { it.isMastered }
    val examsPassedCount: Int get() = examResults.count { it.isPassed }

    fun accuracy(category: QuestionCategory, questions: List<Question>): Double {
        val ids = questions.filter { it.category == category }.map { it.id }.toSet()
        val results = questionResults.filterKeys { it in ids }.values.toList()
        if (results.isEmpty()) return 0.0
        val att = results.sumOf { it.attempts }
        val cor = results.sumOf { it.correctAttempts }
        return if (att > 0) cor.toDouble() / att else 0.0
    }

    fun weakQuestionIds(): List<String> =
        questionResults
            .filter { !it.value.isMastered }
            .entries
            .sortedBy { it.value.accuracy }
            .map { it.key }
}
