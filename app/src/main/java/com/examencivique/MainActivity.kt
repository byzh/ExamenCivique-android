package com.examencivique

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.examencivique.data.repository.LanguageManager
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import com.examencivique.ui.navigation.AppNavigation
import com.examencivique.ui.theme.ExamenCiviqueTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val questionRepo = QuestionRepository(applicationContext)
        val progressRepo = ProgressRepository(applicationContext)
        val languageManager = LanguageManager(applicationContext)

        setContent {
            ExamenCiviqueTheme {
                AppNavigation(
                    questionRepo = questionRepo,
                    progressRepo = progressRepo,
                    languageManager = languageManager
                )
            }
        }
    }
}
