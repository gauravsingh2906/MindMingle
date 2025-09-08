package com.futurion.apps.mindmingle.presentation.sudoku

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mindmingle.R
import com.futurion.apps.mindmingle.domain.model.Difficulty
import com.futurion.apps.mindmingle.domain.state.SudokuState
import com.futurion.apps.mindmingle.presentation.utils.BebasNeueFont
import com.futurion.apps.mindmingle.presentation.utils.FontSize
import com.futurion.apps.mindmingle.presentation.utils.IconPrimary
import com.futurion.apps.mindmingle.presentation.utils.Resources
import com.futurion.apps.mindmingle.presentation.utils.Surface
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary1
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    state: SudokuState,
    onAction: (SudokuAction) -> Unit,
    difficulty: Difficulty,
    onBack: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRestartDialog = remember { mutableStateOf(false) }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SUDOKU",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        showExitDialog = true
                    }) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back arrow icon",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { it ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Difficulty: $difficulty",
                    fontWeight = FontWeight.Normal,
                    fontSize = FontSize.REGULAR,
                    color = TextPrimary1,
                )

                Text(
                    text = "Time: " + formatTime(state.elapsedTime),
                    fontWeight = FontWeight.Normal,
                    fontSize = FontSize.REGULAR,
                    color = TextPrimary,
                )

                Text(
                    text = "Mistakes: ${state.mistakes}/3",
                    fontWeight = FontWeight.Normal,
                    fontSize = FontSize.REGULAR,
                    color = if (state.mistakes >= 3) Color.Red else Color.Unspecified
                )

            }


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
                            onBack()
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

            if (showRestartDialog.value) {
                AlertDialog(
                    onDismissRequest = { showRestartDialog.value = false },
                    title = { Text("Restart Game") },
                    text = { Text("Are you sure you want to restart the puzzle?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showRestartDialog.value = false
                            onAction(SudokuAction.RestartGame)
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRestartDialog.value = false }) {
                            Text("No")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SudokuBoard(state = state, onAction = onAction)

            Spacer(modifier = Modifier.height(16.dp))

            NumberPad(
                onNumberClick = { number ->
                    onAction(SudokuAction.EnterNumber(number))
                }
            )

            Column(
                modifier = Modifier.align(Alignment.End).padding(end = 24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                HintButton(
                    hintsLeft = 3 - state.hintsUsed,
                    onHint = { onAction(SudokuAction.UseHint) }, // use regular hint
                    onWatchAd = onHint // callback to show rewarded ad dialog/screen
                )
                Text(text = "Hint", textAlign = TextAlign.Center)
            }



//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                OutlinedButton(
//                    onClick = { onAction(SudokuAction.UseHint) },
//                    enabled = state.hintsUsed < 3,
//                    modifier = Modifier
//                        .padding(8.dp)
//                ) {
//                    Icon(Icons.Default.Build, contentDescription = "Hint")
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text("Hint (${3 - state.hintsUsed} left)")
//                }
//
//                TextButton(onClick = onHint) { Text("💡 Hint") }
//            }


        }
    }


}

@Composable
fun HintButton(
    hintsLeft: Int,
    onHint: () -> Unit,
    onWatchAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(48.dp)) { // container size for icon + badge
        IconButton(
            onClick = if (hintsLeft > 0) onHint else onWatchAd,
            modifier = Modifier.fillMaxSize().border(width = 1.dp, color = Color.Yellow, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(Resources.Icon.Hint),
                contentDescription = if (hintsLeft > 0) "Hint" else "Watch ad for hint",
                tint = Color(0xFFFFEB3B), // yellow bulb
                modifier = Modifier.size(36.dp)
            )
        }

        if (hintsLeft > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .background(Color(0xFF2196F3), shape = CircleShape), // blue badge circle
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = hintsLeft.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 1.dp)
                )

            }
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .background(Color(0xFF4CAF50), shape = CircleShape), // green badge circle
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Resources.Icon.VideoPlay),
                    contentDescription = "Watch Ad",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}


@Composable
fun SudokuBoard(
    state: SudokuState,
    onAction: (SudokuAction) -> Unit
) {


    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        for (row in 0..8) {
            Row(Modifier.weight(1f)) {
                for (col in 0..8) {
                    val cell = state.board[row][col]
                    val isSelected = state.selectedCell == (row to col)
                    val isInvalid = (row to col) in state.invalidCells

                    val bgColor = when {
                        cell.isFixed -> Color.LightGray
                        cell.isHint -> Color(0xFFB3FFB3)
                        isSelected -> Color(0xFFCCE5FF)
                        isInvalid -> Color.Red.copy(alpha = 0.3f)
                        else -> Color.White
                    }

                    val animatedColor by animateColorAsState(
                        targetValue = when {
                            cell.isHint -> Color(0xFFB3FFB3) // light green
                            cell.isSelected -> Color(0xFFCCE5FF)
                            cell.isError -> Color(0xFFFFCCCC)
                            isSelected -> Color(0xFFCCE5FF)
                            isInvalid -> Color.Red.copy(alpha = 0.3f)
                            else -> Color.White
                        }, // animation duration
                        label = ""
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(animatedColor)
                            .clickable {
                                onAction(SudokuAction.SelectCell(row, col))
                            }
                            .drawBehind {
                                val borderSize = 2.dp.toPx()
                                val thinBorderSize = 0.5.dp.toPx()

                                // Top border
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = if (row % 3 == 0) borderSize else thinBorderSize
                                )
                                // Bottom border
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = if ((row + 1) % 3 == 0) borderSize else thinBorderSize
                                )
                                // Start border
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = if (col % 3 == 0) borderSize else thinBorderSize
                                )
                                // End border
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = if ((col + 1) % 3 == 0) borderSize else thinBorderSize
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell.value != 0) {
                            Text(
                                text = cell.value.toString(),
                                fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal,
                                color = if (cell.isFixed) Color.Black else Color.DarkGray,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        (1..9).chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { number ->
                    OutlinedButton(
                        onClick = { onNumberClick(number) },
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}


//@Composable
//fun NumberPad(
//    onNumberClick: (Int) -> Unit
//) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        (1..9).chunked(3).forEach { row ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                row.forEach { number ->
//                    OutlinedButton(
//                        onClick = { onNumberClick(number) },
//                        modifier = Modifier
//                            .size(50.dp),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text(
//                            text = number.toString(),
//                            fontSize = 20.sp,
//                            textAlign = TextAlign.Center,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//    }
//}
fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}













