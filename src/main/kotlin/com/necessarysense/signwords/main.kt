import com.necessarysense.signwords.models.*
import com.necessarysense.signwords.reader.Reader
import com.necessarysense.signwords.searcher.AcrosticStepGenerator
import com.necessarysense.signwords.searcher.AllPossibilitiesSearcher

fun printPuzzle(fileName: String, puzzle: MaybePuzzle) = when(puzzle) {
    ErrorPuzzleNoTarget -> { println("There were no words in $fileName")}
    ErrorPuzzleNotEnoughComposingWords -> { println("There were not enough other words to spell the target word in $fileName") }
    ErrorPuzzleComposingWordsMissingLetters -> { println("There were letters of the target word which were missing from the composing words in $fileName")}
    ErrorPuzzleComposingWordsNotEnoughContributions -> { println("The proposed other words did not have enough letters to create the full target word in $fileName") }
    is Puzzle -> {
        puzzle.report()
        val searcher = AllPossibilitiesSearcher(AcrosticStepGenerator(puzzle))
        val allTrails = searcher.allTrails()
        println("all trails count = ${allTrails.size}")
        val shuffledTrails = allTrails.toMutableList()
        shuffledTrails.shuffle()
        shuffledTrails.take(20).forEach { trail ->
            printTrail(trail)
        }
    }
}

fun colorize(s: String, highlight: Int): String = 
    arrayOf(s.subSequence(0, highlight), "\u001b[36m", s.subSequence(highlight, highlight+1), "\u001b[0m", s.subSequence(highlight+1, s.length)).joinToString("")

fun printTrail(trail: List<AcrosticStep>) {
    val maxLeft = trail.maxOf { step -> step.contributingLetter }
    trail.forEach { step ->
        val spaceCount = maxLeft - step.contributingLetter + 1
        println(colorize(step.composingWord.toUpperCase().padStart(step.composingWord.length + spaceCount, ' '), step.contributingLetter + spaceCount))
    }
    println("\n*************\n")
}

fun main(args: Array<String>) {
    val puzzle4 = Reader.readResource("test4.txt")
    printPuzzle("test4.txt", puzzle4)

    val puzzle6 = Reader.readResource("test6.txt")
    printPuzzle("test6.txt", puzzle6)
}