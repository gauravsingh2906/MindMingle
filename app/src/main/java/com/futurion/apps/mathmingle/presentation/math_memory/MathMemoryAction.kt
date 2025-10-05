package com.futurion.apps.mathmingle.presentation.math_memory

import com.futurion.apps.mathmingle.domain.model.GameTheme

sealed class MathMemoryAction {
    object RevealCards : MathMemoryAction()
    data class InputChanged(val value: String) : MathMemoryAction()
    object SubmitAnswer : MathMemoryAction()
    object NextLevel : MathMemoryAction()
    object ResetGame : MathMemoryAction()

    object HideCards : MathMemoryAction()

    object SkipLevel : MathMemoryAction()

    data class SelectTheme(val theme: GameTheme) : MathMemoryAction()
    object UnlockNextTheme : MathMemoryAction() // Example action
}