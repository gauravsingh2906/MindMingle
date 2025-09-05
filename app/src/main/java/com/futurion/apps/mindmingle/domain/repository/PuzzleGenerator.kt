package com.futurion.apps.mindmingle.domain.repository

import com.futurion.apps.mindmingle.domain.model.Difficulty

interface PuzzleGenerator {

    fun generate(difficulty: Difficulty): Pair<List<List<Int>>, List<List<Int>>>

}