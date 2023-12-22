package aockt.y2023

import aockt.y2023.Y2023D21.Direction.*
import aockt.y2023.Y2023D21.Farm
import aockt.y2023.Y2023D21.TileType.*
import io.github.jadarma.aockt.core.Solution
import java.util.*
import kotlin.collections.ArrayDeque

private fun Char.toTileType() =
    when (this) {
        'S' -> START
        '.' -> GARDEN
        '#' -> ROCK
        else -> throw IllegalArgumentException("$this is not a valid tile type.")
    }

private fun String.toFarm() = Farm(lineSequence().map { it.map(Char::toTileType) }.toList())

object Y2023D21 : Solution {

    enum class Direction { NORTH, WEST, SOUTH, EAST }
    enum class TileType { START, GARDEN, ROCK }

    data class Node(val r: Int, val c: Int) {
        private fun isValid(numRows: Int, numCols: Int) =
            r >= 0 && c >= 0 && r < numRows && c < numCols

        fun getNeighbours(numRows: Int, numCols: Int) : List<Node> =
            Direction.entries
                .asSequence()
                .map {
                    when (it) {
                        NORTH -> Node(r - 1, c)
                        SOUTH -> Node(r + 1, c)
                        WEST -> Node(r, c - 1)
                        EAST -> Node(r, c + 1)
                    }
                }
                .filter { it.isValid(numRows, numCols) }
                .toList()
    }

    data class Farm(val grid: List<List<TileType>>) {
        private val numRows: Int = grid.size
        private val numCols: Int = grid[0].size

        fun countNodesAtDistance(distance: Int) : Int {
            println("countNodesAtDistance()")
            var currentNodes: Set<Node> = mutableSetOf()
            var nextNodes: MutableSet<Node> = mutableSetOf()
            for (r in 0 until numRows) {
                for (c in 0 until numCols) {
                    if (grid[r][c] == START) {
                        currentNodes += Node(r, c)
                        break
                    }
                }
                if (currentNodes.isNotEmpty()) break
            }

            for (currentStep in 0 until 64) {
                for (current in currentNodes) {
                    val neighbours = current.getNeighbours(numRows, numCols)
                        .filter { (r, c) -> grid[r][c] in listOf(START, GARDEN) }
                    nextNodes.addAll(neighbours)
                }
                currentNodes = nextNodes
                nextNodes = mutableSetOf()
            }

            return currentNodes.size
        }
    }

    override fun partOne(input: String) = input.toFarm().countNodesAtDistance(64).also { println(it) }

//    override fun partTwo(input: String) = input.length
}
