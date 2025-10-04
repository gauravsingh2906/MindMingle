package com.futurion.apps.mathmingle.domain

import android.util.Log
import com.futurion.apps.mathmingle.domain.model.MemoryCard
import com.futurion.apps.mathmingle.domain.model.MemoryLevel
import com.futurion.apps.mathmingle.domain.model.Operations
import kotlin.math.abs
import kotlin.math.min


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

fun generateMemoryLevel1(level: Int): MemoryLevel {
    val (operators, numberRange, steps) = when (level) {

        else -> {}
    }

    val op = operators.random()
    val a = numberRange.random()
    val b = numberRange.random().coerceAtLeast(1)

    val question = "$a $op $b"
    val correctAnswer = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> if (a % b == 0) a / b else a
        else -> a + b
    }

    val options = mutableSetOf(correctAnswer)
    while (options.size < 4) {
        val wrong = correctAnswer + (-5..5).random()
        if (wrong != correctAnswer) options.add(wrong)
    }

    // NEW: Dynamic Timer
    val timeLimit = calculateDynamicTime(level, a, b, op)

    return MathMemoryLevel(
        level = level,
        question = question,
        correctAnswer = correctAnswer,
        options = options.shuffled(),
        timeLimit = timeLimit,
        stepsRequired = steps.random()
    )
}


fun generateMemoryLevel(levelNumber: Int): MemoryLevel {
    val maxAbsValue = 200
    val numCards = minOf(2 + levelNumber / 2, 8)

    val weightedOps = when {
        levelNumber < 5 -> listOf(Operations.ADD)
        levelNumber < 10 -> listOf(Operations.ADD, Operations.SUB)
        levelNumber < 15 -> listOf(Operations.ADD, Operations.SUB, Operations.MUL)
        else -> Operations.entries.toList()
    }

    val cardValuesRangeAddSub = when {
        levelNumber < 3 -> 1..10     // Level 1-2
        levelNumber < 6 -> 1..15     // Level 3-5
        levelNumber < 10 -> 5..25    // Level 6-9
        levelNumber < 15 -> 10..50   // Level 10-14
        else -> 20..100
    }


    val cardValuesRangeMul = when {
        levelNumber < 5 -> listOf(2)
        levelNumber < 10 -> (2..3).toList()
        levelNumber < 15 -> (2..4).toList()
        else -> (2..6).toList()
    }

    val cards = mutableListOf<MemoryCard>()
    var currentValue = (0..10).random()
    val start = currentValue

    Log.d("MathMemoryDebug", "=== Generating Level $levelNumber ===")
    Log.d("MathMemoryDebug", "Start value: $start")

    repeat(numCards) { idx ->
        val op = weightedOps.random()
        val value = when (op) {
            Operations.ADD -> {
                val maxAdd = maxAbsValue - currentValue
                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxAdd)).random()
            }
            Operations.SUB -> {
                val maxSub = currentValue + maxAbsValue
                (cardValuesRangeAddSub.first..min(cardValuesRangeAddSub.last, maxSub)).random()
            }
            Operations.MUL -> {
                val possibleMultipliers = cardValuesRangeMul.filter { abs(currentValue * it) <= maxAbsValue }
                if (possibleMultipliers.isEmpty()) 1 else possibleMultipliers.random()
            }
            Operations.DIV -> {
                val divisors = (2..4).filter { currentValue % it == 0 }
                if (divisors.isEmpty()) 1 else divisors.random()
            }
        }

        cards.add(MemoryCard(op, value))
        val prev = currentValue
        currentValue = when (op) {
            Operations.ADD -> currentValue + value
            Operations.SUB -> currentValue - value
            Operations.MUL -> currentValue * value
            Operations.DIV -> if (value != 0) currentValue / value else currentValue
        }

        Log.d(
            "MathMemoryDebug",
            "Card ${idx + 1}: ${symbol(op)} $value, $prev ${symbol(op)} $value = $currentValue"
        )
    }

    Log.d("MathMemoryDebug", "Correct answer for Level $levelNumber: $currentValue")
    Log.d("MathMemoryDebug", "==============================")

    return MemoryLevel(levelNumber, cards, start,200)
}




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




