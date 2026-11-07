package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QUIZ_QUESTIONS
import com.example.model.QuizQuestion
import com.example.model.SimulationState

@Composable
fun QuizScreen(
    state: SimulationState,
    onTestInSimulation: (SimulationState) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Map of question id to user selected option index
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }

    val answeredCount = selectedAnswers.size
    val correctCount = QUIZ_QUESTIONS.count { q ->
        selectedAnswers[q.id] == q.correctIndex
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Score Banner
        Column {
            Text(
                text = if (state.isEnglish) "Optics Practice & Quiz" else "দর্পণ আলোকবিজ্ঞান কুইজ ও অনুশীলন",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (state.isEnglish) "Test your understanding of image formation, ray tracing & sign convention" else "আলোর প্রতিফলন ও প্রতিবিম্ব গঠনের ধারণা যাচাই করুন",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Score Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("quiz_score_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.isEnglish) "Your Progress" else "আপনার স্কোর",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$correctCount / ${QUIZ_QUESTIONS.size} সঠিক (${answeredCount}টি সম্পন্ন)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (answeredCount > 0) {
                    Button(
                        onClick = { selectedAnswers.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("quiz_reset_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (state.isEnglish) "Reset" else "পুনরায় শুরু", fontSize = 11.sp)
                    }
                }
            }
        }

        // Question Cards
        QUIZ_QUESTIONS.forEachIndexed { qIndex, q ->
            val userChoice = selectedAnswers[q.id]
            val hasAnswered = userChoice != null

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("quiz_question_${q.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Question Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${qIndex + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isEnglish) q.questionEn else q.questionBn,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Options List
                    val options = if (state.isEnglish) q.optionsEn else q.optionsBn
                    options.forEachIndexed { optIndex, optText ->
                        val isCorrectOption = optIndex == q.correctIndex
                        val isUserPick = userChoice == optIndex

                        val bgColor = when {
                            !hasAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            isCorrectOption -> Color(0xFF10B981).copy(alpha = 0.15f)
                            isUserPick -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }

                        val borderColor = when {
                            !hasAnswered -> Color.Transparent
                            isCorrectOption -> Color(0xFF10B981)
                            isUserPick -> MaterialTheme.colorScheme.error
                            else -> Color.Transparent
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedAnswers[q.id] = optIndex
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = optText,
                                fontSize = 12.sp,
                                fontWeight = if (isUserPick || (hasAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                color = if (hasAnswered && isCorrectOption) Color(0xFF047857) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (hasAnswered) {
                                if (isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Correct",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isUserPick) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Incorrect",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Explanation Box if answered
                    AnimatedVisibility(visible = hasAnswered) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.isEnglish) "Explanation:" else "ব্যাখ্যা:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = if (state.isEnglish) q.explanationEn else q.explanationBn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            // Button to test in simulation
                            Button(
                                onClick = {
                                    onTestInSimulation(
                                        state.copy(
                                            mirrorType = q.associatedMirrorType,
                                            objectDistanceU = q.sampleU,
                                            focalLengthF = q.sampleF
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.fillMaxWidth().testTag("quiz_test_sim_btn_${q.id}")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.isEnglish) "Test this in Simulation 🔬" else "সিমুলেশনে পরখ করে দেখুন 🔬",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
