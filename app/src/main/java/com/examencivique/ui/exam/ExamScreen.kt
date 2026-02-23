package com.examencivique.ui.exam

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.examencivique.data.model.QuestionType
import com.examencivique.ui.i18n.AppLanguage
import com.examencivique.ui.i18n.LocalLanguage
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    onQuit: () -> Unit,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val s = LocalStrings.current
    val lang = LocalLanguage.current
    var showQuitDialog   by remember { mutableStateOf(false) }
    var showSubmitDialog  by remember { mutableStateOf(false) }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    BackHandler { showQuitDialog = true }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ExamHeader(state, onQuitClick = { showQuitDialog = true })

            val q = state.currentQuestion
            if (q != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(q.question, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
                    if (lang == AppLanguage.ZH && q.questionZh != null) {
                        Text(q.questionZh, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                    }

                    val letters = listOf("A", "B", "C", "D")
                    q.options.forEachIndexed { idx, opt ->
                        val optZh = if (lang == AppLanguage.ZH) q.optionsZh?.getOrNull(idx) else null
                        ExamOptionButton(
                            letter = letters[idx],
                            text = opt,
                            textZh = optZh,
                            isSelected = viewModel.selectedIndex(q) == idx,
                            onClick = { viewModel.selectAnswer(idx) }
                        )
                    }
                }
            }

            ExamFooter(
                state = state,
                onPrevious = { viewModel.goPrevious() },
                onNext = { viewModel.goNext() },
                onSubmit = { showSubmitDialog = true },
                onDotClick = { viewModel.goTo(it) }
            )
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text(s.quitExamTitle) },
            text = { Text(s.quitExamBody) },
            confirmButton = {
                TextButton(onClick = { viewModel.reset(); onQuit() }) {
                    Text(s.quit, color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text(s.continueExam) }
            }
        )
    }

    if (showSubmitDialog) {
        val unanswered = state.totalQuestions - state.answeredCount
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text(s.submitExamTitle) },
            text = {
                Text(
                    if (unanswered > 0) s.submitUnanswered(unanswered)
                    else s.submitAllAnswered
                )
            },
            confirmButton = {
                TextButton(onClick = { showSubmitDialog = false; viewModel.submitExam() }) {
                    Text(s.submit, color = Color(0xFF2E7D32))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) { Text(s.cancel) }
            }
        )
    }
}

@Composable
private fun ExamHeader(state: ExamState, onQuitClick: () -> Unit) {
    val q = state.currentQuestion
    val s = LocalStrings.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onQuitClick, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Close, s.quit, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.weight(1f))
            Text("${state.currentIndex + 1} / ${state.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))

            val timerColor = if (state.isTimeCritical) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
            val timerBg    = if (state.isTimeCritical) Color(0xFFC62828).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            Row(
                modifier = Modifier.background(timerBg, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Filled.Timer, null, tint = timerColor, modifier = Modifier.size(14.dp))
                Text(state.formattedTime, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = timerColor)
            }
        }

        if (q != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val typeColor = if (q.type == QuestionType.SITUATION) Color(0xFF7B1FA2) else FrenchBlue
                Box(modifier = Modifier.background(typeColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(if (q.type == QuestionType.SITUATION) s.situation else s.connaissance, fontSize = 11.sp, color = typeColor, fontWeight = FontWeight.SemiBold)
                }
                Box(modifier = Modifier.background(q.category.color.copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(q.category.localizedName(s), fontSize = 11.sp, color = q.category.color)
                }
                Spacer(Modifier.weight(1f))
                Text(s.examAnswered(state.answeredCount, state.totalQuestions), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { state.progressFraction },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = FrenchBlue
        )
    }
}

@Composable
private fun ExamOptionButton(letter: String, text: String, textZh: String?, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        if (isSelected) FrenchBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant, label = "eBg"
    )
    val borderColor by animateColorAsState(
        if (isSelected) FrenchBlue else Color.Transparent, label = "eBorder"
    )

    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor), width = 2.dp
        ),
        colors = CardDefaults.outlinedCardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(if (isSelected) FrenchBlue else Color.Gray.copy(alpha = 0.4f)),
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
            if (isSelected) {
                Icon(Icons.Filled.CheckCircle, null, tint = FrenchBlue, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ExamFooter(
    state: ExamState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onDotClick: (Int) -> Unit
) {
    val s = LocalStrings.current
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = state.currentIndex > 0,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.NavigateBefore, null, modifier = Modifier.size(18.dp))
                Text(s.prevShort, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))

            val step = maxOf(1, state.totalQuestions / 10)
            val indices = (0 until state.totalQuestions step step).toList()
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                indices.forEach { i ->
                    val q = state.questions.getOrNull(i)
                    val isCurrent  = i == state.currentIndex
                    val isAnswered = q != null && state.selectedAnswers.containsKey(q.id)
                    val dotColor = when {
                        isCurrent  -> FrenchBlue
                        isAnswered -> Color(0xFF2E7D32).copy(alpha = 0.7f)
                        else       -> Color.Gray.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .clickable { onDotClick(i) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            if (state.currentIndex < state.totalQuestions - 1) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(s.nextShort, fontSize = 13.sp)
                    Icon(Icons.AutoMirrored.Filled.NavigateNext, null, modifier = Modifier.size(18.dp))
                }
            } else {
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(s.submit, fontSize = 13.sp)
                }
            }
        }
    }
}
