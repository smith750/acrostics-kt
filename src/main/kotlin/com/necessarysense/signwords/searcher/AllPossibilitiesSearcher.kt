package com.necessarysense.signwords.searcher

import com.necessarysense.signwords.models.Trail
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import mu.KotlinLogging

class AllPossibilitiesSearcher<S>(private val stepGenerator: StepGenerator<S>) {
    private val logger = KotlinLogging.logger {}

    fun allTrails(): List<PersistentList<S>> {
        return stepGenerator.initialMoves().flatMap { start -> trailsForStep(start) }
    }

    private fun trailsForStep(startStep: S): Sequence<PersistentList<S>> {
        logger.debug("trials for step, start step = $startStep")
        val trails = mutableSetOf<PersistentList<S>>()

        val nodesToSearch = mutableListOf<Trail<S>>()

        val firstNextSteps = stepGenerator.nextSteps(startStep)
        if (firstNextSteps.isNotEmpty()) {
            nodesToSearch.addAll(firstNextSteps.map { step -> Trail(step, persistentListOf(startStep)) })
        }
        logger.debug("initial nodes to search = $nodesToSearch")
        while (nodesToSearch.isNotEmpty()) {
            val currentStep = nodesToSearch.removeLast()
            logger.debug("current step = $currentStep")

            val currentSteps = (currentStep.previousSteps + currentStep.currentStep).toPersistentList()

            val remainingTrails = stepGenerator.nextSteps(currentStep.currentStep).map { proposedStep -> Trail(proposedStep, currentSteps) }.filter { trail -> !stepGenerator.stepAlreadyUsed(trail) }
            logger.debug("remaining trails = $remainingTrails")

            if (remainingTrails.isEmpty()) {
                logger.debug("we're empty so let's see if it's complete")
                if (stepGenerator.isComplete(currentSteps)) {
                    trails.add(currentSteps)
                }
            } else {
                logger.debug("more trails to search: $remainingTrails")
                nodesToSearch.addAll(remainingTrails)
            }
            logger.debug("looping back, nodes to search = $nodesToSearch")
        }
        return trails.asSequence()
    }
}