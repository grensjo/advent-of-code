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

        fun push(robot: Point, dir: Direction): Point {
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
            return p2 + dir
        }

        fun getGpsSum() = getAllCoords().filter { this[it] == 'O' }.sumOf { it.toGps() }
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

    override fun partOne(input: String): Int {
        val (grid, moves) = parseInput(input)

        var robot = grid.findRobot()
        for (dir in moves) {
            assert(robot == grid.findRobot())
            robot = grid.push(robot, dir)
        }

        return grid.getGpsSum().also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}
