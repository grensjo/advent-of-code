package aockt.y2024

import aockt.y2024.Y2024D04.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2024D04 : Solution {
    data class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }

        companion object {
            fun fromInput(input: String): Grid {
                val l = input.lines()
                val h = l.size
                val w = l[0].length
                assert(l.all {it.length == w})
                return Grid(h, w, input.lines().map { line -> line.toList() })
            }
        }

        fun hasMasX(p: Point) : Boolean {
            if (get(p) != 'A') {
                return false
            }

            val d1 =
                (get(p + NORTHWEST) == 'M' && get(p + SOUTHEAST) == 'S') ||
                (get(p + NORTHWEST) == 'S' && get(p + SOUTHEAST) == 'M')

            val d2 =
                (get(p + NORTHEAST) == 'M' && get(p + SOUTHWEST) == 'S') ||
                (get(p + NORTHEAST) == 'S' && get(p + SOUTHWEST) == 'M')

            return d1 && d2
        }
    }

    data class Point(val i: Int, val j: Int) {
        fun next(d: Direction) = Point(i + d.di, j + d.dj)
        operator fun plus(d: Direction) = next(d)
    }

    enum class Direction(val di: Int, val dj: Int) {
        NORTH(-1, 0),
        NORTHEAST(-1, 1),
        EAST(0, 1),
        SOUTHEAST(1, 1),
        SOUTH(1, 0),
        SOUTHWEST(1, -1),
        WEST(0, -1),
        NORTHWEST(-1, -1),
    }

    private const val WORD = "XMAS"
    private fun countWords(grid: Grid, p: Point, d: Direction, chars: MutableList<Char>): Int {
        val newChar = grid.get(p) ?: return 0
        chars.add(newChar)
        val match : Boolean = chars.toCharArray().concatToString().endsWith(WORD)
        return countWords(grid, p.next(d), d, chars) + if (match) 1 else 0
    }

    override fun partOne(input: String) : Int {
        val grid = Grid.fromInput(input)

        var sum = 0
        for (i in 0..<grid.w) {
            sum += countWords(grid, Point(0, i), SOUTHEAST, mutableListOf())
            sum += countWords(grid, Point(0, i), SOUTH, mutableListOf())
            sum += countWords(grid, Point(0, i), SOUTHWEST, mutableListOf())

            sum += countWords(grid, Point(grid.h - 1, i), NORTHEAST, mutableListOf())
            sum += countWords(grid, Point(grid.h - 1, i), NORTH, mutableListOf())
            sum += countWords(grid, Point(grid.h - 1, i), NORTHWEST, mutableListOf())
        }
        for (i in 0..<grid.h) {
            sum += countWords(grid, Point(i, 0), EAST, mutableListOf())
            sum += countWords(grid, Point(i, grid.w - 1), WEST, mutableListOf())

            // Don't double-count the corners.
            if (i == 0 || i == grid.h-1) continue

            sum += countWords(grid, Point(i, 0), NORTHEAST, mutableListOf())
            sum += countWords(grid, Point(i, 0), SOUTHEAST, mutableListOf())
            sum += countWords(grid, Point(i, grid.w - 1), NORTHWEST, mutableListOf())
            sum += countWords(grid, Point(i, grid.w - 1), SOUTHWEST, mutableListOf())
        }

        return sum.also { println(it) }
    }

    override fun partTwo(input: String) : Int {
        val grid = Grid.fromInput(input)
        var count = 0
        for (i in 0..<grid.h) {
            for (j in 0..<grid.w) {
                if (grid.hasMasX(Point(i, j))) {
                    count += 1
                }
            }
        }
        return count.also { println(it) }
    }
}
