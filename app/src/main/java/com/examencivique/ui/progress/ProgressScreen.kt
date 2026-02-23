package com.examencivique.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.model.ExamResult
import com.examencivique.data.model.QuestionCategory
import com.examencivique.ui.theme.FrenchBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val progress by viewModel.progress.collectAsState()
    var showResetAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mes Progrès", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = { showResetAlert = true }) {
                Icon(Icons.Filled.Delete, "Réinitialiser", tint = Color(0xFFC62828))
            }
        }

        // Overview card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Vue d'ensemble", fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Accuracy ring
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                        CircularProgressIndicator(
                            progress = { progress.overallAccuracy.toFloat() },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 10.dp,
                            color = accuracyColor(progress.overallAccuracy),
                            trackColor = Color.Gray.copy(alpha = 0.15f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${(progress.overallAccuracy * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("exact.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        StatRow(Icons.Filled.CheckCircle, Color(0xFF2E7D32), "Bonnes réponses", "${progress.totalCorrect}")
                        StatRow(Icons.Filled.Description, FrenchBlue, "Tentatives totales", "${progress.totalAttempts}")
                        StatRow(Icons.Filled.Star, Color(0xFFFBC02D), "Questions maîtrisées", "${progress.masteredCount}")
                        StatRow(Icons.Filled.EmojiEvents, Color(0xFFE65100), "Examens réussis", "${progress.examsPassedCount}")
                    }
                }
            }
        }

        // Per-category
        Text("Par thème", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        QuestionCategory.entries.forEach { cat ->
            val acc   = viewModel.categoryAccuracy(cat)
            val tried = viewModel.triedCount(cat)
            val total = viewModel.questionRepo.questionsForCategory(cat).size

            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(cat.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(20.dp))
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            if (tried > 0) {
                                Text("${(acc * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accuracyColor(acc))
                            } else {
                                Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { acc.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = accuracyColor(acc),
                            trackColor = Color.Gray.copy(alpha = 0.12f)
                        )

                        Text("$tried/$total questions essayées", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Exam history
        Text("Historique des examens", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        if (progress.examResults.isEmpty()) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                    Text("Aucun examen blanc effectué", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            progress.examResults.take(10).forEach { result ->
                ExamHistoryRow(result)
            }
        }

        Spacer(Modifier.height(8.dp))
    }

    // Reset dialog
    if (showResetAlert) {
        AlertDialog(
            onDismissRequest = { showResetAlert = false },
            title = { Text("Réinitialiser la progression ?") },
            text = { Text("Toute votre progression (questions répondues et historique d'examens) sera effacée.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetProgress(); showResetAlert = false }) {
                    Text("Réinitialiser", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAlert = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun StatRow(icon: ImageVector, color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ExamHistoryRow(result: ExamResult) {
    val passColor = if (result.isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
    val dateStr = remember(result.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(result.timestamp))
    }

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(passColor.copy(alpha = 0.25f)), width = 1.5.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (result.isPassed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                null, tint = passColor, modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(FrenchBlue.copy(alpha = 0.1f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(result.level.shortName, fontSize = 11.sp, color = FrenchBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${result.score}/${result.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = passColor)
                    Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${(result.scorePercentage * 100).toInt()}%", fontSize = 14.sp, color = passColor)
                    Spacer(Modifier.weight(1f))
                    Text(result.formattedDuration, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun accuracyColor(acc: Double): Color = when {
    acc >= 0.8 -> Color(0xFF2E7D32)
    acc >= 0.5 -> Color(0xFFE65100)
    else       -> Color(0xFFC62828)
}
