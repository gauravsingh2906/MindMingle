package com.futurion.apps.mathmingle.domain.model

data class Cell(
    val row: Int,
    val col: Int,
    val value: Int,
    val isFixed: Boolean,
    val isSelected: Boolean = false,     // To highlight selection
    val isError: Boolean = false,        // To show mistake coloring
    val isHint: Boolean = false          // To animate or color hints
)
