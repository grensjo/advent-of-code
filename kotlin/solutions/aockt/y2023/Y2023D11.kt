package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

object Y2023D11 : Solution {
    data class Point(val r: Int, val c: Int)

    data class Image(val grid: MutableList<MutableList<Char>>, val expansionRate: Long) {
        private fun getHeight() = grid.size
        private fun getWidth() = grid[0].size

        private var emptyRows: List<Int> = buildList {
            for (r in (0 until getHeight())) {
                if (grid[r].all { it == '.' }) {
                    add(r)
                }
            }
        }
        private var emptyColumns: List<Int> = buildList {
            for (c in (0 until getWidth())) {
                if ((0 until getHeight()).all { grid[it][c] == '.' }) {
                    add(c)
                }
            }
        }

        fun getGalaxies() =
            buildList {
                for (r in 0 until getHeight()) {
                    for (c in 0 until getWidth()) {
                        if (grid[r][c] == '#') {
                            add(Point(r, c))
                        }
                    }
                }
            }

        fun distance(p1: Point, p2: Point) : Long {
            val imageDist: Long = (p1.r - p2.r).absoluteValue.toLong() + (p1.c - p2.c).absoluteValue.toLong()
            val expansion =
                emptyRows.count { it in min(p1.r, p2.r) until max(p1.r, p2.r) }.toLong() * (expansionRate - 1) +
                emptyColumns.count { it in min(p1.c, p2.c) until max(p1.c, p2.c) }.toLong() * (expansionRate - 1)
            return imageDist + expansion
        }
    }

    private fun String.toImage(expansionRate: Long) =
        Image(lineSequence().map { it.toMutableList() }.toMutableList(), expansionRate)

    private fun solve(image: Image): Long {
        image.getGalaxies()
        val galaxies = image.getGalaxies()
        var sum = 0L
        for (i in 0 until (galaxies.size - 1)) {
            for (j in (i+1) until galaxies.size) {
                sum += image.distance(galaxies[i], galaxies[j])
            }
        }
        return sum
    }

    override fun partOne(input: String) = solve(input.toImage(expansionRate = 2L))
    override fun partTwo(input: String) = solve(input.toImage(expansionRate = 1000000))
}