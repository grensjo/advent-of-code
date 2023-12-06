package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException
import kotlin.math.max

private enum class Color { RED, GREEN, BLUE }
private data class Grab(val color: Color, val amount: Int)
private data class Game(val id: Int, val grabs: List<Grab>)
private data class Counts(val red: Int, val green: Int, val blue: Int)

object Y2023D02 : Solution {

    private fun parseGameNumber(gameNumber: String) = gameNumber.trim().split(' ')[1].toInt()

    private fun parseGrab(grabString: String) : Grab {
        val parts = grabString.trim().split(' ')
        assert(parts.size == 2)
        val color = when(parts[1]) {
            "red" -> Color.RED
            "green" -> Color.GREEN
            "blue" -> Color.BLUE
            else -> throw IllegalArgumentException()
        }
        return Grab(color, parts[0].toInt())
    }

    private fun parseGameLine(gameLine: String): Game {
        val parts = gameLine.split(':')
        return Game(parseGameNumber(parts[0]), parts[1].split(',', ';').map(::parseGrab))
    }

    private fun parseInput(input: String): List<Game> =
        input.split('\n').map(::parseGameLine)

    private fun getMinimumCounts(grabs: List<Grab>) : Counts {
        var red = 0
        var green = 0
        var blue = 0
        for (grab in grabs) {
            if (grab.color == Color.RED) red = max(red, grab.amount)
            if (grab.color == Color.GREEN) green = max(green, grab.amount)
            if (grab.color == Color.BLUE) blue = max(blue, grab.amount)
        }
        return Counts(red, green, blue)
    }

    override fun partOne(input: String) : Int {
        var sum = 0
        for (game in parseInput(input)) {
            val minCounts = getMinimumCounts(game.grabs)
            if (minCounts.red <= 12 && minCounts.green <= 13 && minCounts.blue <= 14) sum += game.id
        }
        return sum
    }

    override fun partTwo(input: String) : Int {
        var sum = 0
        for (game in parseInput(input)) {
            val minCounts = getMinimumCounts(game.grabs)
            sum += minCounts.red  * minCounts.green * minCounts.blue
        }
        return sum
    }
}
