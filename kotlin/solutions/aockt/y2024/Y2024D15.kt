package aockt.y2024

import aockt.y2024.Y2024D15.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D15 : Solution {
    private class Grid(val h: Int, val w: Int, val grid: MutableList<MutableList<Char>>) {
        operator fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }
        operator fun set(p: Point, v: Char) {
            grid[p.i][p.j] = v
        }
        fun getAllCoords(): List<Point> =
            buildList {
                for (i in 0..<h) {
                    for (j in 0..<w) {
                        add(Point(i, j))
                    }
                }
            }
        fun print() {
            for (i in 0..<h) {
                for (j in 0..<w) {
                    print(this[Point(i, j)])
                }
                println()
            }
            println()
        }

        fun swap(p1: Point, p2: Point) {
            val tmp = this[p1]!!
            this[p1] = this[p2]!!
            this[p2] = tmp
        }

        fun findRobot(): Point {
            for (i in 0..<h) {
                for (j in 0..<w) {
                    if (this[Point(i, j)] == '@') {
                        return Point(i, j)
                    }
                }
            }
            throw IllegalArgumentException("There was no robot.")
        }

        fun pushSimple(robot: Point, dir: Direction): Point {
            var nextEmpty: Point? = null
            var current: Point = robot
            while (true) {
                current += dir
                if (this[current] == null || this[current] == '#') { return robot }
                if (this[current] == '.') {
                    nextEmpty = current
                    break
                }
            }

            var p1 = nextEmpty!! + dir.inverse()
            var p2 = nextEmpty
            while (p2 != robot) {
                swap(p1, p2)
                p1 += dir.inverse()
                p2 += dir.inverse()
            }
            return robot + dir
        }

        fun getGpsSum() = getAllCoords().filter { this[it] == 'O' || this[it] == '[' }.sumOf { it.toGps() }

        fun widened(): Grid {
            val newGrid: MutableList<MutableList<Char>> = mutableListOf()
            for (i in 0..<h) {
                newGrid.add(
                    grid[i].flatMap {
                        when (it) {
                            '#' -> listOf('#', '#')
                            'O' -> listOf('[', ']')
                            '.' -> listOf('.', '.')
                            '@' -> listOf('@', '.')
                            else -> throw IllegalArgumentException("Invalid character in grid.")
                        }
                    }.toMutableList()
                )
            }
            assert(newGrid[0].size == w*2)
            return Grid(h, w*2, newGrid)
        }

        fun pushPart2(robot: Point, dir: Direction): Point {
            if (dir == EAST || dir == WEST) {
                // Horisontal pushing can be done with the part 1 approach.
                return pushSimple(robot, dir)
            }

            fun isPushPossible(current: Point): Boolean {
                assert(this[current] == '@' || this[current] == '[')
                val leftResult =
                    when (this[current + dir]) {
                        '[' -> isPushPossible(current + dir)
                        ']' -> isPushPossible(current + dir + WEST)
                        '.' -> true
                        '#' -> false
                        else -> throw IllegalArgumentException()
                    }
                val rightResult =
                    if (this[current] == '[') {
                        when (this[current + dir + EAST]) {
                            '[' -> isPushPossible(current + dir + EAST)
                            ']' -> true // condition already checked with leftResult
                            '.' -> true
                            '#' -> false
                            else -> throw IllegalArgumentException()
                        }
                    } else {
                        // Robot is only one wide, don't have to check rightResult.
                        true
                    }
                return leftResult && rightResult
            }

            fun doPush(current: Point) {
                val currentSymbol = this[current]
                assert(currentSymbol == '@' || currentSymbol == '[')
                // Push left side
                when (this[current + dir]) {
                    '[' -> doPush(current + dir)
                    ']' -> doPush(current + dir + WEST)
                }

                // Push right side
                if (currentSymbol == '[') {
                    if (this[current + dir + EAST] == '[') {
                        doPush(current + dir + EAST)
                    }
                    assert(this[current + dir + EAST] == '.')
                    swap(current + EAST, current+dir + EAST)
                }
                assert(this[current + dir] == '.')
                swap(current, current+dir)
            }

            if (isPushPossible(robot)) {
                doPush(robot)
                return robot + dir
            } else {
                return robot
            }
        }
    }

    private data class Point(val i: Int, val j: Int) {
        fun next(d: Direction) = Point(i + d.di, j + d.dj)
        operator fun plus(d: Direction) = next(d)
        fun toGps() = 100*i + j
    }

    private enum class Direction(val di: Int, val dj: Int) {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1),
    }

    private fun Direction.inverse() =
        when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }

    private val charToDir: MutableMap<Char, Direction> = mutableMapOf(
        '^' to NORTH,
        '>' to EAST,
        'v' to SOUTH,
        '<' to WEST,
    )

    private fun parseInput(input: String): Pair<Grid, List<Direction>> {
        val (gridStr, movesStr) = input.split("\n\n")
        val l = gridStr.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, l.map { line -> line.toMutableList() }.toMutableList()) to movesStr.filter { it in charToDir }.map { charToDir[it]!! }
    }

    private fun solve(grid: Grid, moves: List<Direction>, isPartTwo: Boolean = false): Int {
        var robot = grid.findRobot()
        for (dir in moves) {
            assert(robot == grid.findRobot())
            if (isPartTwo) {
                robot = grid.pushPart2(robot, dir)
            } else {
                robot = grid.pushSimple(robot, dir)
            }
        }
        return grid.getGpsSum().also { println(it) }
    }

    override fun partOne(input: String): Int {
        val (grid, moves) = parseInput(input)
        return solve(grid, moves)
    }

    override fun partTwo(input: String): Int {
        val (grid, moves) = parseInput(input)
        return solve(grid.widened(), moves, isPartTwo = true)
    }
}
