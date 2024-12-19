package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D18 : Solution {
    private class Grid(val n: Int, val corruptedList: List<Point>) {
        // Map of corrupted point to which time in ns it was corrupted.
        val corruptedMap: Map<Point, Int> = buildMap {
            for ((i, p) in corruptedList.withIndex()) {
                put(p, i)
            }
        }

        fun hasPoint(p: Point): Boolean {
            return p.i in 0..<n && p.j in 0..<n
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
        val lines = input.lines()
        val (n, searchAtTime) = lines[0].split(" ").map { it.toInt() }
        val corruptedList = lines.drop(1)
            .map { line -> line.split(",").map(String::toInt).let { Point(it[1], it[0]) }}
        return Grid(n, corruptedList)
    }

    private fun distanceToEnd(grid: Grid, time: Int): Int? {
        val goal = Point(grid.n - 1, grid.n - 1)
        val visitedAt: MutableMap<Point, Int> = mutableMapOf()
        val queue: ArrayDeque<Pair<Point, Int>> = ArrayDeque()
        queue.add(Point(0, 0) to 0)

        while (queue.isNotEmpty()) {
            val (current, distance) = queue.removeFirst()
            if (current in visitedAt) { continue }
            if (grid.corruptedMap[current]?.let { it < time } == true) { continue }
            visitedAt[current] = distance

            if (current == goal) {
                return distance
            }

            for (dir in Direction.entries.filter { grid.hasPoint(current + it) }) {
                val next = current + dir
                if (next in visitedAt) { continue }
                queue.add(next to distance + 1)
            }
        }

        return null
    }

    override fun partOne(input: String): Int {
        val grid = parseInput(input)
        val searchAtTime = if (grid.n == 7) { 12 } else { 1024 }
        return distanceToEnd(grid, searchAtTime)!!.also { println(it) }
    }

    override fun partTwo(input: String): String {
        val grid = parseInput(input)

        var low = 0
        var high = grid.corruptedMap.size
        while (low < high) {
            val mid = low + (high - low) / 2
            if (distanceToEnd(grid, mid) == null) {
                high = mid
            } else {
                low = mid + 1
            }
        }

        val res = grid.corruptedList[high - 1]
        return "${res.j},${res.i}".also { println(it) }
    }
}
