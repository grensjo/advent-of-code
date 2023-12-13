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

        private fun getHorizontalReflections() : List<Int> =
            (0..(height-2)).filter { isReflectedAt(it) }.map { it + 1 }.toList()

        private fun toTransposed() : Grid =
            Grid(buildList {
                for (i in 0 until width) {
                    add(buildList {
                        for (j in 0 until height) {
                            add(grid[j][i])
                        }
                    })
                }
            })

        fun getReflections() : List<Int> =
            getHorizontalReflections().map { it * 100 } + toTransposed().getHorizontalReflections()

        private fun flipSmudge(i: Int, j: Int) : Grid =
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

        fun findSmudgedReflections() : Set<Int> {
            val originalReflections = getReflections().toSet()

            for (i in 0 until height) {
                for (j in 0 until width) {
                    // Flip grid[i][j], and see if we got any new reflection lines.
                    val smudgedReflections = flipSmudge(i, j).getReflections().toSet()
                    val newReflections = smudgedReflections - originalReflections
                    if (newReflections.isNotEmpty()) {
                        return newReflections
                    }
                }
            }

            throw IllegalArgumentException("No flips created new reflections.")
        }
    }

    override fun partOne(input: String) =
        input.split("\n\n")
            .map(String::toGrid)
            .flatMap(Grid::getReflections)
            .sum()
            .also{println(it)}

    override fun partTwo(input: String) =
        input.split("\n\n")
            .map(String::toGrid)
            .flatMap(Grid::findSmudgedReflections)
            .sum()
            .also{println(it)}
}
