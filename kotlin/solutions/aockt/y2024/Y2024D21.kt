package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D21 : Solution {
    private data class Point(val i: Int, val j: Int) {
        operator fun plus(p: Point) = Point(i + p.i, j + p.j)
        operator fun minus(p: Point) = Point(i - p.i, j - p.j)
    }

    private class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        operator fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }

        fun getAllCoords(): List<Point> =
            buildList {
                for (i in 0..<h) {
                    for (j in 0..<w) {
                        add(Point(i, j))
                    }
                }
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
                } else if (diff.j < 0) {
                    // Prioritize going to '<' first, since that requires two left pushes from the parent robot, which
                    // are expensive if they are not done in succession.
                    append(horizontal)
                    append(vertical)
                } else {
                    append(vertical)
                    append(horizontal)
                }

                append('A')
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

    private data class DpState(val from: Char, val to: Char, val depth: Int)
    private val cache: MutableMap<DpState, Long> = mutableMapOf()

    private fun solveTestcase(code: String, numRobots: Int): Long {
        fun dp(from: Char, to: Char, depth: Int): Long {
            val dpState = DpState(from, to, depth)
            if (dpState in cache) {
                return cache[dpState]!!
            }

            val keypad = if (depth == numRobots) {
                // We're at the first robot, computing paths on the numerical keypad.
                numKeypad
            } else {
                // We're at a subsequent robot (or the last human), computing paths on a directional keypad.
                dirKeypad
            }

            var sum = 0L
            val str = keypad.getPath(from, to)

            if (depth == 0) {
                // We're at the human, pressing buttons in constant time.
                return str.length.toLong()
            }

            var prev = 'A'
            for (ch in str) {
                // Compute cost for parent robot to move us to the next character.
                sum += dp(prev, ch, depth - 1)
                prev = ch
            }

            return sum.also { cache[dpState] = it }
        }

        // Go through the given numerical code and compute the recursive cost for each button press.
        var prev = 'A'
        var sum = 0L
        for (ch in code) {
            sum += dp(prev, ch, numRobots)
            prev = ch
        }
        return sum
    }

    private fun solveAndComputeSum(input: String, numRobots: Int): Long {
        var sum = 0L
        for (code in input.lines()) {
            sum += solveTestcase(code, numRobots) * (code.dropLast(1).toLong())
        }
        return sum.also { println(it) }
    }

    override fun partOne(input: String) = solveAndComputeSum(input, 2)
    override fun partTwo(input: String) = solveAndComputeSum(input, 25)
}
