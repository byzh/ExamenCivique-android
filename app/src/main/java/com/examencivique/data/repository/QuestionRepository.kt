package com.examencivique.data.repository

import android.content.Context
import com.examencivique.data.model.ExamLevel
import com.examencivique.data.model.Question
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.model.QuestionType
import com.examencivique.data.model.ProgressData
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
            val questions = json.decodeFromString<List<Question>>(text)

            // Load Chinese translations and merge
            val zhMap = loadChineseTranslations(context)
            if (zhMap.isNotEmpty()) {
                questions.map { q ->
                    val zh = zhMap[q.id]
                    if (zh != null) {
                        q.copy(
                            questionZh = zh.question,
                            optionsZh = zh.options,
                            explanationZh = zh.explanation
                        )
                    } else q
                }
            } else questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadChineseTranslations(context: Context): Map<String, Question> {
        return try {
            val text = context.assets.open("questions_zh.json").use { stream ->
                InputStreamReader(stream).readText()
            }
            json.decodeFromString<List<Question>>(text).associateBy { it.id }
        } catch (_: Exception) {
            emptyMap()
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

    fun weakQuestions(progressData: ProgressData): List<Question> {
        val weakIds = progressData.weakQuestionIds().toSet()
        return allQuestions
            .filter { it.id in weakIds }
            .sortedBy { progressData.questionResults[it.id]?.accuracy ?: 0.0 }
    }

    fun unansweredQuestions(progressData: ProgressData): List<Question> =
        allQuestions.filter { it.id !in progressData.questionResults }
}
