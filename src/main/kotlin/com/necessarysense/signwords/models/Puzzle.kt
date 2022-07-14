package com.necessarysense.signwords.models

sealed interface MaybePuzzle

class Puzzle(val targetWord: String, val composingWords: Set<String>) : MaybePuzzle {
    private val targetWordLetters = targetWord.toSet()

    private fun unusedEntries(): Set<String> {
        return composingWords.filter { word ->
            val wordLetters = word.toSet()
            wordLetters.none { wordLetter -> targetWordLetters.contains(wordLetter) }
        }.toSet()
    }

    fun report() {
        println("We have a puzzle! Target $targetWord # of composing words ${composingWords.size}")
        val unused = unusedEntries()
        if (unused.isNotEmpty()) {
            println("Warning!  The following words share no letters in common with the target word and will not be used")
            unused.forEach { println("\t$it") }
        }
    }

    fun enoughContributingLetters(): Boolean {
        // 1. find the number of letters needed for the target word
        val targetLetterNeeds: Map<Char, List<Char>> = targetWord.groupBy { let -> let }
        val composingSupplies: Map<Char, Int> =
            composingWords.flatMap { word -> word.asSequence() }.fold(mapOf()) { supplies, ch ->
                val currCount = supplies.getOrDefault(ch, 0)
                supplies + (ch to currCount + 1)
            }
        return targetLetterNeeds.keys.all { targetLetter ->
            targetLetterNeeds.getOrDefault(targetLetter, listOf()).size <= composingSupplies.getOrDefault(
                targetLetter,
                0
            )
        }
    }
}

object ErrorPuzzleNoTarget : MaybePuzzle
object ErrorPuzzleNotEnoughComposingWords : MaybePuzzle
object ErrorPuzzleComposingWordsMissingLetters : MaybePuzzle
object ErrorPuzzleComposingWordsNotEnoughContributions : MaybePuzzle
