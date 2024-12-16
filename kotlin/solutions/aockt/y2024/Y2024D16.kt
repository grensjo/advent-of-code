package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import java.lang.AssertionError
import java.util.PriorityQueue

private data class Point(val i: Int, val j: Int) {
    fun next(d: Direction) = Point(i + d.di, j + d.dj)
    operator fun plus(d: Direction) = next(d)
}

private enum class Direction(val di: Int, val dj: Int) {
    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    fun rotateClockwise(): Direction =
        when(this){
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }

    fun rotateCounterclockwise(): Direction =
        when(this){
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
        }
}

private data class ReindeerState(val pos: Point, val dir: Direction) {
    val neighboursToCost
        get() = listOf(
            ReindeerState(pos + dir, dir) to 1L,
            ReindeerState(pos, dir.rotateClockwise()) to 1000L,
            ReindeerState(pos, dir.rotateCounterclockwise()) to 1000L,
    )
}

private data class Grid(val h: Int, val w: Int, val grid: List<List<Char>>) {
    val startState = ReindeerState(getAllCoords().first { this[it] == 'S' }, Direction.EAST)

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

}

object Y2024D16 : Solution {
    private fun parseInput(input: String): Grid {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, input.lines().map { line -> line.toList() })
    }

    private fun Grid.dijkstra(): Long {
        val priorityQueue: PriorityQueue<Pair<ReindeerState, Long>> =
            PriorityQueue(Comparator.comparingLong { it.second })
        val costs: MutableMap<ReindeerState, Long> = mutableMapOf()
        priorityQueue.add(startState to 0L)

        while (priorityQueue.isNotEmpty()) {
            val (currentState, currentCost) = priorityQueue.poll()
            if (currentState in costs) { continue }
            if (this[currentState.pos] == '#') { continue }
            costs[currentState] = currentCost
            if (this[currentState.pos] == 'E') { return currentCost }

            for ((nextState, nextCost) in currentState.neighboursToCost) {
                if (this[nextState.pos] == '#') { continue }
                if (nextState in costs) { continue }
                priorityQueue.add(nextState to (currentCost + nextCost))
            }
        }
        throw AssertionError("No path to goal.")
    }

    override fun partOne(input: String): Long {
        val grid = parseInput(input)
        return grid.dijkstra().also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}
