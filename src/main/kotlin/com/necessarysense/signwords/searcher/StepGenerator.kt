package com.necessarysense.signwords.searcher

import com.necessarysense.signwords.models.Trail
import kotlinx.collections.immutable.PersistentList

interface StepGenerator<S> {
    fun initialMoves(): PersistentList<S>
    fun nextSteps(currentState: S): PersistentList<S>
    fun isComplete(trail: PersistentList<S>): Boolean
    fun stepAlreadyUsed(trail: Trail<S>): Boolean
}
