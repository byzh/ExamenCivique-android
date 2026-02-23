package com.examencivique.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.ProgressRepository
import com.examencivique.data.repository.QuestionRepository
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue
import com.examencivique.ui.theme.FrenchRed

@Composable
fun StudyScreen(
    questionRepo: QuestionRepository,
    progressRepo: ProgressRepository,
    onNavigateToCards: (String, String?) -> Unit
) {
    val progress by progressRepo.progress.collectAsState()
    val scrollState = rememberScrollState()
    val s = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(s.studyTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(FrenchBlue, FrenchRed)),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.Flag, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Column {
                        Text(s.studyBannerTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            s.studyBannerSubtitle(questionRepo.allQuestions.size),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Text(s.studyQuickAccess, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickButton(
                modifier = Modifier.weight(1f),
                title = s.studyAll,
                count = "${questionRepo.allQuestions.size}",
                icon = Icons.Filled.List,
                color = FrenchBlue
            ) { onNavigateToCards("ALL", null) }

            QuickButton(
                modifier = Modifier.weight(1f),
                title = s.studyWeak,
                count = "${questionRepo.weakQuestions(progress).size}",
                icon = Icons.Filled.Warning,
                color = Color(0xFFE65100)
            ) { onNavigateToCards("WEAK", null) }

            QuickButton(
                modifier = Modifier.weight(1f),
                title = s.studyNew,
                count = "${questionRepo.unansweredQuestions(progress).size}",
                icon = Icons.Filled.NewReleases,
                color = Color(0xFF2E7D32)
            ) { onNavigateToCards("UNANSWERED", null) }
        }

        Text(s.studyByTheme, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        QuestionCategory.entries.forEach { cat ->
            CategoryRow(
                category = cat,
                count = questionRepo.questionsForCategory(cat).size,
                accuracy = progress.accuracy(cat, questionRepo.allQuestions),
                onClick = { onNavigateToCards("CATEGORY", cat.key) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun QuickButton(
    modifier: Modifier,
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(count, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CategoryRow(
    category: QuestionCategory,
    count: Int,
    accuracy: Double,
    onClick: () -> Unit
) {
    val s = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(category.localizedName(s), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.weight(1f))
                    if (accuracy > 0) {
                        Text(
                            "${(accuracy * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                accuracy >= 0.8 -> Color(0xFF2E7D32)
                                accuracy >= 0.5 -> Color(0xFFE65100)
                                else -> Color(0xFFC62828)
                            }
                        )
                    }
                }
                Text(s.studyQuestionCount(count), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                LinearProgressIndicator(
                    progress = { accuracy.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = category.color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
