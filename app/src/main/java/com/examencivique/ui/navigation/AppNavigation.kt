package com.examencivique.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.examencivique.data.model.ExamLevel
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.LanguageManager
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import com.examencivique.ui.exam.ExamScreen
import com.examencivique.ui.exam.ExamSetupScreen
import com.examencivique.ui.exam.ExamViewModel
import com.examencivique.ui.exam.ResultsScreen
import com.examencivique.ui.i18n.LocalLanguage
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.i18n.strings
import com.examencivique.ui.progress.ProgressScreen
import com.examencivique.ui.progress.ProgressViewModel
import com.examencivique.ui.study.StudyCardScreen
import com.examencivique.ui.study.StudyMode
import com.examencivique.ui.study.StudyScreen
import com.examencivique.ui.study.StudyViewModel

sealed class BottomTab(val route: String, val icon: ImageVector) {
    data object Study    : BottomTab("study",    Icons.Filled.AutoStories)
    data object Exam     : BottomTab("exam",     Icons.Filled.CheckCircle)
    data object Progress : BottomTab("progress", Icons.Filled.AccountCircle)
}

val bottomTabs = listOf(BottomTab.Study, BottomTab.Exam, BottomTab.Progress)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    questionRepo: QuestionRepository,
    progressRepo: ProgressRepository,
    languageManager: LanguageManager
) {
    val language by languageManager.language.collectAsState()
    val s = strings(language)

    CompositionLocalProvider(
        LocalStrings provides s,
        LocalLanguage provides language
    ) {
        val navController = rememberNavController()
        val navBackStack  by navController.currentBackStackEntryAsState()
        val currentRoute  = navBackStack?.destination?.route

        val showBottomBar = currentRoute in bottomTabs.map { it.route }

        val tabLabels = mapOf(
            BottomTab.Study to s.tabStudy,
            BottomTab.Exam to s.tabExam,
            BottomTab.Progress to s.tabAccount
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomTabs.forEach { tab ->
                            val label = tabLabels[tab] ?: ""
                            NavigationBarItem(
                                icon = { Icon(tab.icon, contentDescription = label) },
                                label = { Text(label) },
                                selected = currentRoute == tab.route,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomTab.Study.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Study tab
                composable(BottomTab.Study.route) {
                    StudyScreen(
                        questionRepo = questionRepo,
                        progressRepo = progressRepo,
                        onNavigateToCards = { mode, catKey ->
                            navController.navigate("study_cards/$mode/${catKey ?: "none"}")
                        }
                    )
                }

                // Study cards
                composable(
                    "study_cards/{mode}/{catKey}",
                    arguments = listOf(
                        navArgument("mode") { type = NavType.StringType },
                        navArgument("catKey") { type = NavType.StringType }
                    )
                ) { entry ->
                    val modeName = entry.arguments?.getString("mode") ?: "ALL"
                    val catKey   = entry.arguments?.getString("catKey")
                    val mode     = StudyMode.valueOf(modeName)
                    val category = if (catKey != "none") QuestionCategory.fromKey(catKey!!) else null
                    val vm = remember { StudyViewModel(questionRepo, progressRepo) }

                    LaunchedEffect(Unit) { vm.startSession(mode, category) }

                    StudyCardScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Exam tab
                composable(BottomTab.Exam.route) {
                    ExamSetupScreen(
                        questionRepo = questionRepo,
                        progressRepo = progressRepo,
                        onStartExam = { level -> navController.navigate("exam_run/${level.key}") }
                    )
                }

                // Exam run
                composable(
                    "exam_run/{levelKey}",
                    arguments = listOf(navArgument("levelKey") { type = NavType.StringType })
                ) { entry ->
                    val levelKey = entry.arguments?.getString("levelKey") ?: "CSP"
                    val level    = ExamLevel.fromKey(levelKey)
                    val vm       = remember { ExamViewModel(questionRepo, progressRepo) }

                    LaunchedEffect(Unit) { vm.startExam(level) }

                    ExamScreen(
                        viewModel = vm,
                        onQuit = { navController.popBackStack() },
                        onFinished = {
                            navController.navigate("exam_results") {
                                popUpTo("exam_run/${levelKey}") { inclusive = true }
                            }
                        }
                    )
                }

                // Exam results
                composable("exam_results") {
                    val lastResult = progressRepo.current.examResults.firstOrNull()
                    if (lastResult != null) {
                        ResultsScreen(
                            result = lastResult,
                            questionRepo = questionRepo,
                            onDismiss = {
                                navController.navigate(BottomTab.Exam.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

                // Progress/Account tab
                composable(BottomTab.Progress.route) {
                    val vm = remember { ProgressViewModel(questionRepo, progressRepo) }
                    ProgressScreen(viewModel = vm, languageManager = languageManager)
                }
            }
        }
    }
}
