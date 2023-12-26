package aockt.y2023

import aockt.y2023.Y2023D23.Direction.*
import io.github.jadarma.aockt.core.Solution

object Y2023D23 : Solution {

    class Graph(val nodes: Map<Int, Node>, val start: Int, val end: Int) {
        // Brute-forces the longest path by recursing for every direction choice.
        fun getLongestPath(): Int {
            val path: MutableList<Int> = mutableListOf()
            var longestDistance = 0

            fun recurse(current: Node, distance: Int) {
                if (current.id == end) {
                    if (distance > longestDistance) {
                        longestDistance = distance
                    }
                    return
                }

                path += current.id
                for (edge in current.edges) {
                    if (edge.node.id in path) continue
                    recurse(edge.node, distance + edge.weight)
                }
                path.removeLast()
            }

            recurse(nodes.getValue(start), 0)
            return longestDistance
        }
    }

    data class Node(val id: Int, val originalPoint: Point) {
        val edges: MutableList<Edge> = mutableListOf()
    }
    data class Edge(val node: Node, val weight: Int)

    enum class Direction(val delta: Point) {
        NORTH(Point(-1, 0)),
        WEST(Point(0, -1)),
        SOUTH(Point(1, 0)),
        EAST(Point(0, 1)),
    }

    data class Point(val r: Int, val c: Int) {
        operator fun plus(other: Point) = Point(r + other.r, c + other.c)
        operator fun minus(other: Point) = Point(r - other.r, c - other.c)
    }

    private operator fun List<List<Char>>.get(point: Point) = this[point.r][point.c]

    private fun Char.toDirection() = when (this) {
        '^' -> NORTH
        '>' -> EAST
        '<' -> WEST
        'v' -> SOUTH
        else -> null
    }

    data class Maze(val grid: List<List<Char>>) {
        private val numRows = grid.size
        private val numCols = grid[0].size

        private fun Point.isValid() =
            r >= 0 && c >= 0 && r < numRows && c < numCols && grid[this] != '#'

        private fun Point.isBranchingNode() : Boolean {
            if (!isValid()) return false
            if ((r == 0 && c == 1) || (r == numRows - 1 && c == numCols - 2)) return true
            if (getNeighbours().size > 2) return true
            return false
        }

        private fun Point.getNeighbours(constraint: Direction? = null) : List<Point> =
            Direction.entries
                .asSequence()
                .filter { if (constraint != null) it == constraint else true}
                .map { this + it.delta }
                .filter { it.isValid() }
                .toList()

        fun toCompressedGraph(ignoreSlopes: Boolean = false): Graph {
            var numNodes = 0
            val pointToNode: Map<Point, Node> = grid.asSequence().withIndex()
                .flatMap { (r, row) ->
                    row.asSequence().withIndex()
                        .map { (c, _) -> Point(r, c) }
                        .filter { it.isBranchingNode() }
                        .map { Node(numNodes++, it) }.toList()
                }.associateBy(Node::originalPoint)

            val turnedIntoEdge: MutableSet<Point> = mutableSetOf()

            for ((startPoint, startNode) in pointToNode) {
                for (neighbour in startPoint.getNeighbours(constraint = if (ignoreSlopes) null else grid[startPoint].toDirection())) {
                    if (neighbour in turnedIntoEdge) continue
                    val seen: MutableSet<Point> = mutableSetOf(startPoint)
                    var current = neighbour
                    var previous = startPoint
                    var distance = 1
                    var allowedForwards = true
                    var allowedBackwards = grid[startPoint].toDirection() == null

                    while (distance > 0) {
                        seen += current

                        if (current.isBranchingNode()) {
                            assert(grid[current] != '#')
                            val endNode = pointToNode.getValue(current)
                            if (ignoreSlopes || allowedForwards) startNode.edges.add(Edge(endNode, distance))
                            if (ignoreSlopes || allowedBackwards) endNode.edges.add(Edge(startNode, distance))
                            break
                        }

                        val neighbours = current.getNeighbours().filterNot { it in seen }
                        if (neighbours.isEmpty()) {
                            distance = -1
                            break
                        }
                        if (neighbours.size > 1) throw AssertionError("This should not be a branching node")
                        val next = neighbours[0]

                        val direction = grid[current].toDirection()
                        if (direction != null) {
                            if ((next - current) != direction.delta) allowedForwards = false
                            if ((previous - current) != direction.delta) allowedBackwards = false
                        }

                        turnedIntoEdge += current
                        previous = current
                        current = next
                        distance++
                    }

                    if (distance == -1) continue
                }
            }

            return Graph(
                pointToNode.values.associateBy { it.id },
                start = pointToNode.getValue(Point(0, 1)).id,
                end = pointToNode.getValue(Point(numRows - 1, numCols - 2)).id,
                )
        }
    }

    override fun partOne(input: String) : Int {
        return input.toMaze().toCompressedGraph().getLongestPath().also { println(it) }
    }

    override fun partTwo(input: String) : Int {
        return input.toMaze().toCompressedGraph(ignoreSlopes = true).getLongestPath().also { println(it) }
    }

    private fun String.toMaze() = Maze(lineSequence().map { it.toList() }.toList())
}
