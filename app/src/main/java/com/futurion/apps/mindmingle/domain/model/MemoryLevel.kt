package com.futurion.apps.mindmingle.domain.model

import android.util.Log

enum class Op { ADD, SUB, MUL, DIV }

data class MemoryCard(
    val op: Op,
    val value: Int
)

data class MemoryLevel(
    val number: Int,
    val cards: List<MemoryCard>,
    val start: Int
) {
    val correctAnswer: Int
        get() {
            var acc = start
            cards.forEachIndexed { idx, card ->
                val prev = acc
                acc = when (card.op) {
                    Op.ADD -> acc + card.value
                    Op.SUB -> acc - card.value
                    Op.MUL -> acc * card.value
                    Op.DIV -> if (card.value != 0) acc / card.value else acc
                }

                // Optional: intermediate clamp to avoid huge numbers during calculation
                acc = acc.coerceIn(-200, 200)

                Log.d("MathMemoryDebug", "Step ${idx + 1}: ${card.op} ${card.value} ($prev ${symbol(card.op)} ${card.value}) = $acc")
            }
            return acc.coerceIn(-200, 200) // Final clamp
        }


}

private fun symbol(op: Op): String = when (op) {
    Op.ADD -> "+"
    Op.SUB -> "-"
    Op.MUL -> "×"
    Op.DIV -> "÷"
}

data class AnswerOption(
    val value: Int,
    val isCorrect: Boolean
)

fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
    val numCards = minOf(2 + levelNumber / 2, 8)
    val weightedOps = when {
        levelNumber < 5 -> listOf(Op.ADD)
        levelNumber < 10 -> listOf(Op.ADD, Op.SUB)
        levelNumber < 15 -> listOf(Op.ADD, Op.SUB, Op.MUL, Op.MUL)
        else -> Op.entries.toList()
    }

    val cardValuesRangeAddSub = when {
        levelNumber < 5 -> 1..5
        else -> 1..9
    }

    val cardValuesRangeMul = when {
        levelNumber < 10 -> listOf(2)
        levelNumber < 15 -> (2..3).toList()
        else -> (2..4).toList()
    }

    val cards = mutableListOf<MemoryCard>()
    var currentValue = (0..10).random()
    val start = currentValue

    repeat(numCards) {
        val op = weightedOps.random()
        val value = when (op) {
            Op.ADD, Op.SUB -> cardValuesRangeAddSub.random()
            Op.MUL -> cardValuesRangeMul.random()
            Op.DIV -> {
                val divisors = (2..4).filter { currentValue % it == 0 }
                if (divisors.isEmpty()) 1 else divisors.random()
            }
        }

        cards.add(MemoryCard(op, value))

        currentValue = when (op) {
            Op.ADD -> currentValue + value
            Op.SUB -> currentValue - value
            Op.MUL -> currentValue * value
            Op.DIV -> if (value != 0) currentValue / value else currentValue
        }

        val clampRange = when {
            levelNumber < 5 -> 20
            levelNumber < 10 -> 50
            else -> 100
        }

        currentValue = currentValue.coerceIn(-clampRange, clampRange)
    }

    return MemoryLevel(levelNumber, cards, start)
}


//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val numCards = minOf(2 + levelNumber / 3, 6)
//    val possibleOps = when {
//        levelNumber < 5  -> listOf(Op.ADD)
//        levelNumber < 10 -> listOf(Op.ADD, Op.SUB)
//        levelNumber < 15 -> listOf(Op.ADD, Op.SUB, Op.MUL)
//        else             -> Op.entries.toList()
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (0..10).random()  // start value
//    val start = currentValue
//
//    repeat(numCards) {
//        val op = possibleOps.random()
//        val value = when (op) {
//            Op.DIV -> {
//                // For division, pick a divisor that divides currentValue exactly
//                val divisors = (2..4).filter { currentValue % it == 0 }
//                if (divisors.isEmpty()) {
//                    // No exact divisor, fallback to a default divisor (avoid division)
//                    1
//                } else {
//                    divisors.random()
//                }
//            }
//            Op.MUL -> (2..4).random()
//            else -> (1..9).random()
//        }
//
//        cards.add(MemoryCard(op, value))
//
//        // Update currentValue to represent intermediate answer
//        currentValue = when (op) {
//            Op.ADD -> currentValue + value
//            Op.SUB -> currentValue - value
//            Op.MUL -> currentValue * value
//            Op.DIV -> if (value != 0) currentValue / value else currentValue
//        }
//
//        val clampRange = when {
//            levelNumber < 5 -> 20
//            levelNumber < 10 -> 50
//            else -> 100
//        }
//
//        // Optional: clamp currentValue to keep numbers manageable (help with final answer size)
//        currentValue = currentValue.coerceIn(-clampRange, clampRange)
//    }
//
//    return MemoryLevel(levelNumber, cards, start)
//}