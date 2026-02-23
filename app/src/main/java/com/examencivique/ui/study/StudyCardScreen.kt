package com.examencivique.ui.study

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.model.Question
import com.examencivique.data.model.QuestionType
import com.examencivique.ui.i18n.AppLanguage
import com.examencivique.ui.i18n.LocalLanguage
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyCardScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val s = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.questions.isNotEmpty())
                        Text("${state.currentIndex + 1} / ${state.totalCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, s.back)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.questions.isEmpty() -> EmptyState(onBack)
                state.sessionFinished -> FinishedState(
                    totalCount = state.totalCount,
                    onRestart = { viewModel.resetSession() },
                    onBack = onBack
                )
                else -> QuestionCard(state, viewModel)
            }
        }
    }
}

@Composable
private fun QuestionCard(state: StudyState, viewModel: StudyViewModel) {
    val q = state.currentQuestion ?: return
    val s = LocalStrings.current
    val lang = LocalLanguage.current

    Column(modifier = Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { state.progressFraction },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = q.category.color
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(q.category.localizedName(s), q.category.color)
                Badge(
                    if (q.type == QuestionType.SITUATION) s.situation else s.connaissance,
                    if (q.type == QuestionType.SITUATION) Color(0xFF7B1FA2) else Color.Gray
                )
            }

            // Question (French always shown)
            Text(q.question, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
            // Chinese translation below
            if (lang == AppLanguage.ZH && q.questionZh != null) {
                Text(q.questionZh, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
            }

            // Options
            val letters = listOf("A", "B", "C", "D")
            q.options.forEachIndexed { idx, opt ->
                val optZh = if (lang == AppLanguage.ZH) q.optionsZh?.getOrNull(idx) else null
                OptionButton(
                    letter = letters[idx],
                    text = opt,
                    textZh = optZh,
                    index = idx,
                    question = q,
                    selectedIndex = state.selectedOptionIndex,
                    showAnswer = state.showAnswer,
                    onClick = { viewModel.selectOption(idx) }
                )
            }

            // Explanation
            if (state.showAnswer && q.explanation != null) {
                val explZh = if (lang == AppLanguage.ZH) q.explanationZh else null
                ExplanationBox(q.explanation, explZh)
            }
        }

        NavigationFooter(state, viewModel)
    }
}

@Composable
private fun OptionButton(
    letter: String,
    text: String,
    textZh: String?,
    index: Int,
    question: Question,
    selectedIndex: Int?,
    showAnswer: Boolean,
    onClick: () -> Unit
) {
    val isSelected = selectedIndex == index
    val isCorrect  = index == question.correctIndex

    val bgColor by animateColorAsState(
        when {
            !showAnswer -> if (isSelected) FrenchBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            isCorrect   -> Color(0xFF2E7D32).copy(alpha = 0.12f)
            isSelected  -> Color(0xFFC62828).copy(alpha = 0.12f)
            else        -> MaterialTheme.colorScheme.surfaceVariant
        }, label = "optBg"
    )

    val borderColor by animateColorAsState(
        when {
            !showAnswer -> if (isSelected) FrenchBlue else Color.Transparent
            isCorrect   -> Color(0xFF2E7D32)
            isSelected  -> Color(0xFFC62828)
            else        -> Color.Transparent
        }, label = "optBorder"
    )

    val letterBg = when {
        !showAnswer -> if (isSelected) FrenchBlue else Color.Gray.copy(alpha = 0.4f)
        isCorrect   -> Color(0xFF2E7D32)
        isSelected  -> Color(0xFFC62828)
        else        -> Color.Gray.copy(alpha = 0.4f)
    }

    OutlinedCard(
        onClick = onClick,
        enabled = !showAnswer,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor),
            width = 2.dp
        ),
        colors = CardDefaults.outlinedCardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(letterBg),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text, fontSize = 14.sp)
                if (textZh != null) {
                    Text(textZh, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (showAnswer && (isCorrect || isSelected)) {
                Icon(
                    if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ExplanationBox(text: String, textZh: String?) {
    val s = LocalStrings.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100).copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                Text(s.explanation, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFE65100))
            }
            Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            if (textZh != null) {
                Text(textZh, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun NavigationFooter(state: StudyState, viewModel: StudyViewModel) {
    val s = LocalStrings.current
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.previousQuestion() },
                enabled = state.currentIndex > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, s.previous)
            }

            Spacer(Modifier.weight(1f))

            if (state.showAnswer) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue)
                ) {
                    Text(if (state.isLastQuestion) s.finish else s.next)
                    if (!state.isLastQuestion) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.NavigateNext, null, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Text(s.chooseAnswer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = { viewModel.nextQuestion() },
                enabled = !state.isLastQuestion
            ) {
                Icon(Icons.AutoMirrored.Filled.NavigateNext, s.next)
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyState(onBack: () -> Unit) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(s.noQuestions, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(s.tryAnotherCategory, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) { Text(s.back) }
    }
}

@Composable
private fun FinishedState(totalCount: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Star, null, tint = Color(0xFFFBC02D), modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(20.dp))
        Text(s.sessionFinished, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text(s.answeredQuestions(totalCount), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue)) {
            Text(s.restart)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text(s.backToHome) }
    }
}
