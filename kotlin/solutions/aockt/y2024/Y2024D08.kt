package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import java.math.BigInteger

object Y2024D08 : Solution {
    private data class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        operator fun get(v: Vec2D): Char? {
            return grid.getOrNull(v.i)?.getOrNull(v.j)
        }

        fun isWithinGrid(v: Vec2D): Boolean =
            v.i in 0..<h && v.j in 0..<w

        fun getAllCoords() =
            buildList {
                for (i in 0..<h) {
                    for (j in 0..<w) {
                        add(Vec2D(i, j))
                    }
                }
            }
    }

    private fun Long.gcd(b: Long) = BigInteger.valueOf(this).gcd(BigInteger.valueOf(b)).toLong()
    private fun Int.gcd(b: Int) = toLong().gcd(b.toLong()).toInt()

    private data class Vec2D(val i: Int, val j: Int) {
        operator fun plus(v: Vec2D) = Vec2D(i + v.i, j + v.j)
        operator fun minus(v: Vec2D) = Vec2D(i - v.i, j - v.j)
        operator fun times(n: Int) = Vec2D(i * n, j * n)

        // Divide both coordinates by their gcd so we get the shortest possible integer vector in the same
        // direction.
        fun reduce(): Vec2D {
            val gcd = i.gcd(j)
            return Vec2D(i / gcd, j / gcd)
        }
    }

    private fun parseInput(input: String): Grid {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, input.lines().map { line -> line.toList() })
    }

    private fun computeAntinodes(frequency: Char, antennas: List<Vec2D>, grid: Grid): Set<Vec2D> {
        val antinodes: MutableSet<Vec2D> = mutableSetOf()
        for ((i, a1) in antennas.withIndex()) {
            for (a2 in antennas.drop(i+1)) {
                val d = a2 - a1
                val r1 = a2 + d
                if (grid.isWithinGrid(r1)) {
                    antinodes += r1
                }
                val r2 = a1 - d
                if (grid.isWithinGrid(r2)) {
                    antinodes += r2
                }
            }
        }
        return antinodes
    }

    private fun computeResonantAntinodes(frequency: Char, antennas: List<Vec2D>, grid: Grid): Set<Vec2D> {
        val antinodes: MutableSet<Vec2D> = mutableSetOf()
        for ((i, a1) in antennas.withIndex()) {
            for (a2 in antennas.drop(i+1)) {
                // Divide both coordinates by their gcd so we get the shortest possible integer vector in the same
                // direction.
                val d = (a2 - a1).reduce()

                // Add `d` until we get outside the grid.
                var current = a1
                while (grid.isWithinGrid(current)) {
                    antinodes += current
                    current += d
                }
                // Subtract `d` until we get outside the grid.
                current = a1 - d
                while (grid.isWithinGrid(current)) {
                    antinodes += current
                    current -= d
                }
            }
        }
        return antinodes
    }

    private fun solve(input: String, isPart2: Boolean): Int {
        val grid = parseInput(input)
        val frequencyToAntennas = grid.getAllCoords().filter { grid[it] != '.'}.groupBy { grid[it]!! }

        val antinodes: MutableSet<Vec2D> = mutableSetOf()
        for ((frequency, antennas) in frequencyToAntennas) {
            antinodes += when (isPart2) {
                false -> computeAntinodes(frequency, antennas, grid)
                true -> computeResonantAntinodes(frequency, antennas, grid)
            }
        }

        return antinodes.size.also { println(it) }
    }

    override fun partOne(input: String): Int =
        solve(input, isPart2 = false)

    override fun partTwo(input: String): Int =
        solve(input, isPart2 = true)
}
