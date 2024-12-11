package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import java.util.ArrayDeque

object Y2024D10 : Solution {
    private data class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
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

        fun getNumberOfReachableNines(startPoint: Point): Long {
            // Do bfs.
            val visited: MutableSet<Point> = mutableSetOf()
            val queue: ArrayDeque<Point> = ArrayDeque()
            queue.add(startPoint)
            var nineCount = 0L

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val elevation = this[current] ?: continue
                if (current in visited) { continue }
                visited.add(current)
                if (this[current] == '9') {
                    nineCount++
                    continue
                }

                for (d in Direction.entries) {
                    val next = current + d
                    if (next in visited) { continue }
                    if (this[next] != (elevation.digitToInt() + 1).digitToChar()) { continue }
                    queue.add(next)
                }
            }

            return nineCount
        }

        fun getTrailRatingSum(): Long {
            val cache: MutableMap<Point, Long> = mutableMapOf()

            fun dfs(current: Point): Long {
                cache[current]?.let { return it }
                val elevation = this[current] ?: return 0

                if (this[current] == '9') {
                    cache[current] = 1
                    return 1
                }

                var rating = 0L
                for (d in Direction.entries) {
                    val next = current + d
                    if (this[next] != (elevation.digitToInt() + 1).digitToChar()) { continue }
                    rating += if (next in cache) {
                        cache[next]!!
                    } else {
                        dfs(next)
                    }
                }
                return rating
            }

            return getAllCoords().filter { this[it] == '0' }.sumOf { dfs(it) }
        }
    }

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

    private fun parseInput(input: String): Grid {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, input.lines().map { line -> line.toList() })
    }

    override fun partOne(input: String): Long {
        val grid = parseInput(input)
        return grid.getAllCoords().filter { grid[it] == '0' }.sumOf { grid.getNumberOfReachableNines(it) }.also { println(it) }
    }

   override fun partTwo(input: String): Long =
       parseInput(input).getTrailRatingSum().also { println(it) }
}
