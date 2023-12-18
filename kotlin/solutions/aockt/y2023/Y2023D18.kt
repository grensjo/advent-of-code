package aockt.y2023

import aockt.y2023.Y2023D18.DigStep
import aockt.y2023.Y2023D18.Direction.*
import aockt.y2023.Y2023D18.Lagoon
import aockt.y2023.Y2023D18.Point
import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

@OptIn(ExperimentalStdlibApi::class)
private fun String.toDigStep(isPart2: Boolean = false) =
    trim().split(' ').let {
        if (isPart2) {
            val hexString = it[2].substringAfter("(#").substringBefore(")")
            val steps = hexString.subSequence(0, 5)
            DigStep(
                when (hexString[5]) {
                    '0' -> EAST
                    '1' -> SOUTH
                    '2' -> WEST
                    '3' -> NORTH
                    else -> throw IllegalArgumentException("Unknown direction '${hexString[5]}'.")
                },
                steps.toString().hexToLong()
            )
        } else {
            DigStep(
                when (it[0]) {
                    "U" -> NORTH
                    "R" -> EAST
                    "D" -> SOUTH
                    "L" -> WEST
                    else -> throw IllegalArgumentException("Unknown direction '$it[0]'.")
                },
                it[1].toLong(),
            )
        }
    }

private fun List<DigStep>.toLagoon() : Lagoon {
    return Lagoon(buildList {
        var lastPoint = Point(0, 0)
        add(lastPoint)
        for (digStep in this@toLagoon) {
            val nextPoint = lastPoint.step(digStep)
            add(nextPoint)
            lastPoint = nextPoint
        }
    })
}

object Y2023D18 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }
    data class DigStep(val dir: Direction, val steps: Long)

    data class Point(val r: Long, val c: Long) {
        fun step(instruction : DigStep) =
            when (instruction.dir) {
                NORTH -> Point(r - instruction.steps, c)
                SOUTH -> Point(r + instruction.steps, c)
                WEST -> Point(r, c - instruction.steps)
                EAST -> Point(r, c + instruction.steps)
            }

        infix fun distanceTo(other: Point) : Long {
            assert (r == other.r || c == other.c)
            return (r - other.r).absoluteValue + (c - other.c).absoluteValue
        }
    }

    data class Lagoon(val trench: List<Point>) {
        fun computeLagoonSize() : Long =
            (
                trench.zipWithNext().sumOf { (p1, p2) ->
                    (p1.c*p2.r) - (p1.r*p2.c)
                } + computeTrenchLength()
            ) / 2 + 1


        private fun computeTrenchLength() : Long =
            trench.zipWithNext().sumOf {(p1, p2) -> p1 distanceTo p2}
    }

    private fun solve(input: String, isPart2: Boolean = false) =
        input
            .lineSequence()
            .map {it.toDigStep(isPart2=isPart2) }
            .toList()
            .toLagoon()
            .computeLagoonSize()
            .also { println(it) }

    override fun partOne(input: String) = solve(input)
    override fun partTwo(input: String) = solve(input, isPart2 = true)
}
