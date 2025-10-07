package com.futurion.apps.mathmingle.domain

import android.util.Log
import com.futurion.apps.mathmingle.domain.model.AnswerOption
import com.futurion.apps.mathmingle.domain.model.MemoryCard
import com.futurion.apps.mathmingle.domain.model.MemoryLevel
import com.futurion.apps.mathmingle.domain.model.Operations
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random


// Generates procedural levels
class LevelManager(
    val levelGenerator: (Int) -> MemoryLevel
) {
    private var currentLevelNumber = 1
    private var cachedLevel: MemoryLevel? = null

    fun currentLevel(): MemoryLevel {
        if (cachedLevel == null) {
            cachedLevel = levelGenerator(currentLevelNumber)
        }
        return cachedLevel!!
    }

    fun setLevel(level: Int) {
        currentLevelNumber = if (level < 1) 1 else level
        cachedLevel = null
    }
    fun nextLevel() {
        currentLevelNumber++
        cachedLevel = null
    }
    fun reset() {
        currentLevelNumber = 1
        cachedLevel = null
    }
}

fun generateMemoryLevel(level: Int): MemoryLevel {
    val random = Random
    val start = when {
        level <= 5 -> random.nextInt(10) + 1
        level <= 15 -> random.nextInt(20) + 1
        level <= 25 -> random.nextInt(30) + 1
        level <= 40 -> random.nextInt(40) + 1
        else -> random.nextInt(50) + 1
    }

    var current = start
    val cards = mutableListOf<MemoryCard>()

    val numOps = when {
        level <= 5 -> random.nextInt(2) + 2 // 2–3 ops
        level <= 15 -> random.nextInt(2) + 3 // 3–4
        level <= 25 -> random.nextInt(2) + 4 // 4–5
        level <= 40 -> random.nextInt(2) + 5 // 5–6
        else -> random.nextInt(2) + 6 // 6–7
    }
    Log.d("MathMemoryScreen","NumOps: $numOps")

    repeat(numOps) {
        var op = when {
            level <= 5 -> listOf(Operations.ADD, Operations.SUB).random()
            level <= 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL).random()
            level <= 25 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.DIV).random()
            else -> Operations.values().random()
        }

        var value: Int

        if (op == Operations.DIV) {
            // ✅ Pick only clean divisors up to 10
            val divisors = (2..10).filter { current % it == 0 }
            if (divisors.isNotEmpty()) {
                value = divisors.random()
            } else {
                // fallback: no clean divisor → change op
                op = listOf(Operations.ADD, Operations.SUB).random()
                value = random.nextInt(10) + 1
            }
        } else {
            value = when (op) {
                Operations.ADD, Operations.SUB -> when {
                    level <= 15 -> random.nextInt(15) + 1
                    level <= 25 -> random.nextInt(20) + 1
                    level <= 40 -> random.nextInt(25) + 1
                    else -> random.nextInt(30) + 1
                }
                Operations.MUL -> when {
                    level <= 15 -> random.nextInt(2) + 2 // ×2–3
                    level <= 25 -> random.nextInt(3) + 2 // ×2–4
                    level <= 40 -> random.nextInt(4) + 2 // ×2–5
                    else -> random.nextInt(5) + 2 // ×2–6
                }
                else -> 1
            }
        }

        // Compute test result
        val test = when (op) {
            Operations.ADD -> current + value
            Operations.SUB -> current - value
            Operations.MUL -> current * value
            Operations.DIV -> if (value != 0) current / value else current
        }

        // ✅ Only accept if within -200..200
        if (test in -200..200) {
            current = test
            cards.add(MemoryCard(op, value))
        }
    }

    return MemoryLevel(
        number = level,
        cards = cards,
        start = start,
        maxAnswer = 200
    )
}



//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val maxAbsValue = 200
//
//    // Number of cards increases with level (2 → 8)
//    val numCards = minOf(2 + levelNumber / 2, 8)
//
//    // Weighted available operations by level
//    val weightedOps = when {
//        levelNumber < 5 -> listOf(Operations.ADD)
//        levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB)
//        levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL)
//        else -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.DIV)
//    }
//
//    // Value ranges that grow with difficulty
//    val cardValuesRangeAddSub = when {
//        levelNumber < 3 -> 1..10
//        levelNumber < 6 -> 1..20
//        levelNumber < 10 -> 5..30
//        levelNumber < 15 -> 10..50
//        else -> 20..100
//    }
//
//    val cardValuesRangeMul = when {
//        levelNumber < 5 -> listOf(2)
//        levelNumber < 10 -> (2..3).toList()
//        levelNumber < 15 -> (2..4).toList()
//        else -> (2..6).toList()
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (0..10).random()
//    val start = currentValue
//
//    Log.d("MathMemoryDebug", "=== Generating Level $levelNumber ===")
//    Log.d("MathMemoryDebug", "Start value: $start")
//
//    repeat(numCards) { idx ->
//        val op = weightedOps.random()
//        val value = when (op) {
//            Operations.ADD -> cardValuesRangeAddSub.random()
//            Operations.SUB -> cardValuesRangeAddSub.random()
//            Operations.MUL -> cardValuesRangeMul.random()
//            Operations.DIV -> {
//                val divisors = (2..4).filter { currentValue != 0 && currentValue % it == 0 }
//                if (divisors.isEmpty()) 1 else divisors.random()
//            }
//        }
//
//        val prev = currentValue
//        currentValue = when (op) {
//            Operations.ADD -> currentValue + value
//            Operations.SUB -> currentValue - value
//            Operations.MUL -> currentValue * value
//            Operations.DIV -> if (value != 0) currentValue / value else currentValue
//        }
//
//        // ✅ Clamp only to avoid numbers too big (but allow negatives)
//        currentValue = currentValue.coerceIn(-maxAbsValue, maxAbsValue)
//
//        cards.add(MemoryCard(op, value))
//        Log.d("MathMemoryDebug", "Card ${idx + 1}: $prev ${symbol(op)} $value = $currentValue")
//    }
//
//    Log.d("MathMemoryDebug", "Correct answer for Level $levelNumber: $currentValue")
//    Log.d("MathMemoryDebug", "==============================")
//
//    return MemoryLevel(levelNumber, cards, start, maxAbsValue)
//}


//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//
//    // Maximum absolute value for the answer (positive or negative)
//    // Up to 50 for early levels, up to 500 for high levels.
//    val maxAbsAnswer = when {
//        levelNumber <= 5 -> 50
//        levelNumber <= 10 -> 100
//        levelNumber <= 20 -> 250
//        else -> 500
//    }
//
//    // Operations and their weights
//    val weightedOps = when {
//        levelNumber < 5 -> listOf(Operations.ADD, Operations.ADD) // ADD focus
//        levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB, Operations.ADD) // SUB introduced
//        levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.MUL)
//        levelNumber < 25 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.DIV)
//        else -> Operations.entries.toList()
//    }
//
//    // Number of steps (cards)
//    val numCards = minOf(3 + levelNumber / 4, 8)
//
//    // Range for ADD/SUB values (increases quickly)
//    val cardValuesRangeAddSub = when {
//        levelNumber < 5 -> 1..10
//        levelNumber < 10 -> 5..15
//        levelNumber < 15 -> 5..20
//        levelNumber < 20 -> 10..25
//        levelNumber < 25 -> 10..30
//        levelNumber < 30 -> 15..35
//        levelNumber < 35 -> 20..40
//        levelNumber < 40 -> 25..45
//        else -> 20..100
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    // Start value allows for larger additions early on
//    var currentValue = (5..min(50, maxAbsAnswer)).random()
//    val start = currentValue
//
//    repeat(numCards) {
//        val op = weightedOps.random()
//
//        val value = when (op) {
//            Operations.ADD -> {
//                // Ensure addition doesn't exceed maxAbsAnswer
//                val maxAdd = maxAbsAnswer - currentValue
//                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxAdd.coerceAtLeast(1))).random()
//            }
//            Operations.SUB -> {
//                // Allow subtractions that result in negative numbers after level 5
//                val maxSub = if (levelNumber < 5) currentValue else currentValue + maxAbsAnswer
//                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxSub.coerceAtLeast(1))).random()
//            }
//            Operations.MUL -> {
//                // Multipliers capped based on maxAnswer and currentValue
//                val maxMultiplier = minOf(4, abs(maxAbsAnswer / currentValue).coerceAtLeast(2))
//                (2..maxMultiplier).random()
//            }
//            Operations.DIV -> {
//                // Strict division for clean results
//                val divisors = (2..5).filter { abs(currentValue) % it == 0 }
//                if (divisors.isEmpty()) 1 else divisors.random()
//            }
//        }
//
//        cards.add(MemoryCard(op, value))
//
//        currentValue = when (op) {
//            Operations.ADD -> currentValue + value
//            Operations.SUB -> currentValue - value
//            Operations.MUL -> currentValue * value
//            Operations.DIV -> if (value != 0) currentValue / value else currentValue
//        }.coerceIn(-maxAbsAnswer, maxAbsAnswer)
//    }
//
//    // Final answer is the last currentValue
//    return MemoryLevel(
//        number = levelNumber,
//        cards = cards,
//        start = start,
//        maxAnswer = maxAbsAnswer // Use maxAbsAnswer for both clamping and level display
//    )
//}



//easy one
//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//
//    val maxAnswer = when {
//        levelNumber <= 5 -> 20
//        levelNumber <= 10 -> 50
//        levelNumber <= 15 -> 100
//        else -> 200
//    }
//
//    val weightedOps = when {
//        levelNumber < 6 -> listOf(Operations.ADD)
//        levelNumber < 11 -> listOf(Operations.ADD, Operations.SUB)
//        levelNumber < 16 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.MUL)
//        else -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.DIV)
//    }
//
//    val numCards = minOf(2 + levelNumber / 3, 8)
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (1..10).random()
//    val start = currentValue
//
//    repeat(numCards) {
//        val op = weightedOps.random()
//        val value = when (op) {
//            Operations.ADD -> (1..9).random()
//            Operations.SUB -> (1..9).random().coerceAtMost(currentValue)
//            Operations.MUL -> listOf(2, 3).random()
//            Operations.DIV -> listOf(2, 3, 4).filter { it != 0 && currentValue % it == 0 }
//                .randomOrNull() ?: 1
//        }
//
//        cards.add(MemoryCard(op, value))
//
//        currentValue = when (op) {
//            Operations.ADD -> currentValue + value
//            Operations.SUB -> currentValue - value
//            Operations.MUL -> currentValue * value
//            Operations.DIV -> if (value != 0) currentValue / value else currentValue
//        }.coerceIn(0, maxAnswer)
//    }
//
//    return MemoryLevel(
//        number = levelNumber,
//        cards = cards,
//        start = start,
//        maxAnswer = maxAnswer
//    )
//}


// last one
//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val maxAbsValue = 200
//    val numCards = minOf(2 + levelNumber / 2, 8)
//
//    val weightedOps = when {
//        levelNumber < 5 -> listOf(Operations.ADD)
//        levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB)
//        levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL)
//        else -> Operations.entries.toList()
//    }
//
//    val cardValuesRangeAddSub = when {
//        levelNumber < 3 -> 1..10     // Level 1-2
//        levelNumber < 6 -> 1..15     // Level 3-5
//        levelNumber < 10 -> 5..25    // Level 6-9
//        levelNumber < 15 -> 10..50   // Level 10-14
//        else -> 20..100
//    }
//
//
//    val cardValuesRangeMul = when {
//        levelNumber < 5 -> listOf(2)
//        levelNumber < 10 -> (2..3).toList()
//        levelNumber < 15 -> (2..4).toList()
//        else -> (2..6).toList()
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (0..10).random()
//    val start = currentValue
//
//    Log.d("MathMemoryDebug", "=== Generating Level $levelNumber ===")
//    Log.d("MathMemoryDebug", "Start value: $start")
//
//    repeat(numCards) { idx ->
//        val op = weightedOps.random()
//        val value = when (op) {
//            Operations.ADD -> {
//                val maxAdd = maxAbsValue - currentValue
//                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxAdd)).random()
//            }
//            Operations.SUB -> {
//                val maxSub = currentValue + maxAbsValue
//                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxSub)).random()
//            }
//            Operations.MUL -> {
//                val possibleMultipliers = cardValuesRangeMul.filter { abs(currentValue * it) <= maxAbsValue }
//                if (possibleMultipliers.isEmpty()) 1 else possibleMultipliers.random()
//            }
//            Operations.DIV -> {
//                val divisors = (2..4).filter { currentValue % it == 0 }
//                if (divisors.isEmpty()) 1 else divisors.random()
//            }
//        }
//
//        cards.add(MemoryCard(op, value))
//        val prev = currentValue
//        currentValue = when (op) {
//            Operations.ADD -> currentValue + value
//            Operations.SUB -> currentValue - value
//            Operations.MUL -> currentValue * value
//            Operations.DIV -> if (value != 0) currentValue / value else currentValue
//        }
//
//        Log.d(
//            "MathMemoryDebug",
//            "Card ${idx + 1}: ${symbol(op)} $value, $prev ${symbol(op)} $value = $currentValue"
//        )
//    }
//
//    Log.d("MathMemoryDebug", "Correct answer for Level $levelNumber: $currentValue")
//    Log.d("MathMemoryDebug", "==============================")
//
//    return MemoryLevel(levelNumber, cards, start,200)
//}




private fun symbol(op: Operations): String = when (op) {
    Operations.ADD -> "+"
    Operations.SUB -> "-"
    Operations.MUL -> "×"
    Operations.DIV -> "÷"
}


//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val numCards = minOf(2 + levelNumber / 2, 8)
//
//    // Max answer grows with level
//    val maxAnswer = when {
//        levelNumber <= 5 -> 50
//        levelNumber <= 10 -> 100
//        levelNumber <= 15 -> 150
//        else -> 200
//    }
//
//    val weightedOps = when {
//        levelNumber < 5 -> listOf(Operations.ADD)
//        levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB)
//        levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL, Operations.MUL)
//        else -> Operations.entries.toList()
//    }
//
//    val cardValuesRangeAddSub = if (levelNumber < 5) 1..5 else 1..9
//    val cardValuesRangeMul = when {
//        levelNumber < 10 -> listOf(2)
//        levelNumber < 15 -> (2..3).toList()
//        else -> (2..4).toList()
//    }
//
//    val cards = mutableListOf<MemoryCard>()
//    var currentValue = (0..10).random().coerceAtMost(maxAnswer)
//    val start = currentValue
//
//    repeat(numCards) {
//        val op = weightedOps.random()
//        val value = when (op) {
//            Operations.ADD -> cardValuesRangeAddSub.filter { it + currentValue <= maxAnswer }.randomOrNull() ?: 1
//            Operations.SUB -> cardValuesRangeAddSub.filter { it <= currentValue }.randomOrNull() ?: 1
//            Operations.MUL -> cardValuesRangeMul.filter { it * currentValue <= maxAnswer }.randomOrNull() ?: 1
//            Operations.DIV -> (2..4).filter { it != 0 && currentValue / it <= maxAnswer }.randomOrNull() ?: 1
//        }
//
//        cards.add(MemoryCard(op, value))
//
//        currentValue = when (op) {
//            Operations.ADD -> currentValue + value
//            Operations.SUB -> currentValue - value
//            Operations.MUL -> currentValue * value
//            Operations.DIV -> if (value != 0) currentValue / value else currentValue
//        }
//
//        currentValue = currentValue.coerceIn(-maxAnswer, maxAnswer)
//    }
//
//    return MemoryLevel(levelNumber, cards, start, maxAnswer)
//}

// Example procedural generator function
//fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
//    val numCards = minOf(2 + levelNumber / 3, 6)
//    val possibleOps = when {
//        levelNumber < 5  -> listOf(Op.ADD)
//        levelNumber < 10 -> listOf(Op.ADD, Op.SUB)
//        levelNumber < 15 -> listOf(Op.ADD, Op.SUB, Op.MUL)
//        else             -> Op.entries
//    }
//    val cards = List(numCards) {
//        val op = possibleOps.random()
//        val value = when (op) {
//            Op.MUL, Op.DIV -> (2..4).random()
//            else -> (1..9).random()
//        }
//        MemoryCard(op, value)
//    }
//    val start = (0..10).random()
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
//    val cards = List(numCards) {
//        val op = possibleOps.random()
//        val value = when (op) {
//            Op.MUL, Op.DIV -> (2..4).random()
//            else -> (1..9).random()
//        }
//        MemoryCard(op, value)
//    }
//    val start = (0..10).random()
//    Log.d("MathMemoryDebug", "Generated level $levelNumber: start=$start, cards=${cards.map { "${it.op}:${it.value}" }}")
//    return MemoryLevel(levelNumber, cards, start)
//}




