package com.futurion.apps.mindmingle.domain.model

enum class Phase {
    // Tutorial (Level 1 only)
    WELCOME, MEMORIZE_TUTORIAL, WAIT_CONFIRM, SOLVE_TUTORIAL, RESULT_TUTORIAL,

    // Normal flow (Level ≥ 2)
    MEMORIZE, SOLVE, RESULT
}
