package com.futurion.apps.mindmingle.presentation.math_memory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.AnswerOption
import com.futurion.apps.mindmingle.domain.model.GameTheme
import com.futurion.apps.mindmingle.domain.model.MemoryCard
import com.futurion.apps.mindmingle.domain.model.Op

@Composable
fun MathMemoryScreenV2(
    startValue: Int,
    moves: List<MemoryCard>,
    answerOptions: List<AnswerOption>,
    streak: Int,
    bestStreak: Int,
    xp: Int,
    coins: Int,
    phase: String, // "MEMORIZE", "SOLVE", "RESULT"
    selectedTheme: GameTheme,
    userInput: String,
    isCorrect: Boolean,
    onSelectOption: (AnswerOption) -> Unit,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    showTip: Boolean,
    calculationBreakdown: String // "Started at 5, +2=7, x3=21"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Persistent Instructions
        Text(
            text = "Start at $startValue. Do each move left-to-right. Select the answer below!",
            style = MaterialTheme.typography.titleMedium,
            color = selectedTheme.textColor,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9F7EF))
                .padding(10.dp)
        )

        Spacer(Modifier.height(14.dp))

        // Visual Flow: Start Number + Moves + Arrows
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            StartNumberCircles(startValue, selectedTheme.buttonTextColor, selectedTheme.buttonColor)

            moves.forEach { card ->
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward, // your arrow asset
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
                CardStep(card, selectedTheme.textColor)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tip/Helper Text
        if (phase == "SOLVE") {
            Text(
                text = "Tip: Work left-to-right, no BODMAS!",
                color = Color(0xFF4682B4),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Answer Options
        if (phase == "SOLVE") {
            AnswerOptionsColumns(
                options = answerOptions,
                onSelect = onSelectOption,
                selectedTheme = selectedTheme
            )
        }

        // Result & Stats
        if (phase == "RESULT") {
            ResultSectionV2(
                isCorrect = isCorrect,
                onNext = onNextLevel,
                onRetry = onRetry,
                streak = streak,
                bestStreak = bestStreak,
                xp = xp,
                coins = coins,
                textColor = selectedTheme.textColor,
                buttonColor = selectedTheme.buttonColor,
                buttonTextColor = selectedTheme.buttonTextColor,
                breakdown = calculationBreakdown
            )
        }
    }
}

@Composable
fun StartNumberCircles(value: Int, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.dp, textColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = value.toString(), color = textColor, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CardStep(card: MemoryCard, textColor: Color) {
    // compact card step, e.g., +2, -3
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.93f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (card.op) {
                Op.ADD -> "+${card.value}"
                Op.SUB -> "-${card.value}"
                Op.MUL -> "×${card.value}"
                Op.DIV -> "÷${card.value}"
            },
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun AnswerOptionsColumns(
    options: List<AnswerOption>,
    onSelect: (AnswerOption) -> Unit,
    selectedTheme: GameTheme
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            Button(
                onClick = { onSelect(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(selectedTheme.buttonColor)
            ) {
                Text(text = option.value.toString(), color = selectedTheme.buttonTextColor)
            }
        }
    }
}

@Composable
fun ResultSectionV2(
    isCorrect: Boolean,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    streak: Int,
    bestStreak: Int,
    xp: Int,
    coins: Int,
    textColor: Color,
    buttonColor: Color,
    buttonTextColor: Color,
    breakdown: String
) {
    val resultColor = if (isCorrect) Color(0xFF43A047) else Color(0xFFE53935)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (isCorrect) "🎉 Correct!" else "Try Again!",
            color = resultColor,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(breakdown, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Spacer(Modifier.height(18.dp))
        // Stats row
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatChips("Streak", streak)
            StatChips("Best", bestStreak)
            StatChips("XP", xp)
            StatChips("Coins", coins)
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = if (isCorrect) onNext else onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isCorrect) "Next Level" else "Restart", color = buttonTextColor)
        }
    }
}

@Composable
fun StatChips(label: String, value: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(64.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value.toString(), style = MaterialTheme.typography.titleMedium, color = Color.Black)
        }
    }
}
