package com.examencivique.data.repository

import android.content.Context
import com.examencivique.data.model.ExamLevel
import com.examencivique.data.model.Question
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.model.QuestionType
import com.examencivique.data.model.UserProgress as UserProgressModel
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class QuestionRepository(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val allQuestions: List<Question> = loadQuestions(context)

    // MARK: - Loading

    private fun loadQuestions(context: Context): List<Question> {
        return try {
            val text = context.assets.open("questions.json").use { stream ->
                InputStreamReader(stream).readText()
            }
            json.decodeFromString<List<Question>>(text)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // MARK: - Filters

    fun questionsForCategory(category: QuestionCategory): List<Question> =
        allQuestions.filter { it.category == category }

    fun questionsForLevel(level: ExamLevel): List<Question> =
        allQuestions.filter { it.isForLevel(level) }

    fun connaissanceQuestions(level: ExamLevel): List<Question> =
        allQuestions.filter { it.isForLevel(level) && it.type == QuestionType.CONNAISSANCE }

    fun situationQuestions(level: ExamLevel): List<Question> =
        allQuestions.filter { it.isForLevel(level) && it.type == QuestionType.SITUATION }

    // MARK: - Exam generation (28 conn + 12 situation = 40 total, shuffled)

    fun generateExam(level: ExamLevel): List<Question> {
        val conn = connaissanceQuestions(level).shuffled().take(28)
        val sit  = situationQuestions(level).shuffled().take(12)
        return (conn + sit).shuffled()
    }

    // MARK: - Weak & unanswered

    fun weakQuestions(progressData: com.examencivique.data.model.ProgressData): List<Question> {
        val weakIds = progressData.weakQuestionIds().toSet()
        return allQuestions
            .filter { it.id in weakIds }
            .sortedBy { progressData.questionResults[it.id]?.accuracy ?: 0.0 }
    }

    fun unansweredQuestions(progressData: com.examencivique.data.model.ProgressData): List<Question> =
        allQuestions.filter { it.id !in progressData.questionResults }
}
