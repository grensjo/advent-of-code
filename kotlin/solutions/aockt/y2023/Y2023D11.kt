package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2023D11 : Solution {
    data class Point(val r: Int, val c: Int) {
        infix fun distanceTo(other: Point) = (r - other.r).absoluteValue + (c - other.c).absoluteValue
    }

    data class Image(val grid: MutableList<MutableList<Char>>) {
        private fun getHeight() = grid.size
        private fun getWidth() = grid[0].size

        fun extend() {
            println("Extending, size before: ${getHeight()} x ${getWidth()}")
            printImage()
            for (r in (0 until getHeight()).reversed()) {
                if (grid[r].all { it == '.' }) {
                    println("Adding a row at $r")
                    grid.add(r, (0 until getWidth()).map { '.' }.toMutableList())
                }
            }
            for (c in (0 until getWidth()).reversed()) {
                if ((0 until getHeight()).all { grid[it][c] == '.' }) {
                    println("Adding a column at $c")
                    (0 until getHeight()).forEach {grid[it].add(c, '.') }
                }
            }
            println("Finished extending, size after: ${getHeight()} x ${getWidth()}")
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
            }.also { println(it) }

        private fun printImage() = println(grid.map{ it.joinToString("") }.joinToString("\n"))
    }

    private fun String.toImage() = Image(lineSequence().map { it.toMutableList() }.toMutableList())

    override fun partOne(input: String): Int {
        val image = input.toImage()
        image.getGalaxies()
        image.extend()
        val galaxies = image.getGalaxies()
        var sum = 0
        for (i in 0 until (galaxies.size - 1)) {
            for (j in (i+1) until galaxies.size) {
                val d = galaxies[i] distanceTo galaxies[j]
                println("Distance between galaxy #$i (${galaxies[i]}) and #$j (${galaxies[j]}): $d")
                sum += d
                println("New sum: $sum")
            }
        }
        return sum
    }

    override fun partTwo(input: String): Int =
        input.lineSequence().count()
}