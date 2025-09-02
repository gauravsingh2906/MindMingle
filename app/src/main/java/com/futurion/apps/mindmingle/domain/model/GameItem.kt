package com.futurion.apps.mindmingle.domain.model

data class GameItem(
    val id: String? =null,
    val name: String? ="PlayZone",
    val description: String? ="Skill Game",
    val coverImageUrl: Int,
    val isComingSoon: Boolean = false
)