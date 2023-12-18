package aockt.y2023

import aockt.y2023.Y2023D18.DigStep
import aockt.y2023.Y2023D18.Direction.*
import aockt.y2023.Y2023D18.Lagoon
import aockt.y2023.Y2023D18.Point
import io.github.jadarma.aockt.core.Solution

@OptIn(ExperimentalStdlibApi::class)
private fun String.toDigStep() =
    trim().split(' ').let {
        DigStep(
            when (it[0]) {
                "U" -> NORTH
                "R" -> EAST
                "D" -> SOUTH
                "L" -> WEST
                else -> throw IllegalArgumentException("Unknown direction '$it[0]'.")
            },
            it[1].toInt(),
            it[2].substringAfter("(#").substringBefore(")").hexToInt()
        )
    }

private fun List<DigStep>.toLagoon() : Lagoon {
    return Lagoon(buildSet {
        var lastPoint = Point(0, 0)
        add(lastPoint)
        for (digStep in this@toLagoon) {
            for (i in 0 until digStep.steps) {
                val nextPoint = lastPoint.step(digStep.dir)
                add(nextPoint)
                lastPoint = nextPoint
            }
        }
    })
}

object Y2023D18 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }
    data class DigStep(val dir: Direction, val steps: Int, val color: Int)

    data class Point(val r: Int, val c: Int) {
        fun step(dir: Direction) =
            when (dir) {
                NORTH -> Point(r - 1, c)
                SOUTH -> Point(r + 1, c)
                WEST -> Point(r, c - 1)
                EAST -> Point(r, c + 1)
            }

        fun getNeighbours() : List<Point> = entries.map(::step)
    }

    data class Lagoon(val trench: Set<Point>) {
        private fun findInteriorPoint() : Point {
            trench.groupBy { it.r }.forEach { row ->
                row.value.sortedBy { it.c }.zipWithNext().forEach {(p1, p2) ->
                    if (p2.c - p1.c > 1) return p1.step(EAST)
                }
            }
            throw AssertionError("Could not find any interior point.")
        }

        fun computeLagoonSize() : Int {
            val start = findInteriorPoint()
            val queue : ArrayDeque<Point> = ArrayDeque()
            val visited : MutableSet<Point> = mutableSetOf()
            queue.add(start)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current in visited || current in trench) continue
                visited.add(current)
                queue.addAll(current.getNeighbours().filterNot { it in visited || it in trench })
            }

            return visited.size + trench.size
        }
    }

    override fun partOne(input: String) = input
        .lineSequence()
        .map(String::toDigStep)
        .toList()
        .toLagoon()
        .computeLagoonSize()
        .also { println(it) }

//    override fun partTwo(input: String) = input.length
}
