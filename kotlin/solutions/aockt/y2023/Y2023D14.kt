package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D14 : Solution {

    private fun String.toGrid() =
        Grid(lineSequence().map { it.toList() }.toList())

    data class Grid(val grid: List<List<Char>>) {
        val height = grid.size
        val width = grid[0].size

        init {
            val numRound = grid.sumOf { it.count { ch -> ch == 'O' } }
            val numEmpty = grid.sumOf { it.count { ch -> ch == '.' } }
            println("numRound: $numRound, numEmpty: $numEmpty")
        }

        fun solve() : Int {
            var sum = 0
            for (c in 0 until width) {
                var nextPosition = 0

                for (r in 0 until height) {
                    when (grid[r][c]) {
                        '#' -> nextPosition = r + 1
                        'O' -> {
                            sum += height - nextPosition
                            nextPosition++
                        }
                    }
                }
            }
            return sum
        }
    }

    override fun partOne(input: String) =
        input.toGrid().solve().also{println(it)}
}
