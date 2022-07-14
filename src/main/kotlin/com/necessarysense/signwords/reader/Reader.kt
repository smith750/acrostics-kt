package com.necessarysense.signwords.reader

import com.necessarysense.signwords.models.*
import java.io.BufferedReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

object Reader {
    fun readResource(resourceName: String): MaybePuzzle {
        val path = Path.of(ClassLoader.getSystemResource(resourceName).toURI())
        Files.newBufferedReader(path, Charset.forName("UTF-8")).use { reader ->
            return readPuzzle(reader)
        }
    }

    private fun readPuzzle(reader: BufferedReader): MaybePuzzle {
        val lines = reader.lines().map { it.toLowerCase().trim() }.toList()
        return if (lines.isEmpty() || lines.firstOrNull() == null || lines.first().isBlank()) {
            ErrorPuzzleNoTarget
        } else {
            val first = lines.first().toLowerCase()
            val remaining = lines.drop(1).filter { it.isNotBlank() }.toSet()
            if (remaining.size < first.length) {
                ErrorPuzzleNotEnoughComposingWords
            } else {
                val puzzle = Puzzle(first, remaining)
                val letterPossibilities = remaining.flatMap { it.asSequence() }.toSet()
                if (!puzzle.targetWord.all { c -> letterPossibilities.contains(c) }) {
                    ErrorPuzzleComposingWordsMissingLetters
                } else {
                    if (!puzzle.enoughContributingLetters()) {
                        ErrorPuzzleComposingWordsNotEnoughContributions
                    } else {
                        puzzle
                    }
                }
            }
        }
    }
}
