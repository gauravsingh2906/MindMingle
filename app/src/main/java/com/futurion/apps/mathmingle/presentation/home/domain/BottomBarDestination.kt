package com.futurion.apps.mathmingle.presentation.home.domain

import com.futurion.apps.mathmingle.presentation.navigation.Screen
import com.futurion.apps.mathmingle.presentation.utils.Resources

enum class BottomBarDestination(
    val icon: Int,
    val title:String,
    val screen: Screen
) {

    Home(
        icon = Resources.Icon.Unlock,
        title = "Home",
        screen = Screen.Home
    ),
    Games(
        icon = Resources.Icon.Warning,
        title = "Math Mingle",
        screen = Screen.Games
    ),
    Profile(
        icon = Resources.Icon.Person,
        title = "Profile",
        screen = Screen.Profile
    )

}