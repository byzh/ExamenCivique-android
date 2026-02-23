package com.examencivique.ui.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.model.ExamLevel
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue

@Composable
fun ExamSetupScreen(
    questionRepo: QuestionRepository,
    progressRepo: ProgressRepository,
    onStartExam: (ExamLevel) -> Unit
) {
    var selectedLevel by remember { mutableStateOf(ExamLevel.CSP) }
    val progress by progressRepo.progress.collectAsState()
    val s = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(s.examTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Info card
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Info, null, tint = FrenchBlue)
                    Text(s.examOfficialFormat, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoCell(Modifier.weight(1f), Icons.Filled.Tag, "40", s.examQuestions, FrenchBlue)
                    InfoCell(Modifier.weight(1f), Icons.Filled.Timer, "45", s.examMinutes, Color(0xFFE65100))
                    InfoCell(Modifier.weight(1f), Icons.Filled.CheckCircle, "32", s.examToPass, Color(0xFF2E7D32))
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletRow(Icons.Filled.Description, s.exam28Connaissance, FrenchBlue)
                    BulletRow(Icons.Filled.PersonSearch, s.exam12Situation, Color(0xFF7B1FA2))
                    BulletRow(Icons.Filled.Percent, s.examPassThreshold, Color(0xFF2E7D32))
                }
            }
        }

        // Level selection
        Text(s.examChooseLevel, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        ExamLevel.entries.forEach { level ->
            val isSelected = selectedLevel == level
            val conn = questionRepo.connaissanceQuestions(level).size
            val sit  = questionRepo.situationQuestions(level).size

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLevel = level }
                    .then(
                        if (isSelected) Modifier.border(2.dp, FrenchBlue, RoundedCornerShape(14.dp))
                        else Modifier
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) FrenchBlue.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) FrenchBlue else Color.Gray.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(modifier = Modifier.size(13.dp).clip(CircleShape).background(FrenchBlue))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(level.shortName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(level.localizedDesc(s), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        Text(s.examLevelDetail(conn, sit), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Past exam stats
        val pastExams = progress.examResults.filter { it.levelKey == selectedLevel.key }
        if (pastExams.isNotEmpty()) {
            Text(s.examHistory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            val last = pastExams.first()
            val passed = pastExams.count { it.isPassed }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMini(Modifier.weight(1f), "${pastExams.size}", s.examExams)
                StatMini(Modifier.weight(1f), "$passed", s.examPassed, Color(0xFF2E7D32))
                StatMini(Modifier.weight(1f), "${(last.scorePercentage * 100).toInt()}%", s.examLast, if (last.isPassed) Color(0xFF2E7D32) else Color(0xFFC62828))
            }
        }

        // Start button
        val canStart = questionRepo.connaissanceQuestions(selectedLevel).size >= 28
                    && questionRepo.situationQuestions(selectedLevel).size >= 12

        Button(
            onClick = { onStartExam(selectedLevel) },
            enabled = canStart,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue)
        ) {
            Icon(Icons.Filled.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text(s.examStart, fontWeight = FontWeight.Bold)
        }

        if (!canStart) {
            Text(s.examNotEnough, fontSize = 12.sp, color = Color(0xFFC62828))
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun InfoCell(modifier: Modifier, icon: ImageVector, value: String, label: String, color: Color) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BulletRow(icon: ImageVector, text: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatMini(modifier: Modifier, value: String, label: String, color: Color = Color.Unspecified) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
