package com.futurion.apps.mindmingle.presentation.home.domain

import com.futurion.apps.mindmingle.presentation.navigation.Screen
import com.futurion.apps.mindmingle.presentation.utils.Resources

enum class BottomBarDestination(
    val icon: Int,
    val title:String,
    val screen: Screen
) {

    Home(
        icon = Resources.Icon.Home,
        title = "Home",
        screen = Screen.Home
    ),
    Games(
        icon = Resources.Icon.ShoppingCart,
        title = "Games",
        screen = Screen.Games
    ),
    Profile(
        icon = Resources.Icon.Person,
        title = "Profile",
        screen = Screen.Profile
    )

}