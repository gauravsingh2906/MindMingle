package com.futurion.apps.mathmingle.presentation.algebra


import android.app.Activity
import android.util.Log
import com.futurion.apps.mathmingle.R
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.futurion.apps.mathmingle.GoogleRewardedAdManager
import com.futurion.apps.mathmingle.domain.model.UniversalResult
import com.futurion.apps.mathmingle.presentation.game_result.GameResultViewModel
import com.futurion.apps.mathmingle.presentation.utils.Constants
import com.google.codelab.gamingzone.presentation.games.algebra.Question
import kotlinx.coroutines.delay

@Composable
fun AlgebraGameScreen(
    onBackPressed: () -> Unit,
    viewModel: AlgebraViewModel = hiltViewModel(),
    onBack: () -> Unit,
    naviagteToResultScreen: (UniversalResult) -> Unit,
    naviagteToDialogScreen: (UniversalResult) -> Unit,
    gameResultModel: GameResultViewModel = hiltViewModel(),
) {
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    val specialLevels = setOf(5, 10, 14, 20, 28, 35) // your special levels
    var showCoinDialog by remember { mutableStateOf(false) }
    var earnedCoins by remember { mutableStateOf(0) }
    var navigationPending by remember { mutableStateOf(false) }


    var shouldLoadResult by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }


    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit? Your progress will be saved to resume later.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.endGame()
                    shouldLoadResult=true
                    shouldNavigate = true
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // States from ViewModel
    val question by viewModel.question.collectAsState()
    val score by viewModel.score.collectAsState()
    val level by viewModel.level.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val isGameOver by viewModel.gameOver.collectAsState()  //
    val levelCompleted by viewModel.levelCompleted.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState() //

    val context = LocalContext.current
    val activity = context as? Activity

    var textAnswer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var feedback by remember { mutableStateOf<String?>(null) }
    val focus = LocalFocusManager.current

    LaunchedEffect(isGameOver) {
        Log.e("Game", isGameOver.toString() + "changes")
    }

    // 🎵 SoundPool
    val soundPool = rememberSoundPool()
//    val winSound = rememberSound(context, soundPool, R.raw.win_sound)
//    val loseSound = rememberSound(context, soundPool, R.raw.lose_sound)
//    val tickSound = rememberSound(context, soundPool, R.raw.tick_sound)

    //  val soundPool = rememberSoundPool()

    val winSoundId = remember { soundPool.load(context, R.raw.game_completed, 1) }
    val loseSoundId = remember { soundPool.load(context, R.raw.game_over, 1) }
    val clickSound = remember { soundPool.load(context, R.raw.click_sound, 1) }

    val timer12SoundId = remember { soundPool.load(context, R.raw.tensecond, 1) }
    val timer3SoundId = remember { soundPool.load(context, R.raw.three_seconds, 1) }

    // Keep track of whether the sounds already played to avoid repetition
    var played12 by remember { mutableStateOf(false) }
    var played3 by remember { mutableStateOf(false) }
    var streamId by remember { mutableStateOf(0) }
    var isLoopPlaying by remember { mutableStateOf(false) }

//    LaunchedEffect(timeRemaining) {
//        if (timeRemaining in 1..5 && !isLoopPlaying) {
//            // Start the looping ticking sound once when entering last 3 seconds
//            streamId = soundPool.play(timer3SoundId, 1f, 1f, 1, -1, 1f)
//            isLoopPlaying = true
//        } else if ((timeRemaining <= 0 || viewModel.gameOver.value) && isLoopPlaying) {
//            // Stop the looping sound when timer ends or game is over
//            soundPool.stop(streamId)
//            streamId = 0
//            isLoopPlaying = false
//        }
//    }
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }


    LaunchedEffect(timeRemaining) {
        if (timeRemaining in 1..3) {
            soundPool.play(timer3SoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    val adManager = remember { GoogleRewardedAdManager(context, Constants.AD_Unit) }
    // Facebook ad manager
    //  val facebookAdManager = remember { FacebookRewardedAdManager(context) }

    val adMobAdUnitId =
        "ca-app-pub-3940256099942544/5224354917"         // Your AdMob rewarded ad unit
    val facebookPlacementId = "xxxxxxxx"               // Your FB placement ID

    var showHint by remember { mutableStateOf(false) }


//    LaunchedEffect(levelCompleted) {
//        if (levelCompleted) {
//          //  viewModel.levelCompleted()
//            // onBack()
//        }
//    }

    LaunchedEffect(Unit) { viewModel.startNext() }

    var showAnimation by remember { mutableStateOf(false) }

    var showCompletedDialog by remember { mutableStateOf(false) }

    var showResultDialog by remember { mutableStateOf(false) }
//    LaunchedEffect(levelCompleted) {
//        showAnimation = true
//    }

    LaunchedEffect(timeRemaining) {
        if (timeRemaining <= 0) {
            Log.d("TimeoutNavTest", "Timer zero, forcing navigation")
            shouldLoadResult = true
            shouldNavigate = true
        }
    }




    LaunchedEffect(gameResult) {
        Log.e("Final", "$gameResult")
        gameResult?.let { result ->
            if (result.won) {
                Log.e("Final", "Won")
                soundPool.play(winSoundId, 1f, 1f, 1, 0, 1f)
                // Step 1: Start animation first
                showAnimation = true
                showResultDialog = false

                // Step 2: Wait for animation duration (~2s or match your lottie file)
                delay(3000)
//                Log.e("Result",viewModel.userId.value ?: "guhgu")
//                val result = gameResultModel.loadResult(viewModel.userId.value ?: "wew","algebra")
//                Log.e("Result",result.toString())
//                gameResultModel.resultState.collect { resultData ->
//                    Log.e("Result",resultData.toString())
//                    if (resultData != null) {
//                        naviagteToResultScreen()
//                    }
//                }

                shouldLoadResult = true
                // Step 3: Hide animation, show dialog
                showAnimation = false
                //   showResultDialog = true  .. important

                val currentLevel = viewModel.level.value

                if (specialLevels.contains(currentLevel)) {
                    earnedCoins = viewModel.coinsEarned.value
                    showCoinDialog = true
                    navigationPending = true
                    shouldNavigate = false
                } else {
                    showCoinDialog = false
                    navigationPending = false
                    shouldNavigate = true
                }

            } else {
                soundPool.play(loseSoundId, 1f, 1f, 1, 0, 1f)
                // If lost → show dialog directly
                //     gameResultModel.loadResult(viewModel.userId.value ?: "wew","algebra")
                //  showResultDialog = true important
                //   naviagteToResultScreen()
                delay(500)
                shouldLoadResult = true
                shouldNavigate=true
            }
            shouldLoadResult = true
            shouldNavigate = true
        }
    }

    if (showCoinDialog) {
        CoinClaimDialog(
            coinsEarned = earnedCoins,
            onClaim = {
                showCoinDialog = false
                navigationPending = false
                shouldNavigate = true
            },
            onDismiss = {
                showCoinDialog = false
                shouldNavigate = true
            }
        )
    }

    LaunchedEffect(shouldLoadResult) {
        if (shouldLoadResult) {
            Log.e("Result", viewModel.userId.value ?: "no userId")
            gameResultModel.loadResult(
                viewModel.userId.value ?: "5c3e1fce-b077-4c55-8cd2-8e718a4560c9", "algebra",
                algebraScore = score
            )
            shouldLoadResult = false // Reset flag
       //     shouldNavigate = true // Set navigation flag //important

            if (specialLevels.contains(level) && gameResult?.won == true) {
                // Wait for user to confirm, don't navigate automatically
                shouldNavigate = false
            } else {
                // For other levels, navigate immediately
                shouldNavigate = true
            }

        }
    }
    val resultData = gameResultModel.resultState.collectAsState().value


    LaunchedEffect(shouldNavigate, resultData) {
        if (shouldNavigate && resultData != null) {
            Log.e("Result", "Navigating with data: $resultData")
            naviagteToResultScreen(resultData)
            shouldNavigate = false // Reset flag
        }
    }

    // Navigate with the already loaded data
//    LaunchedEffect(resultData, hasLoadedResult) {
//        if (hasLoadedResult && resultData != null) {
//            Log.e("Result", "Ready to navigate: $resultData")
//            naviagteToResultScreen(resultData) // Pass the data directly
//        }
//    }


    val value = score compareTo (level * 100)

    val total = (level + 1) * 100

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFE3F2FD), Color(0xFFFAF8FF)
                        )
                    )
                )
                .padding(12.dp)
                .systemBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ---------- HUD ----------
                GameHud(
                    level = level,
                    score = score,
                    timeLeft = timeRemaining,
                    onBack = {
                        showExitDialog = true
                    }
                )

                LinearProgressIndicator(
                    progress = (score / total.toFloat()).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )


                // ---------- Question Card ----------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(6.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Question Title
                        Text(
                            text = "Solve this!",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Color(0xFF2C3E50)
                        )

                        Spacer(Modifier.height(8.dp))

                        // Big Question Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF6F7FB))
                                .border(
                                    1.dp, Color(0xFFE6E8F0), RoundedCornerShape(20.dp)
                                ), contentAlignment = Alignment.TopCenter
                        ) {
                            when (val q = question) {
                                is Question.MissingNumber -> MissingNumberCard(
                                    q = q,
                                    textAnswer = textAnswer,
                                    onTextChange = { textAnswer = it },
                                    onSubmit = { ans ->
                                        focus.clearFocus()
                                        val correct = ans == q.answer
                                        viewModel.submitAnswer(ans)
                                        feedback =
                                            if (correct) "Great job! you done hard part" else "Oops! Answer: ${q.answer}"
                                        textAnswer = ""
                                    })

                                is Question.MissingOperator -> MissingOperatorCard(q = q) { op ->
                                    val correct = op == q.answer
                                    viewModel.submitAnswer(op)
                                    feedback = if (correct) "Nice!" else "Not quite!"
                                }

                                is Question.TrueFalse -> TrueFalseCard(q = q) { choice ->
                                    val correct = choice == q.isCorrect
                                    viewModel.submitAnswer(choice)
                                    feedback = if (correct) "Correct!" else "Wrong!"
                                }

                                is Question.Reverse -> ReverseCard(q = q) { op ->
                                    val correct = op == q.answer
                                    viewModel.submitAnswer(op)
                                    feedback = if (correct) "Awesome!" else "Try again!"
                                }

                                is Question.Mix -> MixCard(
                                    q = q,
                                    textAnswer = textAnswer,
                                    onTextChange = { textAnswer = it },
                                    onSubmitMissing = { ans, correctAns ->
                                        focus.clearFocus()
                                        viewModel.submitAnswer(ans)
                                        feedback =
                                            if (ans == correctAns) "Great!" else "Answer: $correctAns"
                                        textAnswer = ""
                                    },
                                    onSubmitOp = { op, correctOp ->
                                        viewModel.submitAnswer(op)
                                        feedback = if (op == correctOp) "Nice!" else "Oops!"
                                    },
                                    onSubmitTF = { choice, correct ->
                                        viewModel.submitAnswer(choice)
                                        feedback =
                                            if (choice == correct) "Correct!" else "Wrong!"
                                    })

                                else -> {
                                    Log.e("MathGame", "Unexpected question type: $q")
                                }
                            }
                        }

                        // Feedback Toast (animated)
                        FeedbackPill(feedback = feedback)
                    }
                }

                // Bottom actions
                BottomBar(
                    onHint = {
                        if (activity != null) {
                            adManager.showRewardedAd(
                                activity,
                                onUserEarnedReward = {
                                    showHint = true
                                    viewModel.useHint()
                                },
                                onClosed = {
                                    // Optional: show a message if ad wasn’t ready
                                    if (!showHint) {
                                        Toast.makeText(
                                            context,
                                            "Ad not ready, please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    },
                    onRestart = { viewModel.startGame() },
                    onPause = { /* hook if you add pause modal */ }
                )

            }


//                if (levelCompleted && showAnimation) {
//                    GameWinAnimation(
//                        onAnimationFinished = {
//                            showAnimation = false
//                            showCompletedDialog = true
//                        }
//                    )
//                }

//                if (levelCompleted && showCompletedDialog && !showAnimation) {
//                    LevelCompletedDialog(
//                        level = level,
//                        earnedScore = score,
//                        onNextLevel = {
//                            viewModel.levelCompleted() // unlock in Room
//                            viewModel.setLevel(level + 1)
//                            viewModel.startGame()
//                            // naviagteToGameScreen(viewModel.level.value)
//                        },
//                        onReplay = {
//                            viewModel.setLevel(level)
//                            viewModel.startGame()
//                        },
//                        onHome = {
//                            viewModel.levelCompleted()
//                            onBack()
//                        }
//                    )
//                }

            if (showHint) {
                Text(
                    "Hint: think step-by-step!",
                    color = Color(0xFFAD1457),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            }

        }

        if (showAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)), // transparent background
                contentAlignment = Alignment.Center
            ) {
                GameWinAnimation() // 🎉 full screen animation
            }
        }

    }

}


@Composable
fun GameWinAnimation(
    onAnimationFinished: () -> Unit = {}
) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.winner) // put your Lottie JSON in res/raw
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1, // play once
        speed = 1.2f,
        restartOnPlay = false
    )



    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    )


    LaunchedEffect(progress) {
        if (progress >= 1f) {
            onAnimationFinished()
        }

    }

}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GameHud(
    level: Int, score: Int, timeLeft: Int, onBack: () -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // Top row: Back + Title
        Row(
            Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack

            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }

            Text(
                "Algebra Quest",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.width(64.dp)) // balance back button
        }

        // Stat row: Level • Score • Time
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatChip(label = "Level", value = "$level")
            AnimatedContent(
                targetState = score,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "scoreAnim"
            ) { sc ->
                StatChip(label = "Score", value = "$sc")
            }

            Spacer(Modifier.weight(1f))

            // Timer bar + number
            Column(horizontalAlignment = Alignment.End) {
                Text("Time", fontSize = 12.sp, color = Color(0xFF666A7A))
                LinearProgressIndicator(
                    progress = {
                        (timeLeft.coerceAtLeast(0) / maxOf(
                            1f, timeLeft.coerceAtLeast(1).toFloat()
                        ))
                    },
                    // we show a full bar shrinking using width animation below
                    modifier = Modifier
                        .width(160.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Text("$timeLeft s", fontSize = 12.sp, color = Color(0xFF424656))
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFEEF2FF))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF5B6BFF))
        Spacer(Modifier.width(6.dp))
        Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
    }
}

/* ================  QUESTION CARDS  ================= */

@Composable
private fun MissingNumberCard(
    q: Question.MissingNumber,
    textAnswer: String,
    onTextChange: (String) -> Unit,
    onSubmit: (Int) -> Unit
) {
    val context = LocalContext.current
    val soundPool = rememberSoundPool()
    val clickSound = remember { soundPool.load(context, R.raw.win_sound, 1) }
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BigMathText(
            left = if (q.missingPosition == 1) "_" else q.left.toString(),
            op = q.operator,
            right = if (q.missingPosition == 2) "_" else q.right.toString(),
            result = when (q.operator) {
                '+' -> q.left + q.right
                '-' -> q.left - q.right
                '×' -> q.left * q.right
                '÷' -> if (q.right != 0) q.left / q.right else 0
                else -> q.left + q.right
            }.toString()
        )

        OutlinedTextField(
            value = textAnswer,
            onValueChange = { if (it.length <= 6) onTextChange(it.filter { ch -> ch.isDigit() || ch == '-' }) },
            label = { Text("Enter the missing number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = { textAnswer.toIntOrNull()?.let(onSubmit) },
            enabled = textAnswer.toIntOrNull() != null,
            shape = RoundedCornerShape(16.dp)
        ) {
            soundPool.play(clickSound, 1f, 1f, 1, 0, 1f)
            Text("Submit")
        }
    }
}

@Composable
private fun MissingOperatorCard(
    q: Question.MissingOperator, onSubmit: (Char) -> Unit
) {
    val context = LocalContext.current
    val soundPool = rememberSoundPool()
    val clickSound = remember { soundPool.load(context, R.raw.click_sound, 1) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BigMathText(
            left = q.a.toString(), op = "?", right = q.b.toString(), result = q.result.toString()
        )
        FlowRowButtons(
            options = q.options.map { it.toString() },
            onClick = { onSubmit(it.first()) }
        )
    }
}

@Composable
private fun TrueFalseCard(q: Question.TrueFalse, onSubmit: (Boolean) -> Unit) {
    val context = LocalContext.current
    val soundPool = rememberSoundPool()
    val clickSound = remember { soundPool.load(context, R.raw.click_sound, 1) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            q.expression,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF222A3A),
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    soundPool.play(clickSound, 1f, 1f, 1, 0, 1f)
                    onSubmit(true)
                }, shape = RoundedCornerShape(18.dp)
            ) { Text("TRUE", fontSize = 18.sp) }
            Button(
                onClick = {
                    soundPool.play(clickSound, 1f, 1f, 1, 0, 1f)
                    onSubmit(false)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE3E3), contentColor = Color(0xFFB00020)
                ), shape = RoundedCornerShape(18.dp)
            ) { Text("FALSE", fontSize = 18.sp) }
        }
    }
}

@Composable
private fun ReverseCard(q: Question.Reverse, onSubmit: (Char) -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BigMathText(
            left = q.a.toString(), op = "_", right = q.b.toString(), result = q.result.toString()
        )
        FlowRowButtons(
            options = q.options.map { it.toString() },
            onClick = { onSubmit(it.first()) })
    }
}

@Composable
private fun MixCard(
    viewModel: AlgebraViewModel = hiltViewModel(),
    q: Question.Mix,
    textAnswer: String,
    onTextChange: (String) -> Unit,
    onSubmitMissing: (Int, Int) -> Unit,
    onSubmitOp: (Char, Char) -> Unit,
    onSubmitTF: (Boolean, Boolean) -> Unit
) {
    Column(
        Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mix Mode", color = Color(0xFF7B61FF), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        when (val inner = q.inner) {
            is Question.MissingNumber -> MissingNumberCard(
                inner, textAnswer, onTextChange
            ) { onSubmitMissing(it, inner.answer) }

            is Question.MissingOperator -> MissingOperatorCard(inner) {
                onSubmitOp(
                    it, inner.answer
                )
            }

            is Question.TrueFalse -> TrueFalseCard(inner) { onSubmitTF(it, inner.isCorrect) }
            is Question.Reverse -> ReverseCard(inner) { onSubmitOp(it, inner.answer) }
            else -> {
                // Fallback UI if somehow inner question is unexpected
                LaunchedEffect(Unit) {
                    delay(500)
                    viewModel.startNext()
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Question not available",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.startNext()
                        }, // Manual skip, optional
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A8BFF))
                    ) {
                        Text("Next Question", color = Color.White)
                    }

                }
            }
        }
    }
}

@Composable
private fun BigMathText(left: String, op: Char, right: String, result: String) {
    BigMathText(left, op.toString(), right, result)
}

@Composable
private fun BigMathText(left: String, op: String, right: String, result: String) {
    Text(
        text = "$left  $op  $right  =  $result",
        fontSize = 34.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF222A3A),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

/* ================  REUSABLES  ================= */

@Composable
private fun FlowRowButtons(options: List<String>, onClick: (String) -> Unit) {
    // Simple wrap row without extra deps
    var rowWidth by remember { mutableStateOf(0) }
    val maxPerRow = 4.coerceAtMost(options.size)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.take(maxPerRow).forEach { text ->
            Button(
                onClick = { onClick(text) }, shape = RoundedCornerShape(16.dp)
            ) {
                Text(text, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
private fun FeedbackPill(feedback: String?) {
    if (feedback == null) return
    val alpha by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(200), label = "fb_in"
    )
    LaunchedEffect(feedback) {
        // auto fade out
        // delay handled implicitly by the caller changing message; or add a delay if you wish
    }
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF222A3A).copy(alpha = 0.85f * alpha))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                feedback, color = Color.White, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BottomBar(
    onHint: () -> Unit, onRestart: () -> Unit, onPause: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onHint) { Text("💡 Hint") }
        TextButton(onClick = onRestart) { Text("🔁 Restart") }
        TextButton(onClick = onPause) { Text("⏸ Pause") }
    }
}

@Composable
private fun LoadingCard() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/* ================  GAME OVER DIALOG  ================= */



@Composable
fun MissingNumberUI(
    q: Question.MissingNumber,
    textAnswer: String,
    onTextChange: (String) -> Unit,
    onSubmit: (Int) -> Unit
) {
    val leftText = if (q.missingPosition == 1) "_" else q.left.toString()
    val rightText = if (q.missingPosition == 2) "_" else q.right.toString()

    Text(
        "$leftText ${q.operator} $rightText = ${
            when (q.operator) {
                '+' -> q.left + q.right
                '-' -> q.left - q.right
                '×' -> q.left * q.right
                '÷' -> if (q.right != 0) q.left / q.right else 0
                else -> q.left + q.right
            }
        }"
    )

    OutlinedTextField(
        value = textAnswer, onValueChange = onTextChange, label = { Text("Answer") })
    Spacer(Modifier.height(8.dp))
    Button(onClick = {
        val ans = textAnswer.toIntOrNull()
        if (ans != null) onSubmit(ans)
    }) { Text("Submit") }
}

@Composable
fun MissingOperatorUI(q: Question.MissingOperator, onSubmit: (Char) -> Unit) {
    Text("${q.a} ? ${q.b} = ${q.result}")
    Row(Modifier.padding(top = 8.dp)) {
        q.options.forEach { op ->
            Button(onClick = { onSubmit(op) }, modifier = Modifier.padding(4.dp)) {
                Text(op.toString())
            }
        }
    }
}

@Composable
fun TrueFalseUI(q: Question.TrueFalse, onSubmit: (Boolean) -> Unit) {
    Text(q.expression)
    Row(Modifier.padding(top = 8.dp)) {
        Button(onClick = { onSubmit(true) }) { Text("✔") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = { onSubmit(false) }) { Text("✖") }
    }
}

@Composable
fun ReverseUI(q: Question.Reverse, onSubmit: (Char) -> Unit) {
    Text("${q.a} _ ${q.b} = ${q.result}")
    Row(Modifier.padding(top = 8.dp)) {
        q.options.forEach { op ->
            Button(onClick = { onSubmit(op) }, modifier = Modifier.padding(4.dp)) {
                Text(op.toString())
            }
        }
    }
}