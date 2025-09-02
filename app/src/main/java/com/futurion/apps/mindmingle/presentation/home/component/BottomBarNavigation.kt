package com.futurion.apps.mindmingle.presentation.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.futurion.apps.mindmingle.presentation.home.domain.BottomBarDestination
import com.futurion.apps.mindmingle.presentation.utils.AltAccent
import com.futurion.apps.mindmingle.presentation.utils.AppBackground
import com.futurion.apps.mindmingle.presentation.utils.CardSurface
import com.futurion.apps.mindmingle.presentation.utils.IconPrimary
import com.futurion.apps.mindmingle.presentation.utils.MainAccent
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary
import com.futurion.apps.mindmingle.presentation.utils.TextPrimary1
import kotlin.collections.forEach

@Composable
fun BottomBarNavigation(
    modifier: Modifier = Modifier,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppBackground)
            .padding(
                vertical = 24.dp, horizontal = 36.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomBarDestination.entries.forEach { destinaion ->
            val animatedTint by animateColorAsState(
                targetValue = if (selected==destinaion) AltAccent else IconPrimary
            )

           Box(
               contentAlignment = Alignment.TopEnd
           ){
               Icon(
                   modifier = Modifier.size(24.dp).clickable {
                       onSelect(destinaion)
                   },
                   painter = painterResource(destinaion.icon),
                   tint = animatedTint,
                   contentDescription = destinaion.title
               )
//               Box(
//                   modifier = Modifier
//                       .size(8.dp)
//                       .offset(x=4.dp,y= (-5).dp)
//                       .clip(CircleShape).background(IconSecondary)
//               )
//               if(destinaion == BottomBarDestination.Cart) {
//                   AnimatedContent(
//                       targetState = customer
//                   ) { customerState->
//                       if(customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()) {
//
//                       }
//                   }
//               }
           }

        }


    }
}