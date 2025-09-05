package com.futurion.apps.mindmingle.presentation.math_memory

import com.futurion.apps.mindmingle.domain.model.GameTheme

sealed class MathMemoryAction {
    object RevealCards : MathMemoryAction()
    data class InputChanged(val value: String) : MathMemoryAction()
    object SubmitAnswer : MathMemoryAction()
    object NextLevel : MathMemoryAction()
    object ResetGame : MathMemoryAction()

    object HideCards : MathMemoryAction()

    data class SelectTheme(val theme: GameTheme) : MathMemoryAction()
    object UnlockNextTheme : MathMemoryAction() // Example action
}