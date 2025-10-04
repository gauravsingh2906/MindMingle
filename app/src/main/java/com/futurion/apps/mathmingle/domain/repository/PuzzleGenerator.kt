package com.futurion.apps.mathmingle.domain.repository

import com.futurion.apps.mathmingle.domain.model.Difficulty

interface PuzzleGenerator {

    fun generate(difficulty: Difficulty): Pair<List<List<Int>>, List<List<Int>>>

}