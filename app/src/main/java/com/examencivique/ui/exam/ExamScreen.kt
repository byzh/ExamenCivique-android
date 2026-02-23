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
import com.examencivique.data.model.Question
import com.examencivique.data.model.QuestionType
import com.examencivique.ui.theme.FrenchBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    onQuit: () -> Unit,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showQuitDialog   by remember { mutableStateOf(false) }
    var showSubmitDialog  by remember { mutableStateOf(false) }

    // If exam finishes (time or submit), navigate to results
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    BackHandler { showQuitDialog = true }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            ExamHeader(state, onQuitClick = { showQuitDialog = true })

            // Question content
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

                    val letters = listOf("A", "B", "C", "D")
                    q.options.forEachIndexed { idx, opt ->
                        ExamOptionButton(
                            letter = letters[idx],
                            text = opt,
                            isSelected = viewModel.selectedIndex(q) == idx,
                            onClick = { viewModel.selectAnswer(idx) }
                        )
                    }
                }
            }

            // Footer
            ExamFooter(
                state = state,
                onPrevious = { viewModel.goPrevious() },
                onNext = { viewModel.goNext() },
                onSubmit = { showSubmitDialog = true },
                onDotClick = { viewModel.goTo(it) }
            )
        }
    }

    // Quit dialog
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quitter l'examen ?") },
            text = { Text("Votre progression sera perdue.") },
            confirmButton = {
                TextButton(onClick = { viewModel.reset(); onQuit() }) {
                    Text("Quitter", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Continuer") }
            }
        )
    }

    // Submit dialog
    if (showSubmitDialog) {
        val unanswered = state.totalQuestions - state.answeredCount
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Soumettre l'examen ?") },
            text = {
                Text(
                    if (unanswered > 0)
                        "Vous n'avez pas répondu à $unanswered question(s). Elles seront comptées comme incorrectes."
                    else
                        "Vous avez répondu à toutes les questions. Confirmez-vous ?"
                )
            },
            confirmButton = {
                TextButton(onClick = { showSubmitDialog = false; viewModel.submitExam() }) {
                    Text("Soumettre", color = Color(0xFF2E7D32))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun ExamHeader(state: ExamState, onQuitClick: () -> Unit) {
    val q = state.currentQuestion
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quit
            IconButton(onClick = onQuitClick, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.Close, "Quitter", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.weight(1f))

            // Counter
            Text("${state.currentIndex + 1} / ${state.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(Modifier.weight(1f))

            // Timer
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

        // Type badges + answered count
        if (q != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val typeColor = if (q.type == QuestionType.SITUATION) Color(0xFF7B1FA2) else FrenchBlue
                Box(modifier = Modifier.background(typeColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(if (q.type == QuestionType.SITUATION) "Situation" else "Connaissance", fontSize = 11.sp, color = typeColor, fontWeight = FontWeight.SemiBold)
                }
                Box(modifier = Modifier.background(q.category.color.copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(q.category.displayName, fontSize = 11.sp, color = q.category.color)
                }
                Spacer(Modifier.weight(1f))
                Text("${state.answeredCount}/${state.totalQuestions} répondues", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun ExamOptionButton(letter: String, text: String, isSelected: Boolean, onClick: () -> Unit) {
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
            Text(text, modifier = Modifier.weight(1f), fontSize = 14.sp)
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
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous
            OutlinedButton(
                onClick = onPrevious,
                enabled = state.currentIndex > 0,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.NavigateBefore, null, modifier = Modifier.size(18.dp))
                Text("Préc.", fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))

            // Dots
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

            // Next or Submit
            if (state.currentIndex < state.totalQuestions - 1) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Suiv.", fontSize = 13.sp)
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
                    Text("Soumettre", fontSize = 13.sp)
                }
            }
        }
    }
}
