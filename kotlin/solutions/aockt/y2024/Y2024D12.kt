package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D12 : Solution {
    private class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
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

        class AreaInfo(var area: Long = 0L, var perimiterLength: Long = 0L, var perimiterCount: Long = 0L)
        val areaInfos: MutableList<AreaInfo> = mutableListOf()
        val pointToAreaId: MutableMap<Point, Int> = mutableMapOf()

        fun computeNumPerimetersForRowOrCol(rowOrCol: List<Point>, perimiterDirection: Direction) {
            var active = false
            for ((i, p) in rowOrCol.withIndex()) {
                if (this[p + perimiterDirection] == this[p]) {
                    // Not a perimiter.
                    active = false
                    continue
                }
                if (i > 0 && this[p] != this[rowOrCol[i-1]]) {
                    // We went into a different area, any previous perimeter is no longer active.
                    active = false
                }
                // We are on a perimiter.
                if (!active) {
                    // We are on a new perimeter.
                    areaInfos[pointToAreaId[p]!!].perimiterCount++
                    active = true
                }
            }
        }

        fun computeNumPerimeters() {
            // Row by row
            for (i in 0..<h) {
                computeNumPerimetersForRowOrCol((0..<w).map { Point(i, it) }, Direction.NORTH)
                computeNumPerimetersForRowOrCol((0..<w).map { Point(i, it) }, Direction.SOUTH)
            }
            // Col by col
            for (i in 0..<w) {
                computeNumPerimetersForRowOrCol((0..<h).map { Point(it, i) }, Direction.EAST)
                computeNumPerimetersForRowOrCol((0..<h).map { Point(it, i) }, Direction.WEST)
            }
        }

        fun computeAreas() {
            val visited: MutableSet<Point> = mutableSetOf()

            for (startPoint in getAllCoords()) {
                if (startPoint in visited) { continue }
                val areaId = areaInfos.size
                val areaInfo = AreaInfo()
                areaInfos.add(areaInfo)

                val queue: ArrayDeque<Point> = ArrayDeque()
                queue.add(startPoint)

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    if (current in visited) { continue }
                    visited.add(current)
                    pointToAreaId[current] = areaId

                    areaInfo.area++
                    for (d in Direction.entries) {
                        val next = current + d

                        if (this[next] == this[current]) {
                            if (next !in visited) {
                                queue.add(next)
                            }
                        } else {
                            areaInfo.perimiterLength++
                        }
                    }
                }
            }
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
        grid.computeAreas()
        return grid.areaInfos.sumOf { it.area * it.perimiterLength }
            .also { println(it) }
    }

    override fun partTwo(input: String): Long {
        val grid = parseInput(input)
        grid.computeAreas()
        grid.computeNumPerimeters()
        return grid.areaInfos.sumOf { it.area * it.perimiterCount }
            .also { println(it) }
    }
}
