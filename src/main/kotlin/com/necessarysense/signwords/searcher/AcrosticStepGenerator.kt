package com.necessarysense.signwords.searcher

import com.necessarysense.signwords.models.AcrosticStep
import com.necessarysense.signwords.models.Puzzle
import com.necessarysense.signwords.models.Trail
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import mu.KotlinLogging

class AcrosticStepGenerator(private val puzzle: Puzzle) : StepGenerator<AcrosticStep> {
    private val letterPossibilities: Map<Char, List<String>>

    private val logger = KotlinLogging.logger {}

    init {
        val possMap = mutableMapOf<Char, MutableList<String>>()
        puzzle.composingWords.forEach { word ->
            word.forEach { letter ->
                val possList: MutableList<String> = if (possMap.containsKey(letter)) {
                    possMap[letter]!!
                } else {
                    mutableListOf()
                }
                if (!possList.any { possibility -> possibility == word }) {
                    possList.add(word)
                }
                possMap[letter] = possList
            }
        }
        letterPossibilities = possMap
    }

    override fun initialMoves(): PersistentList<AcrosticStep> {
        val firstLetter = puzzle.targetWord.first()
        return (letterPossibilities[firstLetter] ?: persistentListOf()).map { possibility ->
            AcrosticStep(possibility, 0, possibility.indexOfFirst { let -> let == firstLetter })
        }.toPersistentList()
    }

    override fun stepAlreadyUsed(trail: Trail<AcrosticStep>): Boolean {
        val proposedWord = trail.currentStep.composingWord
        val usedBefore = trail.previousSteps.any { step -> step.composingWord == proposedWord }
        logger.debug("checking if step already used; usedBefore = $usedBefore; proposed word = ${trail.currentStep.composingWord} previous steps = ${trail.previousSteps}")
        return usedBefore
    }

    override fun nextSteps(currentState: AcrosticStep): PersistentList<AcrosticStep> {
        val nextLetter = currentState.currentLetter + 1
        logger.debug("nextSteps. nextLetter = $nextLetter.  Puzzle size: ${puzzle.targetWord.length}")
        return if (nextLetter == puzzle.targetWord.length) {
            persistentListOf()
        } else {
            logger.debug("currently looking for a ${puzzle.targetWord[nextLetter]}")
            val possibilities = letterPossibilities[puzzle.targetWord[(currentState.currentLetter + 1)]]!!
            logger.debug("all possibilities = $possibilities")
            possibilities.map { possibility ->
                AcrosticStep(possibility, nextLetter, possibility.indexOfFirst { let -> let == puzzle.targetWord[nextLetter] })
            }.toPersistentList()
        }
    }

    override fun isComplete(trail: PersistentList<AcrosticStep>): Boolean {
        // we're complete if we have the number of steps as the target word,
        // and we don't reuse any words
        logger.debug("checking if trail is complete; previous steps = $trail")
        return trail.size == puzzle.targetWord.length &&
                trail.size == trail.map { trailStep -> trailStep.composingWord }.toSet().count() &&
                spellsWord(trail)
    }

    private fun spellsWord(trail: List<AcrosticStep>): Boolean =
        trail.map { step -> step.composingWord[step.contributingLetter] }.joinToString("") ==
                puzzle.targetWord
}
