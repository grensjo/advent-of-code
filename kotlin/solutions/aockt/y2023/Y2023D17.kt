package aockt.y2023

import aockt.y2023.Y2023D17.Direction.*
import io.github.jadarma.aockt.core.Solution
import java.util.Comparator
import java.util.PriorityQueue

object Y2023D17 : Solution {

    enum class Direction { UNSPECIFIED, NORTH, WEST, SOUTH, EAST }
    data class Node(val r: Int, val c: Int, val dir: Direction, val dirCount: Int, val ultra: Boolean) {
        private fun isValid(numRows: Int, numCols: Int) =
            r >= 0 && c >= 0 && r < numRows && c < numCols

        fun getNeighbours(numRows: Int, numCols: Int) : List<Node> =
            Direction.entries
                .asSequence()
                .filterNot {
                    if (ultra) {
                        if (dirCount < 4) dir != UNSPECIFIED && dir != it else if (dirCount == 10) it == dir else false
                    } else {
                        it == dir && dirCount == 3
                    }
                }
                // Cannot turn 180 degrees.
                .filterNot {
                    it == when (dir) {
                        NORTH -> SOUTH
                        SOUTH -> NORTH
                        EAST -> WEST
                        WEST-> EAST
                        UNSPECIFIED -> UNSPECIFIED
                    }
                }
                .filterNot { it == UNSPECIFIED }
                .map {
                    when (it) {
                        NORTH -> Node(r - 1, c, NORTH, if (dir == NORTH) dirCount + 1 else 1, ultra)
                        SOUTH -> Node(r + 1, c, SOUTH, if (dir == SOUTH) dirCount + 1 else 1, ultra)
                        WEST -> Node(r, c - 1, WEST, if (dir == WEST) dirCount + 1 else 1, ultra)
                        EAST -> Node(r, c + 1, EAST, if (dir == EAST) dirCount + 1 else 1, ultra)
                        UNSPECIFIED -> throw AssertionError("Trying to move in UNSPEICIFIED direction.")
                    }
                }
                .filter { it.isValid(numRows, numCols) }
                .toList()
    }

    data class City(val costMap: Map<Node, Int>, val ultra: Boolean) {
        private val numRows: Int by lazy { costMap.maxOf { it.key.r } + 1 }
        private val numCols: Int by lazy { costMap.maxOf { it.key.c } + 1 }

        fun shortestPath() : Int {
            val visited: MutableSet<Node> = mutableSetOf()
            val queue: PriorityQueue<Pair<Node, Int>> = PriorityQueue(Comparator.comparing { it.second })
            queue.add(Node(0, 0, UNSPECIFIED, 0, ultra) to 0)

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (current.first in visited) continue
                if (current.first.r == numRows - 1 && current.first.c == numCols - 1) {
                    return current.second
                }
                visited += current.first
                val neighbours = current.first.getNeighbours(numRows, numCols).map { it to current.second + costMap[it]!! }.filterNot { it.first in visited }
                queue.addAll(neighbours)
            }

            throw AssertionError("No path found.")
        }
    }

    private fun String.toCity(ultra: Boolean) = City(
        lineSequence().withIndex().flatMap { (r, row) ->
            row.withIndex().flatMap { (c, ch) ->
                buildList {
                    for (dir in Direction.entries) {
                        for (dirCount in 0..10) {
                            add(Node(r, c, dir, dirCount, ultra) to ch.digitToInt())
                        }
                    }
                }
            }
        }.toMap(), ultra)

    override fun partOne(input: String) =
        input.toCity(ultra = false).shortestPath().also { println(it) }


    override fun partTwo(input: String) =
        input.toCity(ultra = true).shortestPath().also { println(it) }
}
