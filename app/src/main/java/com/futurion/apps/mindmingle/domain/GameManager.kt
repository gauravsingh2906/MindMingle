package com.futurion.apps.mindmingle.domain

import com.futurion.apps.mindmingle.domain.model.GameType
import com.google.codelab.gamingzone.presentation.games.algebra.Question
import kotlin.random.Random

class GameManager {

    fun nextQuestion(level: Int): Question {
        val config = LevelConfig(level.coerceAtLeast(1))
        val pick = Random.nextInt(100)
        val type = when {
            level <= 5 -> if (pick < 70) GameType.MISSING_NUMBER else GameType.TRUE_FALSE
            level <= 10 -> when {
                pick < 40 -> GameType.MISSING_NUMBER
                pick < 70 -> GameType.MIX
                else -> GameType.MISSING_OPERATOR
            }

            level <= 15 -> when {
                pick < 30 -> GameType.MISSING_NUMBER
                pick < 55 -> GameType.MISSING_OPERATOR
                pick < 80 -> GameType.REVERSE
                else -> GameType.TRUE_FALSE
            }

            else -> GameType.MIX
        }

        return when (type) {
            GameType.MISSING_NUMBER -> genMissingNumber(config)
            GameType.MISSING_OPERATOR -> genMissingOperator(config)
            GameType.TRUE_FALSE -> genTrueFalse(config)
            GameType.REVERSE -> genReverse(config)
            GameType.MIX -> Question.Mix(
                nextQuestion(
                    (level - 1).coerceAtLeast(1),
                ),
                difficultyLevel = config.level,
                timeLimit = config.timeLimitSeconds()
            )
        }
    }

    private fun pickNumber(range: IntRange) = range.random()

    private fun safeDivide(a: Int, b: Int): Int = if (b == 0) 0 else a / b

    private fun genMissingNumber(config: LevelConfig): Question.MissingNumber {
        val r = config.numberRange()
        val ops = config.allowedOperators()
        val op = ops.random()

        val (a, b, result) = when (op) {
            '+' -> {
                val x = pickNumber(r)
                val y = pickNumber(r)
                Triple(x, y, x + y)
            }
            '-' -> {
                val x = pickNumber(r)
                val y = pickNumber(r)
                val (larger, smaller) = if (x >= y) x to y else y to x
                Triple(larger, smaller, larger - smaller)
            }
            '×' -> {
                val x = pickNumber(r)
                val y = pickNumber(r)
                if (x==0 || y==0) return genMissingNumber(config)
                Triple(x, y, x * y)
            }
            '÷' -> {
                val (num, den) = safeDivisionPair(r)
                Triple(num, den, num / den)
            }
            else -> {
                val x = pickNumber(r)
                val y = pickNumber(r)
                Triple(x, y, x + y)
            }
        }

        val missingPos = Random.nextInt(1, 3) // 1 or 2
        val correctAnswer = if (missingPos == 1) a else b

        // Check for ambiguous cases:
        if (op == '×' && (a == 0 || b == 0)) {
            return genMissingNumber(config)
        }
        if (op == '÷' && (a == 0)) {
            return genMissingNumber(config)
        }

        return Question.MissingNumber(
            left = a,
            right = b,
            operator = op,
            answer = correctAnswer,
            missingPosition = missingPos,
            difficultyLevel = config.level
        )
    }



    private fun genMissingOperator(config: LevelConfig): Question.MissingOperator {
        val r = config.numberRange()
        val ops = listOf('+', '-', '×', '÷')
        val correctOp = ops.random()

        val (a, b) = when (correctOp) {
            '÷' -> safeDivisionPair(r)
            else -> pickNumber(r) to pickNumber(r).let { if (it == 0) 1 else it }
        }

        val result = when (correctOp) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> a / b
            else -> a + b
        }

        // ✅ Avoid ambiguous cases
        if ((result == a && b == 1) || (result == b && a == 1)) {
            return genMissingOperator(config) // regenerate to avoid cases like 10 ? 1 = 10
        }

      //  val options = (ops.shuffled().take(3) + correctOp).distinct().shuffled()

        val options = (ops.shuffled().take(3) + correctOp).distinct().shuffled()

        return Question.MissingOperator(
            a = a,
            b = b,
            result = result,
            options = options,
            answer = correctOp,
            difficultyLevel = config.level
        )
    }

    private fun genTrueFalse(config: LevelConfig): Question.TrueFalse {
        val r = config.numberRange()
        val op = config.allowedOperators().random()

        val (a, b) = when (op) {
            '÷' -> safeDivisionPair(r)
            else -> pickNumber(r) to pickNumber(r).let { if (it == 0) 1 else it }
        }

        val real = when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> a / b
            else -> a + b
        }

        val showCorrect = Random.nextBoolean()
        val shown = if (showCorrect) real
        else real + Random.nextInt(1, 6) * if (Random.nextBoolean()) 1 else -1

        return Question.TrueFalse(
            expression = "$a $op $b = $shown",
            isCorrect = showCorrect,
            difficultyLevel = config.level
        )
    }


    // ✅ FIX: consistent '×' and '÷'
    private fun genReverse(config: LevelConfig): Question.Reverse {
        val r = config.numberRange()
        val ops = listOf('+', '-', '×', '÷')
        val op = ops.random()

        val (a, b) = when (op) {
            '÷' -> safeDivisionPair(r)
            else -> pickNumber(r) to pickNumber(r).let { if (it == 0) 1 else it }
        }

        val res = when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> a / b
            else -> a + b
        }

        val options = (ops.shuffled().take(3) + op).distinct().shuffled()

        return Question.Reverse(
            a = a,
            b = b,
            result = res,
            options = options,
            answer = op,
            difficultyLevel = config.level
        )
    }

    private fun safeDivisionPair(range: IntRange): Pair<Int, Int> {
        val b = pickNumber(range).let { if (it == 0) 1 else it }
        val a = b * pickNumber(range)
        if (a == 0) return safeDivisionPair(range) // avoid 0 ÷ anything
        return a to b
    }


}