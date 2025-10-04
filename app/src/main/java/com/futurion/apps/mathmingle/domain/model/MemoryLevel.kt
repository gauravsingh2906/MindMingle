package com.futurion.apps.mathmingle.domain.model

data class MemoryLevel(
    val number: Int,
    val cards: List<MemoryCard>,
    val start: Int,
    val maxAnswer: Int // max answer for this level
) {
    val correctAnswer: Int
        get() {
            var acc = start
            cards.forEach { card ->
                acc = when (card.op) {
                    Operations.ADD -> acc + card.value
                    Operations.SUB -> acc - card.value
                    Operations.MUL -> acc * card.value
                    Operations.DIV -> if (card.value != 0) acc / card.value else acc
                }
                // Clamp intermediate results to 0..maxAnswer
                acc = acc.coerceIn(-maxAnswer, maxAnswer)
            }
            return acc
        }
}
