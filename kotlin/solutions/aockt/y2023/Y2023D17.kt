package aockt.y2023

import aockt.y2023.Y2023D17.Direction.*
import io.github.jadarma.aockt.core.Solution
import java.util.Comparator
import java.util.PriorityQueue
import kotlin.math.cos

object Y2023D17 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }
    data class Node(val r: Int, val c: Int, val dir: Direction, val dirCount: Int) {
        private fun isValid(numRows: Int, numCols: Int) =
            r >= 0 && c >= 0 && r < numRows && c < numCols

        fun getNeighbours(numRows: Int, numCols: Int) : List<Node> =
            Direction.entries.filterNot { it == dir && dirCount == 3 }
                .filterNot {
                    it == when (dir) {
                        NORTH -> SOUTH
                        SOUTH -> NORTH
                        EAST -> WEST
                        WEST->EAST
                    }
                }
                .map {
                    when (it) {
                        NORTH -> Node(r - 1, c, NORTH, if (dir == NORTH) dirCount + 1 else 1)
                        SOUTH -> Node(r + 1, c, SOUTH, if (dir == SOUTH) dirCount + 1 else 1)
                        EAST -> Node(r, c - 1, EAST, if (dir == EAST) dirCount + 1 else 1)
                        WEST -> Node(r, c + 1, WEST, if (dir == WEST) dirCount + 1 else 1)
                    }
                }
                .filter { it.isValid(numRows, numCols) }
    }

    data class City(val costMap: Map<Node, Int>) {
        private val numRows: Int by lazy { costMap.maxOf { it.key.r } + 1 }
        private val numCols: Int by lazy { costMap.maxOf { it.key.c } + 1 }

        fun shortestPath() : Int {
            val visited: MutableSet<Node> = mutableSetOf()
            val queue: PriorityQueue<Pair<Node, Int>> = PriorityQueue(Comparator.comparing { it.second })
            queue.add(Node(0, 0, NORTH, 0) to 0) //.let { it to costMap[it]!! })

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (current.first in visited) continue
                if (current.first.r == numRows - 1 && current.first.c == numCols - 1) return current.second
                visited += current.first
                queue.addAll(current.first.getNeighbours(numRows, numCols).map { it to current.second + costMap[it]!! }.filterNot { it.first in visited })
            }

            throw AssertionError("No path found.")
        }
    }

    private fun String.toCity() = City(
        lineSequence().withIndex().flatMap { (r, row) ->
            row.withIndex().flatMap { (c, ch) ->
                buildList {
                    for (dir in Direction.entries) {
                        for (dirCount in 0..3) {
                            add(Node(r, c, dir, dirCount) to ch.digitToInt())
                        }
                    }
                }
            }
        }.toMap()
    )

    override fun partOne(input: String) =
        input.toCity().shortestPath().also { println(it) }


//    override fun partTwo(input: String) = input.length
}
