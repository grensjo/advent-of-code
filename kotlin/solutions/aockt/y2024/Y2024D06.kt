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
        fun nextState(grid: Grid): GuardState {
            var nextDir = dir
            var nextPos = pos + dir
            while (grid[nextPos] == '#') {
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

    override fun partOne(input: String): Int {
        val (grid, initialGuardState) = parseInput(input)

        var guardState = initialGuardState
        val visitedPoints: MutableSet<Point> = mutableSetOf()
        val visitedStates: MutableSet<GuardState> = mutableSetOf()

        while (grid[guardState.pos] != null) {
            if (guardState in visitedStates) {
                throw RuntimeException("Encountered a loop after visiting ${visitedStates.size} states and ${visitedPoints} points.")
            }
            visitedStates.add(guardState)
            visitedPoints.add(guardState.pos)
            guardState = guardState.nextState(grid)
        }

        return visitedPoints.size.also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}
