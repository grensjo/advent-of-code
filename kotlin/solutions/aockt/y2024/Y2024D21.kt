package aockt.y2024

import aockt.y2024.Y2024D21.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D21 : Solution {
    private data class Point(val i: Int, val j: Int) {
        fun next(d: Direction) = Point(i + d.di, j + d.dj)
        operator fun plus(d: Direction) = next(d)
        operator fun plus(p: Point) = Point(i + p.i, j + p.j)
        operator fun minus(p: Point) = Point(i - p.i, j - p.j)
    }

    private enum class Direction(val di: Int, val dj: Int) {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1),
    }

    private val directions: Map<Char, Direction> = mapOf(
        '^' to NORTH,
        '>' to EAST,
        'v' to SOUTH,
        '<' to WEST,
    )

    private class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        val index: Map<Char, Point> = buildMap {
            for (p in getAllCoords()) {
                put(this@Grid[p]!!, p)
            }
        }

        operator fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }

        fun getPath(from: Char, to: Char): String {
            val p1 = getAllCoords().first { this@Grid[it] == from }
            val p2 = getAllCoords().first { this@Grid[it] == to }
            val diff = p2 - p1

            return buildString {
                val horizontal = if (diff.j > 0) {
                    buildString { for (n in 1..diff.j) { append('>') } }
                } else if (diff.j < 0) {
                    buildString { for (n in 1..-diff.j) { append('<') } }
                } else {
                    ""
                }
                val vertical = if (diff.i > 0) {
                    buildString { for (n in 1..diff.i) { append('v') } }
                } else if (diff.i < 0) {
                    buildString { for (n in 1..-diff.i) { append('^') } }
                } else {
                    ""
                }

                if (this@Grid[p1 + Point(diff.i, 0)] == '#') {
                    // Starting vertically would make us point at the invalid cell for a second.
                    append(horizontal)
                    append(vertical)
                } else if (this@Grid[p1 + Point(0, diff.j)] == '#') {
                    // Starting horizontally would make us point at the invalid cell for a second.
                    append(vertical)
                    append(horizontal)
                } else if (diff.i < 0 && diff.j < 0) {
                    append(horizontal)
                    append(vertical)
                } else if (diff.i > 0 && diff.j > 0) {
                    append(vertical)
                    append(horizontal)
                } else if (diff.i > 0 && diff.j < 0) {
                    append(horizontal)
                    append(vertical)
                } else {
                    append(vertical)
                    append(horizontal)
                }

                append('A')
            }

        }

        fun next(current: Char, instruction: Char): Char? {
            val currentPoint = index[current]!!
            val nextPoint = currentPoint + directions[instruction]!!
            val nextChar = this[nextPoint]
            return when (this[nextPoint]) {
                null -> null
                '#' -> null
                else -> nextChar
            }
        }

        fun getAllCoords(): List<Point> =
            buildList {
                for (i in 0..<h) {
                    for (j in 0..<w) {
                        add(Point(i, j))
                    }
                }
            }
    }
    private fun String.toGrid(): Grid {
        val lines = lines()
        val h = lines.size
        val w = lines[0].length
        assert(lines.all { it.length == w })
        return Grid(h, w, lines.map { line -> line.toList() })
    }

    private val dirKeypad = """
        #^A
        <v>
    """.trimIndent().toGrid()

    private val numKeypad = """
        789
        456
        123
        #0A
    """.trimIndent().toGrid()

    private fun generateInstructions(code: String, keypad: Grid): String {
        var prev = 'A'
        var str = ""

        println(code)
        for (ch in code) {
            str += keypad.getPath(prev, ch)
            prev = ch
        }

        println(str.length.toString() + ": " + str)

        return str
    }

    data class DpState(val from: Char, val to: Char, val depth: Int)
    val cache: MutableMap<DpState, Long> = mutableMapOf()

    private fun solve(code: String, numRobots: Int): Long {
        fun dp(from: Char, to: Char, depth: Int): Long {
            val params = DpState(from, to, depth)
            if (params in cache) {
                return cache[params]!!
            }

            val keypad = if (depth == numRobots) { numKeypad } else { dirKeypad }

            var sum = 0L
            val str = keypad.getPath(from, to)

            if (depth == 0) {
                return str.length.toLong()
            }

            var prev = 'A'
            for (ch in str) {
                sum += dp(prev, ch, depth - 1)
                prev = ch
            }
            return sum.also { cache[params] = it }
        }

        var prev = 'A'
        var sum = 0L
        for (ch in code) {
            sum += dp(prev, ch, numRobots)
            prev = ch
        }
        return sum
    }

    override fun partOne(input: String): Long {
        var sum = 0L
        for (code in input.lines()) {
//            val robot1 = generateInstructions(code, numKeypad)
//            val robot2 = generateInstructions(robot1, dirKeypad)
//            val human = generateInstructions(robot2, dirKeypad)
            val dpResult = solve(code, 2)
            sum += dpResult * (code.dropLast(1).toLong())
        }
        return sum.also { println(it) }
    }

    override fun partTwo(input: String): Long {
        var sum = 0L
        for (code in input.lines()) {
            val dpResult = solve(code, 25)
            sum += dpResult * (code.dropLast(1).toLong())
        }
        return sum.also { println(it) }
    }
}
