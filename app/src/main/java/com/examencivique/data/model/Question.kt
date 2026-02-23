package com.examencivique.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// MARK: - Category

enum class QuestionCategory(
    val key: String,
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    PRINCIPES_VALEURS(
        "principes_valeurs",
        "Principes & Valeurs",
        Icons.Filled.Star,
        Color(0xFF002494)      // French blue
    ),
    INSTITUTIONS(
        "institutions",
        "Institutions & Politique",
        Icons.Filled.AccountBalance,
        Color(0xFF7B1FA2)      // Purple
    ),
    DROITS_DEVOIRS(
        "droits_devoirs",
        "Droits & Devoirs",
        Icons.Filled.Scale,
        Color(0xFFE65100)      // Orange
    ),
    HISTOIRE_GEO_CULTURE(
        "histoire_geo_culture",
        "Histoire, Géo & Culture",
        Icons.Filled.Book,
        Color(0xFF338C33)      // Green
    ),
    VIE_EN_FRANCE(
        "vie_en_france",
        "Vivre en France",
        Icons.Filled.Home,
        Color(0xFFED2939)      // French red
    );

    companion object {
        fun fromKey(key: String): QuestionCategory =
            entries.first { it.key == key }
    }
}

// MARK: - Type & Level

enum class QuestionType(val key: String) {
    CONNAISSANCE("connaissance"),
    SITUATION("situation");

    companion object {
        fun fromKey(key: String): QuestionType = entries.first { it.key == key }
    }
}

enum class ExamLevel(
    val key: String,
    val displayName: String,
    val shortName: String,
    val description: String
) {
    CSP(
        "CSP",
        "Carte de Séjour Pluriannuelle (10 ans)",
        "CSP",
        "Pour le renouvellement en carte de séjour pluriannuelle ou pour la carte de résident"
    ),
    CR(
        "CR",
        "Carte de Résident / Naturalisation",
        "CR",
        "Pour la carte de résident ou la demande de naturalisation"
    );

    companion object {
        fun fromKey(key: String): ExamLevel = entries.first { it.key == key }
    }
}

// MARK: - Question (JSON-serializable)

@Serializable
data class Question(
    val id: String,
    @SerialName("category") val categoryKey: String,
    val levels: List<String>,
    @SerialName("type") val typeKey: String,
    val question: String,
    val options: List<String>,
    @SerialName("correct_index") val correctIndex: Int,
    val explanation: String? = null
) {
    val category: QuestionCategory get() = QuestionCategory.fromKey(categoryKey)
    val type: QuestionType get() = QuestionType.fromKey(typeKey)
    val examLevels: List<ExamLevel> get() = levels.map { ExamLevel.fromKey(it) }
    val correctAnswer: String get() = options[correctIndex]

    fun isForLevel(level: ExamLevel): Boolean = level.key in levels
}
