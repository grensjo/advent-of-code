package aockt.y2023

import aockt.y2023.Y2023D16.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2023D16 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }

    data class Beam(val point: Point, val dir: Direction) {
        fun getNext(newDirection: Direction? = null) =
            when (newDirection ?: dir) {
                NORTH -> Beam(Point(point.r - 1, point.c), NORTH)
                SOUTH -> Beam(Point(point.r + 1, point.c), SOUTH)
                WEST -> Beam(Point(point.r, point.c - 1), WEST)
                EAST -> Beam(Point(point.r, point.c + 1), EAST)
            }
    }

    data class Point(val r: Int, val c: Int) {
        fun isValid(nRows: Int, nCols: Int) = r >= 0 && c >= 0 && r < nRows && c < nCols
    }

    operator fun List<List<Char>>.get(p: Point): Char {
        return this[p.r][p.c]
    }

    data class Contraption(val grid: List<List<Char>>) {
        val nRows = grid.size
        val nCols = grid[0].size

        private fun Beam.getNextBeams() : List<Beam> {
            return when (grid[point]) {
                '.' -> listOf(getNext())
                '/' -> listOf(getNext(
                    when (dir) {
                        NORTH -> EAST
                        EAST -> NORTH
                        SOUTH -> WEST
                        WEST -> SOUTH
                    }))
                '\\' -> listOf(getNext(
                    when (dir) {
                        NORTH -> WEST
                        WEST -> NORTH
                        SOUTH -> EAST
                        EAST -> SOUTH
                    }))
                '-' ->
                    when (dir) {
                        NORTH, SOUTH -> listOf(getNext(WEST), getNext(EAST))
                        WEST, EAST -> listOf(getNext())
                    }
                '|' ->
                    when (dir) {
                        NORTH, SOUTH -> listOf(getNext())
                        WEST, EAST -> listOf(getNext(NORTH), getNext(SOUTH))
                    }
                else -> throw IllegalArgumentException("Unknown grid entry.")
            }.filter { it.point.isValid(nRows, nCols) }
        }

        fun computeNumEnergized(start: Beam): Int {
            val visited: MutableSet<Beam> = mutableSetOf()
            val queue: ArrayDeque<Beam> = ArrayDeque()
            queue.add(start)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current in visited) continue
                visited += current
                queue.addAll(current.getNextBeams())
            }

            return visited.map(Beam::point).distinct().count()
        }
    }

    private fun String.toContraption() = Contraption(lineSequence().map { it.toList() }.toList())

    override fun partOne(input: String) = input.toContraption()
        .computeNumEnergized(Beam(Point(0, 0), EAST))
        .also { println(it) }

    override fun partTwo(input: String) : Int {
        val contraption = input.toContraption()
        val startBeams = buildList {
            for (c in 0 until contraption.nCols) {
                add(Beam(Point(0, c), SOUTH))
                add(Beam(Point(contraption.nRows - 1, c), NORTH))
            }
            for (r in 0 until contraption.nRows) {
                add(Beam(Point(r, 0), EAST))
                add(Beam(Point(r, contraption.nCols - 1), WEST))
            }
        }
        return startBeams.maxOf { contraption.computeNumEnergized(it) }.also { println(it) }
    }
}
