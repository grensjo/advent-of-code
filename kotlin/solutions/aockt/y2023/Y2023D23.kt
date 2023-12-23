package aockt.y2023

import aockt.y2023.Y2023D23.Direction.*
import io.github.jadarma.aockt.core.Solution
import kotlin.math.max

object Y2023D23 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }

    data class Point(val r: Int, val c: Int) {
        private fun isValid(numRows: Int, numCols: Int) =
            r >= 0 && c >= 0 && r < numRows && c < numCols

        fun getNeighbours(numRows: Int, numCols: Int, constraint: Direction? = null) : List<Point> =
            Direction.entries
                .asSequence()
                .filter { if (constraint != null) it == constraint else true}
                .map {
                    when (it) {
                        NORTH -> Point(r - 1, c)
                        SOUTH -> Point(r + 1, c)
                        WEST -> Point(r, c - 1)
                        EAST -> Point(r, c + 1)
                    }
                }
                .filter { it.isValid(numRows, numCols) }
                .toList()
    }

    private operator fun List<List<Char>>.get(point: Point) = this[point.r][point.c]

    private fun Char.toDirection() = when (this) {
        '^' -> NORTH
        '>' -> EAST
        '<' -> WEST
        'v' -> SOUTH
        else -> null
    }

    data class Maze(val grid: List<List<Char>>) {
        private val numRows = grid.size
        private val numCols = grid[0].size

        // Brute-forces the longest path by recursing for every direction choice.
        fun getLongestPath(): Int {
            val path: MutableList<Point> = mutableListOf()

            fun recurse(start: Point) : Int {
                var current: Point? = start
                var nToRemove = 0
                var best = 0

                while (current != null) {
                    assert (current !in path) { "There is a cycle in the path $path." }
                    path.add(current)
                    nToRemove++

                    if (current == Point(numRows - 1, numCols - 2)) {
                        // We have reached the goal, see if this is longer than the longest known path.
                        best = max(best, path.size)
                        break
                    }

                    val neighbours = current.getNeighbours(numRows, numCols, grid[current].toDirection())

                    val toRecurse: MutableSet<Point> = mutableSetOf()
                    for (next in neighbours) {
                        if (path.size > 1 && next == path[path.size - 2]) continue
                        if (grid[next] == '#') continue
                        toRecurse += next
                    }

                    // In order to avoid stack overflow, only recurse if there are 2 or more directions to go in.
                    if (toRecurse.size > 1) {
                        for (next in toRecurse) {
                            best = max(best, recurse(next))
                        }
                        current = null
                    } else if (toRecurse.size == 1) {
                        current = toRecurse.first()
                    } else {
                        current = null
                    }

                }

                (0 until nToRemove).forEach { _ -> path.removeLast() }
                return best
            }

            // Subtract one to not count the initial position in the path length.
            return recurse(Point(0, 1)) - 1
        }
    }

    override fun partOne(input: String) : Int {
        return input.toMaze().getLongestPath()
    }

    private fun String.toMaze() = Maze(lineSequence().map { it.toList() }.toList())

//    override fun partTwo(input: String) = input.length
}


