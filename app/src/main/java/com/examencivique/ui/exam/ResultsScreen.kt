package com.examencivique.ui.exam

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.model.ExamResult
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.QuestionRepository
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    result: ExamResult,
    questionRepo: QuestionRepository,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.results) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, s.back)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ScoreCard(result)
            CategoryBreakdown(result, questionRepo)
            ActionButtons(onDismiss)
        }
    }
}

@Composable
private fun ScoreCard(result: ExamResult) {
    val s = LocalStrings.current
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val passColor = if (result.isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
            Box(
                modifier = Modifier.background(passColor, RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        if (result.isPassed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                        null, tint = Color.White, modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (result.isPassed) s.passed else s.failed,
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp
                    )
                }
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                val pct = result.scorePercentage.toFloat()
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 16.dp.toPx()
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val arcOffset = Offset(stroke / 2, stroke / 2)
                    drawArc(Color.Gray.copy(alpha = 0.15f), 0f, 360f, false, arcOffset, arcSize, style = Stroke(stroke))
                    drawArc(passColor, -90f, 360f * pct, false, arcOffset, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${result.score}", fontWeight = FontWeight.Bold, fontSize = 44.sp, color = passColor)
                    Text(s.scoreOutOf(result.totalQuestions), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                SubStat(Modifier.weight(1f), "${(result.scorePercentage * 100).toInt()}%", s.score, passColor)
                SubStat(Modifier.weight(1f), "${result.score}/${result.totalQuestions}", s.correctAnswers)
                SubStat(Modifier.weight(1f), result.formattedDuration, s.duration)
            }

            Text(
                if (result.isPassed) s.passMessage else s.failMessage,
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
            )

            Text(
                s.examLevelLabel(result.level.shortName),
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubStat(modifier: Modifier, value: String, label: String, color: Color = Color.Unspecified) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CategoryBreakdown(result: ExamResult, questionRepo: QuestionRepository) {
    val s = LocalStrings.current
    Text(s.summaryByTheme, fontWeight = FontWeight.Bold, fontSize = 16.sp)

    QuestionCategory.entries.forEach { cat ->
        val catTotal = questionRepo.questionsForCategory(cat).size
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(20.dp))
                Text(cat.localizedName(s), fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text(s.studyQuestionCount(catTotal), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ActionButtons(onDismiss: () -> Unit) {
    val s = LocalStrings.current
    Button(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(s.backToHome, fontWeight = FontWeight.Bold)
    }
}
