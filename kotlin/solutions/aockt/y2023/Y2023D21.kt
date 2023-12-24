package aockt.y2023

import aockt.y2023.Y2023D21.Direction.*
import aockt.y2023.Y2023D21.Farm
import aockt.y2023.Y2023D21.TileType.*
import io.github.jadarma.aockt.core.Solution

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

        // Create a new farm identical to this one, but tripled horizontally. I.e. if the original farm looks  like F,
        // the new one will look like FFF. Will be very useful for part 2. :)
        fun toTripleFarmRow() =
            Farm(
                buildList {
                    for (r in 0 until numRows) {
                        add(buildList {
                            for (c in 0 until numCols * 3) {
                                add(grid[r][c % numCols])
                            }
                        })
                    }
                }
            )

        fun countNodesAtDistance(distance: Int, startRow: Int? = null, startCol: Int? = null) : Int {
            // TODO: optimize this using the insight that we can always move back to a node with 2 steps, so we don't
            //  need to keep searching from each node at every step.
            var currentNodes: MutableSet<Node> = mutableSetOf()
            var nextNodes: MutableSet<Node> = mutableSetOf()

            if (startRow != null && startCol != null) {
                currentNodes += Node(startRow, startCol)
            } else {
                // If no explicit start point was provided, use the one from the input.
                for (r in 0 until numRows) {
                    for (c in 0 until numCols) {
                        if (grid[r][c] == START) {
                            currentNodes += Node(r, c)
                            break
                        }
                    }
                    if (currentNodes.isNotEmpty()) break
                }
            }

            for (currentStep in 0 until distance) {
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

    override fun partTwo(input: String): Long {
        val farm = input.toFarm()

        var evenUnits = 1L
        var oddUnits = 0L

        // The given number of steps is 202300 * 131 + 65. 131 is the input grid size, 65 is the number of steps from
        // the middle (start) cell to the edge. TODO: write better comments to explain this...
        val N = 202300L
        for (i in 1L until N) {
            val num = 4L*i
            if (i % 2L == 1L) {
                oddUnits += num
            } else {
                evenUnits += num
            }
        }

        val innerEvenCount = farm.countNodesAtDistance(130, 65, 65)
        val innerOddCount = farm.countNodesAtDistance(129, 65, 65)
        var sum = innerEvenCount*oddUnits + innerOddCount*evenUnits

        // TODO: write better comments to explain this...
        val tripleFarmRow = farm.toTripleFarmRow()
        sum += (N - 1) * tripleFarmRow.countNodesAtDistance(130 + 65, 0, 0) // south-east
        sum += (N - 1) * tripleFarmRow.countNodesAtDistance(130 + 65, 0, 131 * 3 - 1) // south-west
        sum += (N - 1) * tripleFarmRow.countNodesAtDistance(130 + 65, 130, 0) // north-east
        sum += (N - 1) * tripleFarmRow.countNodesAtDistance(130 + 65, 130, 131 * 3 - 1) // north-west

        sum += tripleFarmRow.countNodesAtDistance(130, 0, 131 + 65) // Southernmost box
        sum += tripleFarmRow.countNodesAtDistance(130, 130, 131 + 65) // Northernmost box

        sum += farm.countNodesAtDistance(130, 65, 0) // Easternmost box
        sum += farm.countNodesAtDistance(130, 65, 130) // Westernmost box

        return sum.also { println(it) }
    }
}
