package com.futurion.apps.mindmingle.presentation.math_memory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mindmingle.domain.model.Phase

@Composable
fun MathMemoryScreen(
    viewModel: MathMemoryViewModel,
    onExit: () -> Unit
) {
    val uiState = viewModel.uiState.value
    val game = uiState.game
    val level = game.level.number

    var phase by remember {
        mutableStateOf(if (level == 1) Phase.WELCOME else Phase.MEMORIZE)
    }

    if (game.showResult) {
        if (level == 1) {
            ResultTutorialPhase(
                viewModel = viewModel,
                onNext = {
                    viewModel.onAction(MathMemoryAction.NextLevel)
                    phase = Phase.MEMORIZE // move into normal flow
                }
            )
        } else {
            ResultPhase(
                viewModel = viewModel,
                onNext = {
                    viewModel.onAction(MathMemoryAction.NextLevel)
                    phase = Phase.MEMORIZE
                }
            )
        }
        return // ⛔ stop drawing other phases underneath
    }

    when (phase) {
        // ---------------- Tutorial (Level 1 only) ----------------
        Phase.WELCOME -> WelcomeTutorialPhase(onStart = { phase = Phase.MEMORIZE_TUTORIAL })

        Phase.MEMORIZE_TUTORIAL -> MemorizeTutorialPhase(
            viewModel = viewModel,
            onDone = { phase = Phase.WAIT_CONFIRM }
        )

        Phase.WAIT_CONFIRM -> WaitConfirmPhase(
            onProceed = { phase = Phase.SOLVE_TUTORIAL }
        )

        Phase.SOLVE_TUTORIAL -> SolveTutorialPhase(
            viewModel = viewModel,
            onSolved = { phase = Phase.RESULT_TUTORIAL }
        )

        Phase.RESULT_TUTORIAL -> ResultTutorialPhase(
            viewModel = viewModel,
            onNext = {
                // ✅ After tutorial, move to normal gameplay
                viewModel.onAction(MathMemoryAction.NextLevel)
                phase = Phase.MEMORIZE
            }
        )

        // ---------------- Normal Flow (Level ≥ 2) ----------------
        Phase.MEMORIZE -> MemorizePhase(
            viewModel = viewModel,
            onDone = { phase = Phase.SOLVE }
        )

        Phase.SOLVE -> SolvePhase(
            viewModel = viewModel,
            onSolved = { phase = Phase.RESULT }
        )

        Phase.RESULT -> ResultPhase(
            viewModel = viewModel,
            onNext = {
                viewModel.onAction(MathMemoryAction.NextLevel)
                phase = Phase.MEMORIZE
            }
        )
    }
}

// -------------------- Welcome Phase --------------------
@Composable
fun WelcomeTutorialPhase(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1976D2)) // Blue background
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👋 Welcome to Math Memory!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "You’ll learn how to play in this level.",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(48.dp))

            Button(onClick = onStart) {
                Text("Start Tutorial →")
            }
        }
    }
}

// -------------------- Wait Confirm Phase --------------------
@Composable
fun WaitConfirmPhase(onProceed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC107)) // Yellow background
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👍 Got it?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Now let’s try solving a question!",
                fontSize = 18.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(48.dp))

            Button(onClick = onProceed) {
                Text("Continue →")
            }
        }
    }
}


@Composable
fun MemorizeTutorialPhase(
    viewModel: MathMemoryViewModel,
    onDone: () -> Unit
) {
    val state = viewModel.uiState.value.game
    val startNumber = state.level.start

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Step 1: Start with this number!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Yellow, shape = CircleShape)
                .border(3.dp, Color.Red, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = startNumber.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onDone) {
            Text("I Got It →")
        }
    }
}


@Composable
fun MemorizePhase(viewModel: MathMemoryViewModel, onDone: () -> Unit) {
    val game = viewModel.uiState.value.game
    val startNumber = game.level.start

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Memorize: $startNumber")
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDone) { Text("Proceed →") }
    }
}

@Composable
fun SolvePhase(viewModel: MathMemoryViewModel, onSolved: () -> Unit) {
    val options = viewModel.answerOptions

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        options.value.forEach { option ->
            Button(
                onClick = {
                    viewModel.onAction(MathMemoryAction.InputChanged(option.value.toString()))
                    viewModel.onAction(MathMemoryAction.SubmitAnswer)
                    onSolved()
                }
            ) { Text(option.value.toString()) }
        }
    }
}

@Composable
fun ResultPhase(viewModel: MathMemoryViewModel, onNext: () -> Unit) {
    val game = viewModel.uiState.value.game

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (game.isCorrect) {
            Text("✅ Correct!", color = Color.Green)
        } else {
            Text("❌ Wrong!", color = Color.Red)
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onNext) { Text("Next Level →") }
    }
}


@Composable
fun ResultTutorialPhase(
    viewModel: MathMemoryViewModel,
    onNext: () -> Unit
) {
    val game = viewModel.uiState.value.game

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (game.isCorrect) {
            Text(
                text = "🎉 Great job! You solved it!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(Modifier.height(16.dp))
            Text("From next level, no more hints.")
        } else {
            Text(
                text = "❌ Oops, try again!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext) {
            Text("Next Level →")
        }
    }
}

@Composable
fun SolveTutorialPhase(
    viewModel: MathMemoryViewModel,
    onSolved: () -> Unit
) {
    val options = viewModel.answerOptions
    val state = viewModel.uiState.value.game
    val questionText = state.level.number // e.g., "7 + 3 = ?"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Step 2: Solve the question!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(questionText.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        options.value.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        if (option.isCorrect) Color(0xFFB3E5FC) else Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        // ✅ Use ViewModel’s SubmitAnswer
                        viewModel.onAction(MathMemoryAction.InputChanged(option.value.toString()))
                        viewModel.onAction(MathMemoryAction.SubmitAnswer)
                        onSolved()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(option.value.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "✅ Correct Answer → Tap here!",
            color = Color.Green,
            fontWeight = FontWeight.SemiBold
        )
    }
}

