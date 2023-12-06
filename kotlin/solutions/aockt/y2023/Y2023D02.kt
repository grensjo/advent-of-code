package aockt.y2023

import aockt.y2023.Color.*
import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException
import kotlin.math.max

private enum class Color { RED, GREEN, BLUE }
private data class Grab(val color: Color, val amount: Int)
private data class Game(val id: Int, val grabs: List<Grab>)

object Y2023D02 : Solution {

    private fun parseGameNumber(gameNumber: String) = gameNumber.trim().split(' ')[1].toInt()

    private fun parseGrab(grabString: String) : Grab {
        val parts = grabString.trim().split(' ')
        assert(parts.size == 2)
        val color = when(parts[1]) {
            "red" -> RED
            "green" -> GREEN
            "blue" -> BLUE
            else -> throw IllegalArgumentException()
        }
        return Grab(color, parts[0].toInt())
    }

    private fun parseGameLine(gameLine: String): Game {
        val parts = gameLine.split(':')
        return Game(parseGameNumber(parts[0]), parts[1].split(',', ';').map(::parseGrab))
    }

    private fun parseInput(input: String): List<Game> =
        input.lineSequence().map(::parseGameLine).toList()

    private fun getMinimumCounts(grabs: List<Grab>) : Map<Color, Int> {
        val counts = mutableMapOf(RED to 0, GREEN to 0, BLUE to 0)
        for (grab in grabs) {
            counts[grab.color] = max(counts.getValue(grab.color), grab.amount)
        }
        return counts
    }

    override fun partOne(input: String) : Int {
        var sum = 0
        for (game in parseInput(input)) {
            val minCounts = getMinimumCounts(game.grabs)
            if (minCounts.getValue(RED) <= 12 && minCounts.getValue(GREEN) <= 13 && minCounts.getValue(BLUE) <= 14) {
                sum += game.id
            }
        }
        return sum
    }

    override fun partTwo(input: String) : Int {
        var sum = 0
        for (game in parseInput(input)) {
            val minCounts = getMinimumCounts(game.grabs)
            sum += minCounts.getValue(RED) * minCounts.getValue(GREEN) * minCounts.getValue(BLUE)
        }
        return sum
    }
}
