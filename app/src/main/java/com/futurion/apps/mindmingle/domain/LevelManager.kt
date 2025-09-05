package com.futurion.apps.mindmingle.domain

import android.util.Log
import com.futurion.apps.mindmingle.domain.model.MemoryCard
import com.futurion.apps.mindmingle.domain.model.MemoryLevel
import com.futurion.apps.mindmingle.domain.model.Op

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




