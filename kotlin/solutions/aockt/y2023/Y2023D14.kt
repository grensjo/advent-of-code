package aockt.y2023

import aockt.y2023.Y2023D14.Direction.EAST
import aockt.y2023.Y2023D14.Direction.NORTH
import aockt.y2023.Y2023D14.Direction.SOUTH
import aockt.y2023.Y2023D14.Direction.WEST
import io.github.jadarma.aockt.core.Solution

private fun List<List<Char>>.copy() = map { it.toMutableList() }

object Y2023D14 : Solution {

    private fun String.toPlatform() =
        Platform(lineSequence().map { it.toMutableList() }.toList())

    enum class Direction { NORTH, WEST, SOUTH, EAST }

    data class Platform(val grid: List<MutableList<Char>>) {
        private val height = grid.size
        private val width = grid[0].size

        fun getLoad(): Int {
            var sum = 0
            for (c in 0 until width) {
                for (r in 0 until height) {
                    if (grid[r][c] == 'O') sum += height - r
                }
            }
            return sum
        }

        fun tilt(direction: Direction) {
            when (direction) {
                NORTH -> {
                    for (c in 0 until width) {
                        var nextPosition = 0
                        for (r in 0 until height) {
                            when (grid[r][c]) {
                                '#' -> nextPosition = r + 1
                                'O' -> {
                                    grid[r][c] = '.'
                                    grid[nextPosition++][c] = 'O'
                                }
                            }
                        }
                    }
                }
                WEST -> {
                    for (r in 0 until height) {
                        var nextPosition = 0
                        for (c in 0 until width) {
                            when (grid[r][c]) {
                                '#' -> nextPosition = c + 1
                                'O' -> {
                                    grid[r][c] = '.'
                                    grid[r][nextPosition++] = 'O'
                                }
                            }
                        }
                    }
                }
                SOUTH -> {
                    for (c in 0 until width) {
                        var nextPosition = height - 1
                        for (r in (0 until height).reversed()) {
                            when (grid[r][c]) {
                                '#' -> nextPosition = r - 1
                                'O' -> {
                                    grid[r][c] = '.'
                                    grid[nextPosition--][c] = 'O'
                                }
                            }
                        }
                    }
                }
                EAST -> {
                    for (r in 0 until height) {
                        var nextPosition = width - 1
                        for (c in (0 until width).reversed()) {
                            when (grid[r][c]) {
                                '#' -> nextPosition = c - 1
                                'O' -> {
                                    grid[r][c] = '.'
                                    grid[r][nextPosition--] = 'O'
                                }
                            }
                        }
                    }
                }
            }
        }

        fun tiltAll() = listOf(NORTH, WEST,SOUTH, EAST).forEach(::tilt)
    }

    override fun partOne(input: String) : Int {
        val platform = input.toPlatform()
        platform.tilt(NORTH)
        return platform.getLoad().also{ println(it) }
    }

    /**
     * See this as a graph problem, with the possible grid configurations as nodes. Each node
     * has an edge to a single other node: the one you get to by tilting once in each direction.
     * There has to be a cycle; utilize that to find the north beam load after a billion iterations.
     */
    override fun partTwo(input: String) : Int {
        val platform = input.toPlatform()

        // Tilt the grid in all directions until we encounter a configuration we have previously
        // seen, i.e. traverse the graph from the start node until we find a cycle.
        var steps = 0
        val gridHashToStep : MutableMap<List<MutableList<Char>>, Int> = mutableMapOf()
        val stepToLoad : MutableList<Int> = mutableListOf()
        while (platform.grid !in gridHashToStep) {
            gridHashToStep[platform.grid.copy()] = steps++
            stepToLoad.add(platform.getLoad())
            platform.tiltAll()
        }

        val stepsToCycle = gridHashToStep.getValue(platform.grid)
        val cycleLength = steps - stepsToCycle
        val resultIndex = stepsToCycle + ((1000000000 - stepsToCycle) % cycleLength)
        println("steps: $steps, stepsToCycle: $stepsToCycle, cycleLength: $cycleLength")
        println("resultIndex: $resultIndex, result: ${stepToLoad[resultIndex]}")
        return stepToLoad[resultIndex].also { println(it) }
    }
}
