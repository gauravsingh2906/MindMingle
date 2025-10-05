package com.futurion.apps.mathmingle.domain.model

data class AnswerOption(
    val value: Int,
    val isCorrect: Boolean,
    var isHidden: Boolean = false // default = visible
)