package com.examencivique.ui.progress

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import com.examencivique.data.model.ExamResult
import com.examencivique.data.model.QuestionCategory
import com.examencivique.data.repository.AuthRepository
import com.examencivique.data.repository.LanguageManager
import com.examencivique.ui.i18n.AppLanguage
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    languageManager: LanguageManager,
    authRepo: AuthRepository,
    onLoggedOut: () -> Unit
) {
    val progress by viewModel.progress.collectAsState()
    val s = LocalStrings.current
    val currentLang by languageManager.language.collectAsState()
    val currentUser by authRepo.currentUser.collectAsState()
    var showResetAlert by remember { mutableStateOf(false) }
    var showLogoutAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(s.accountTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = { showResetAlert = true }) {
                Icon(Icons.Filled.Delete, s.reset, tint = Color(0xFFC62828))
            }
        }

        // User info card
        if (currentUser != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FrenchBlue.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(FrenchBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, null, tint = FrenchBlue, modifier = Modifier.size(24.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            currentUser?.displayName ?: currentUser?.email ?: "",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        if (currentUser?.displayName != null && currentUser?.email != null) {
                            Text(currentUser!!.email!!, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { showLogoutAlert = true }) {
                        Icon(Icons.Filled.Logout, s.authLogout, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Language switcher (Modern Segmented Picker style)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Language, null, tint = FrenchBlue, modifier = Modifier.size(18.dp))
                    Text(s.language, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = currentLang == lang
                            val backgroundColor by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                                label = "bg"
                            )
                            val textColor by animateColorAsState(
                                if (isSelected) FrenchBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "text"
                            )
                            val elevation by animateDpAsState(
                                if (isSelected) 2.dp else 0.dp,
                                label = "elev"
                            )

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { languageManager.setLanguage(lang) },
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = elevation
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overview card
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(s.overview, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                        CircularProgressIndicator(
                            progress = { progress.overallAccuracy.toFloat() },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 8.dp,
                            color = accuracyColor(progress.overallAccuracy),
                            trackColor = Color.Gray.copy(alpha = 0.1f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${(progress.overallAccuracy * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(s.accuracy, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.width(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                        StatRow(Icons.Filled.CheckCircle, Color(0xFF2E7D32), s.correctAnswersLabel, "${progress.totalCorrect}")
                        StatRow(Icons.Filled.Description, FrenchBlue, s.totalAttempts, "${progress.totalAttempts}")
                        StatRow(Icons.Filled.Star, Color(0xFFFBC02D), s.masteredQuestions, "${progress.masteredCount}")
                        StatRow(Icons.Filled.EmojiEvents, Color(0xFFE65100), s.examsPassed, "${progress.examsPassedCount}")
                    }
                }
            }
        }

        // Per-category
        Text(s.byTheme, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        QuestionCategory.entries.forEach { cat ->
            val acc   = viewModel.categoryAccuracy(cat)
            val tried = viewModel.triedCount(cat)
            val total = viewModel.questionRepo.questionsForCategory(cat).size

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(cat.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(22.dp))
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.localizedName(s), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            if (tried > 0) {
                                Text("${(acc * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accuracyColor(acc))
                            } else {
                                Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { acc.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = accuracyColor(acc),
                            trackColor = Color.Gray.copy(alpha = 0.1f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )

                        Text(s.questionsTried(tried, total), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Exam history
        Text(s.examHistoryTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        if (progress.examResults.isEmpty()) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Filled.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                    Text(s.noExamsTaken, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            progress.examResults.take(10).forEach { result ->
                ExamHistoryRow(result)
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showResetAlert) {
        AlertDialog(
            onDismissRequest = { showResetAlert = false },
            title = { Text(s.resetProgressTitle) },
            text = { Text(s.resetProgressBody) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetProgress(); showResetAlert = false }) {
                    Text(s.resetConfirm, color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAlert = false }) { Text(s.cancel) }
            }
        )
    }

    if (showLogoutAlert) {
        AlertDialog(
            onDismissRequest = { showLogoutAlert = false },
            title = { Text(s.authLogoutConfirmTitle) },
            text = { Text(s.authLogoutConfirmBody) },
            confirmButton = {
                TextButton(onClick = { authRepo.signOut(); showLogoutAlert = false; onLoggedOut() }) {
                    Text(s.authLogout, color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutAlert = false }) { Text(s.cancel) }
            }
        )
    }
}

@Composable
private fun StatRow(icon: ImageVector, color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = color.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun ExamHistoryRow(result: ExamResult) {
    val s = LocalStrings.current
    val passColor = if (result.isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
    val dateStr = remember(result.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(result.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(passColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (result.isPassed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    null, tint = passColor, modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = FrenchBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            result.level.shortName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = FrenchBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${result.score}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = passColor)
                    Text("/${result.totalQuestions}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))

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
