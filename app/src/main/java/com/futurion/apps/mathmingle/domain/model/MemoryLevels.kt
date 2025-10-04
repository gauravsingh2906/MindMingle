package com.futurion.apps.mathmingle.domain.model

enum class Operations { ADD, SUB, MUL, DIV }






private fun symbol(op: Operations): String = when (op) {
    Operations.ADD -> "+"
    Operations.SUB -> "-"
    Operations.MUL -> "×"
    Operations.DIV -> "÷"
}








//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val numCards = minOf(2 + levelNumber / 2, 8)
//    val weightedOps = when {
//        levelNumber < 5 -> listOf(Op.ADD)
//        levelNumber < 10 -> listOf(Op.ADD, Op.SUB)
//        levelNumber < 15 -> listOf(Op.ADD, Op.SUB, Op.MUL, Op.MUL)
//        else -> Op.entries.toList()
//    }
//
//    val cardValuesRangeAddSub = when {
//        levelNumber < 5 -> 1..5
//        else -> 1..9
//    }
//
//    val cardValuesRangeMul = when {
//        levelNumber < 10 -> listOf(2)
//        levelNumber < 15 -> (2..3).toList()
//        else -> (2..4).toList()
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (0..10).random()
//    val start = currentValue
//
//    repeat(numCards) {
//        val op = weightedOps.random()
//        val value = when (op) {
//            Op.ADD, Op.SUB -> cardValuesRangeAddSub.random()
//            Op.MUL -> cardValuesRangeMul.random()
//            Op.DIV -> {
//                val divisors = (2..4).filter { currentValue % it == 0 }
//                if (divisors.isEmpty()) 1 else divisors.random()
//            }
//        }
//
//        cards.add(MemoryCard(op, value))
//
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
//        currentValue = currentValue.coerceIn(-clampRange, clampRange)
//    }
//
//    return MemoryLevel(levelNumber, cards, start)
//}


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