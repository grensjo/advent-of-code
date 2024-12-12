package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D12 : Solution {
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

        fun computeCost(): Long {
            val visited: MutableSet<Point> = mutableSetOf()
            var cost = 0L

            for (startPoint in getAllCoords()) {
                if (startPoint in visited) { continue }
                var perimiter = 0L
                var area = 0L
                val queue: ArrayDeque<Point> = ArrayDeque()
                queue.add(startPoint)

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    if (current in visited) { continue }
                    visited.add(current)

                    area++
                    for (d in Direction.entries) {
                        val next = current + d

                        if (this[next] == this[current]) {
                            if (next !in visited) {
                                queue.add(next)
                            }
                        } else {
                            perimiter++
                        }
                    }
                }

                cost += (perimiter * area)
            }

            return cost
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
        return grid.computeCost().also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}
