package aockt.y2024

import aockt.y2024.Y2024D21.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D21 : Solution {
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

    // Initial state: (A, A, A, 0)
    // Finished state: (A, A, A, 4)
    data class State(val dirRobots: List<Char>, val numRobot: Char, val codePos: Int) {
        fun next(humanInstruction: Char, code: String): State? {
            val newDirRobots = dirRobots.toMutableList()
            var newNumRobot = numRobot
            var newCodePos = codePos

            fun recurse(instruction: Char, robotIdx: Int): Boolean {
                if (robotIdx < dirRobots.size) {
                    // We're at a directional keypad robot
                    if (instruction == 'A') {
                        return recurse(dirRobots[robotIdx], robotIdx + 1)
                    } else {
                        val newRobotCh = dirKeypad.next(dirRobots[robotIdx], instruction) ?: return false
                        newDirRobots[robotIdx] = newRobotCh
                        return true
                    }
                } else {
                    // We're at the numerical keypad robot
                    if (instruction == 'A') {
                        val nextCodeChar = numRobot
                        // Is the character correct?
                        if (code[codePos] == nextCodeChar) {
                            newCodePos++
                            return true
                        } else {
                            return false
                        }
                    } else {
                        val newNumRobotCh = numKeypad.next(numRobot, instruction) ?: return false
                        newNumRobot = newNumRobotCh
                        return true
                    }
                }
            }

            if(!recurse(humanInstruction, 0)) { return null }

            return State(newDirRobots, newNumRobot, newCodePos)
        }
    }

    fun solve(code: String, numDirRobots: Int): Int {
        val startState = State(List(numDirRobots) {_ -> 'A'}, 'A', 0)
        val endState = State(List(numDirRobots) {_ -> 'A'}, 'A', code.length)

        val visited: MutableSet<State> = mutableSetOf()
        val queue: ArrayDeque<Pair<State, Int>> = ArrayDeque()
        queue.add(startState to 0)

        while(queue.isNotEmpty()) {
            val (currentState, currentCost) = queue.removeFirst()
            if (currentState == endState) { return currentCost }
            if (currentState in visited) { continue }
            visited.add(currentState)

            for (instruction in listOf('^', '<', 'v', '>', 'A')) {
                val nextState = currentState.next(instruction, code) ?: continue
                if (nextState in visited) { continue }
                queue.add(nextState to currentCost + 1)
            }
        }

        return Int.MAX_VALUE
    }

    override fun partOne(input: String): Long {
        var sum = 0L
        for (code in input.lines()) {
            sum += solve(code, 2).toLong() * (code.dropLast(1).toLong())
        }
        return sum.also { println(it) }
    }

    override fun partTwo(input: String): Long {
        var sum = 0L
        for (code in input.lines()) {
            // Too many states... Need to figure out something better.
            sum += solve(code, 25).toLong() * (code.dropLast(1).toLong())
        }
        return sum.also { println(it) }
    }
}
