package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private fun String.toGrid() =
    Y2023D13.Grid(lineSequence().map { it.toList() }.toList())

object Y2023D13 : Solution {
    data class Grid(val grid: List<List<Char>>) {
        private val height = grid.size
        private val width = grid[0].size

        private fun isReflectedAt(i: Int) =
            grid.slice(0..i)
                .reversed()
                .zip(
                    grid.slice((i+1) until height)
                ).all { (r1, r2) -> r1 == r2 }

        private fun toTransposed(): Grid =
            Grid(buildList {
                for (i in 0 until width) {
                    add(buildList {
                        for (j in 0 until height) {
                            add(grid[j][i])
                        }
                    })
                }
            })

        private fun getHorizontalReflectionSum(): Int =
            (0..(height-2)).filter { isReflectedAt(it) }.sumOf { it + 1 }

        fun solve() : Int =
            100 * getHorizontalReflectionSum() + toTransposed().getHorizontalReflectionSum()

        private fun flipSmudge(i: Int, j: Int) =
            Grid(
                grid.withIndex().map { (r, row) ->
                    when {
                        r == i -> row.withIndex().map { (c, char) ->
                            when {
                                c == j && char == '#' -> '.'
                                c == j && char == '.' -> '#'
                                else -> char
                            }
                        }
                        else -> row
                    }
                }
            )

        fun solveWithSmudge() : Int {
            val sumWithoutSmudge = solve()

            for (i in 0 until height) {
                for (j in 0 until width) {
                    // Flip grid[i][j], and see if we got a different reflection line
                    val sumWithSmudge = flipSmudge(i, j).solve()
                    if (sumWithSmudge != sumWithoutSmudge && sumWithSmudge > 0) {
                        println("Flipping ($i, $j) made the sum go $sumWithoutSmudge -> $sumWithSmudge")
                        return sumWithSmudge
                    }
                }
            }

            throw IllegalArgumentException()
        }
    }

    override fun partOne(input: String) =
            input.split("\n\n")
                .map(String::toGrid)
                .sumOf(Grid::solve)
                .also{println(it)}

    override fun partTwo(input: String) =
        input.split("\n\n")
            .map(String::toGrid)
            .sumOf(Grid::solveWithSmudge)
            .also{println(it)}
}

