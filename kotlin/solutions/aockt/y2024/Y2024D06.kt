package aockt.y2024

import aockt.y2024.Y2024D06.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D06 : Solution {
    private data class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        operator fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }
    }

    private data class Point(val i: Int, val j: Int) {
        fun next(d: Direction) = Point(i + d.di, j + d.dj)
        operator fun plus(d: Direction) = next(d)
    }

    private enum class Direction(val di: Int, val dj: Int) {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1),
    }

    private val charToDirection: Map<Char, Direction> =
        mapOf('^' to NORTH, '>' to EAST, 'v' to SOUTH, '>' to WEST)

    private fun Direction.nextClockwise() =
            when(this) {
                NORTH -> EAST
                EAST -> SOUTH
                SOUTH -> WEST
                WEST -> NORTH
            }

    private data class GuardState(val pos: Point, val dir: Direction) {
        fun nextState(grid: Grid, extraObstacle: Point? = null): GuardState {
            var nextDir = dir
            var nextPos = pos + dir
            while (grid[nextPos] == '#' || nextPos == extraObstacle) {
                nextDir = nextDir.nextClockwise()

                if (nextDir == dir) {
                    throw RuntimeException("Didn't find any valid next state from ${this}.")
                }

                nextPos = pos + nextDir
            }
            return GuardState(nextPos, nextDir)
        }
    }

    private fun parseInput(input: String): Pair<Grid, GuardState> {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        val grid = Grid(h, w, input.lines().map { line -> line.toList() })

        for (i in 0..<grid.h) {
            for (j in 0..<grid.w) {
                val p = Point(i, j)
                if (grid[p] in charToDirection) {
                    return grid to GuardState(Point(i, j), charToDirection[grid[p]]!!)
                }
            }
        }

        throw IllegalArgumentException("There was no guard start point in the input.")
    }

    /** Returns a pair of Boolean and Int -- whether the guard entered a loop, and how many positions she visited. */
    private fun simulateGuard(initialGuardState: GuardState, grid: Grid, extraObstacle: Point? = null): Pair<Boolean, Int> {
        var guardState = initialGuardState
        val visitedPoints: MutableSet<Point> = mutableSetOf()
        val visitedStates: MutableSet<GuardState> = mutableSetOf()

        // Loop as long as the guard stays within the grid.
        while (grid[guardState.pos] != null) {
            if (guardState in visitedStates) {
                // We found a loop.
                return true to visitedPoints.size
            }
            visitedStates.add(guardState)
            visitedPoints.add(guardState.pos)
            guardState = guardState.nextState(grid, extraObstacle)
        }

        // No loop was found.
        return false to visitedPoints.size
    }

    override fun partOne(input: String): Int {
        val (grid, initialGuardState) = parseInput(input)
        val (enteredLoop, numVisited) = simulateGuard(initialGuardState, grid)
        if (enteredLoop) { throw RuntimeException("The guard entered a loop in part 1.") }
        return numVisited.also { println(it) }
    }

    override fun partTwo(input: String): Int {
        val (grid, initialGuardState) = parseInput(input)
        var obstacleOptionCount = 0

        // Loop over possible extra obstacle positions, and simulate the guard for each such position.
        for (i in 0..<grid.h) {
            for (j in 0..<grid.w) {
                val p = Point(i, j)
                if (grid[p]=='.') {
                    val (enteredLoop, _) = simulateGuard(initialGuardState, grid, extraObstacle = p)
                    if (enteredLoop) { obstacleOptionCount++ }
                }
            }
        }

        return obstacleOptionCount.also { println(it) }
    }
}
