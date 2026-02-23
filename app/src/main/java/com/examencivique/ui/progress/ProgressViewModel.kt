package com.examencivique.ui.progress

import androidx.lifecycle.ViewModel
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository

class ProgressViewModel(
    val questionRepo: QuestionRepository,
    val progressRepo: ProgressRepository
) : ViewModel() {

    val progress = progressRepo.progress

    fun categoryAccuracy(category: QuestionCategory): Double =
        progressRepo.current.accuracy(category, questionRepo.allQuestions)

    fun triedCount(category: QuestionCategory): Int =
        questionRepo.questionsForCategory(category).count { q ->
            q.id in progressRepo.current.questionResults
        }

    fun resetProgress() {
        progressRepo.reset()
    }
}
