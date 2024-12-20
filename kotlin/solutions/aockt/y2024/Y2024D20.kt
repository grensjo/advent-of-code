package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import aockt.y2024.Y2024D20.Direction.*

object Y2024D20 : Solution {
    private data class Point(val i: Int, val j: Int) {
        fun next(d: Direction) = Point(i + d.di, j + d.dj)
        operator fun plus(d: Direction) = next(d)
        operator fun plus(p: Point) = Point(i + p.i, j + p.j)
        operator fun minus(p: Point) = Point(i - p.i, j - p.j)
    }

    private enum class Direction(val di: Int, val dj: Int) {
        NORTH(-1, 0),
        EAST(0, 1),
        SOUTH(1, 0),
        WEST(0, -1),
    }
    private val directions: Map<Point, Direction> = Direction.entries.associateBy { Point(it.di, it.dj) }

    private class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
        operator fun get(p: Point): Char? {
            return grid.getOrNull(p.i)?.getOrNull(p.j)
        }
        fun hasPoint(p: Point): Boolean {
            return p.i in 0..<h && p.j in 0..<w
        }

        fun getAllCoords(): List<Point> =
            buildList {
                for (i in 0..<h) {
                    for (j in 0..<w) {
                        add(Point(i, j))
                    }
                }
            }

        val start = getAllCoords().first { this[it] == 'S' }
        val end = getAllCoords().first { this[it] == 'E' }

        fun getShortestPathsFrom(p: Point): Map<Point, Int> {
            val queue: ArrayDeque<Pair<Point, Int>> = ArrayDeque()
            val visited: MutableMap<Point, Int> = mutableMapOf()
            queue.add(p to 0)

            while (queue.isNotEmpty()) {
                val (currentPoint, currentDist) = queue.removeFirst()
                if (currentPoint in visited) { continue }
                visited[currentPoint] = currentDist

                for (dir in Direction.entries) {
                    val nextPoint = currentPoint + dir
                    if (!hasPoint(nextPoint)) { continue }
                    if (this[nextPoint] == '#') { continue }
                    queue.add(nextPoint to currentDist + 1)
                }
            }
            return visited
        }

        fun getCheats(): Map<Int, Int> {
            val distFromStart = getShortestPathsFrom(start)
            val distFromEnd = getShortestPathsFrom(end)
            val distNoCheating = distFromStart[end]!!

            // how much you save -> count of cheats
            val cheats: MutableMap<Int, Int> = mutableMapOf()

            fun calculateCheat(p1: Point, p2: Point): Int? {
                if (hasPoint(p1) && hasPoint(p2) && this[p1] != '#' && this[p2] != '#') {
                    val newDist = distFromStart[p1]!! + 2 + distFromEnd[p2]!!
                    if (newDist < distNoCheating) { return newDist }
                }
                return null
            }

            for (p in getAllCoords()) {
                if (this[p] != '#') { continue }

                for (dirFrom in Direction.entries) {
                    for (dirTo in Direction.entries) {
                        if (dirFrom == dirTo) { continue }
                        val cheatDist = calculateCheat(p + dirFrom, p + dirTo)
                        if (cheatDist != null) {
                            val savings = distNoCheating - cheatDist
                            cheats.merge(savings, 1) { v1, v2 -> v1 + v2}
                        }
                    }
                }
            }

            return cheats
        }


    }

    private fun parseInput(input: String): Grid {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, input.lines().map { line -> line.toList() })
    }

    override fun partOne(input: String): Int {
        val grid = parseInput(input)
        val cheats = grid.getCheats()
        return cheats.entries.filter { (k, v) -> k >= 100}.sumOf { (k, v) -> v}.also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}
