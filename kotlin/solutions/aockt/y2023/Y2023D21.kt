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

        fun countNodesAtDistance(distance: Int, startRow: Int? = null, startCol: Int? = null) : Int {
            var currentNodes: Set<Node> = mutableSetOf()
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

            println("distance: $distance, startRow: $startRow, startCol: $startCol, result: ${currentNodes.size}")
            return currentNodes.size
        }
    }

    override fun partOne(input: String) = input.toFarm().countNodesAtDistance(64).also { println(it) }

    override fun partTwo(input: String): Long {
        val farm = input.toFarm()

        var evenUnits = 1L
        var oddUnits = 0L

        val N = 202300L
        for (i in 1L until N) {
            val num = 4L*i
            if (i % 2L == 1L) {
                oddUnits += num
            } else {
                evenUnits += num
            }
        }
//        var evenUnitsCmp = 4 * (N / 2) * (N / 2 + 1) + 1
//        var oddUnitsCmp = 4 * (N+1)*(N+1)

//        println("evenUnits: $oddUnits, evenUnitsCmp: $evenUnits")
//        println("oddUnits: $evenUnits, oddUnitsCmp: $oddUnitsCmp")
//        println("tot: ${oddUnits+evenUnits}, calc: ${2*N*(N+1) + 1}")

        val innerEvenCount = farm.countNodesAtDistance(129, 65, 65)
        val innerOddCount = farm.countNodesAtDistance(130, 65, 65)
        var sum = innerEvenCount*oddUnits + innerOddCount*evenUnits
//        var sum = innerEvenCount*evenUnits + innerOddCount*oddUnits

        sum += farm.countNodesAtDistance(130, 65, 0)
        sum += farm.countNodesAtDistance(130, 65, 130)
        sum += farm.countNodesAtDistance(130, 0, 65)
        sum += farm.countNodesAtDistance(130, 130, 65)

        sum += (N - 1) * farm.countNodesAtDistance(130, 0, 0)
        sum += (N - 1) * farm.countNodesAtDistance(130, 0, 130)
        sum += (N - 1) * farm.countNodesAtDistance(130, 130, 130)
        sum += (N - 1) * farm.countNodesAtDistance(130, 130, 0)

        return sum.also { println(it) }
    }
}
