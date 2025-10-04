package com.futurion.apps.mathmingle.presentation.profile.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futurion.apps.mathmingle.presentation.utils.FontSize
import com.futurion.apps.mathmingle.presentation.utils.RobotoCondensedFont
import kotlinx.coroutines.delay

@Composable
fun AnimatedEditableUsername(
    username: String,
    onEditClicked: () -> Unit,
) {
    val offsetX = remember { Animatable(-50f) }
    val alpha = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            Reverse
        )
    )
    var showHint by remember { mutableStateOf(false) }

    // Animate entrance
    LaunchedEffect(Unit) {
        offsetX.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, animationSpec = tween(600))
        delay(2000)
        showHint = true
        delay(2500)
        showHint = false
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .clickable { onEditClicked() }
                .offset(x = offsetX.value.dp)
                .alpha(alpha.value),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = username,
                fontFamily = RobotoCondensedFont(),
                fontSize = FontSize.LARGE,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
                color = Color.Black
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Username",
                tint = Color.Black,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )
        }
    }
}
