package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D10 : Solution {
    data class Point(val r: Int, val c: Int)
    data class Node(val point: Point, val edges: List<Point>)
    data class Maze(val nodes: Map<Point, Node>, val start: Point, val height: Int, val width: Int) {
        fun getFurthestDistance(): Int {
            val visited: MutableSet<Point> = mutableSetOf()
            data class WorkItem(val current: Point, val last: Point, val steps: Int)
            val stack = ArrayDeque<WorkItem>()
            stack += WorkItem(start, start, 0)

            while (stack.isNotEmpty()) {
                val (current, last, steps) = stack.removeLast()
                if (current == start && last != start) {
                    return (steps + 1) / 2
                }
                if (current in visited) continue
                visited += current
                nodes.getValue(current).edges.filterNot { it == last }.forEach { next ->
                    stack += WorkItem(next, current, steps + 1)
                }
            }

            return -1
        }
    }

    private val NORTH_LIST = listOf('S', '|', 'L', 'J')
    private val SOUTH_LIST = listOf('S', '|', '7', 'F')
    private val WEST_LIST = listOf('S', '-', 'J', '7')
    private val EAST_LIST = listOf('S', '-', 'L', 'F')

    private fun String.toMaze(): Maze {
        val grid: List<String> = lineSequence().map(String::trim).filterNot(String::isBlank).toList()
        val height = grid.size
        val width = grid[0].length
        val nodes: MutableMap<Point, Node> = mutableMapOf()
        var start: Point? = null
        println("height: $height, width: $width")

        for (r in 0 until height) {
            for (c in 0 until width) {
                val edges = buildList {
                    if (r > 0) {
                        // North
                        if (grid[r][c] in NORTH_LIST && grid[r-1][c] in SOUTH_LIST) {
                            add(Point(r - 1, c))
                        }
                    }
                    if (r < height - 1) {
                        // South
                        if (grid[r][c] in SOUTH_LIST && grid[r+1][c] in NORTH_LIST) {
                            add(Point(r + 1, c))
                        }
                    }
                    if (c > 0) {
                        // West
                        if (grid[r][c] in WEST_LIST && grid[r][c-1] in EAST_LIST) {
                            add(Point(r, c - 1))
                        }
                    }
                    if (c < width - 1) {
                        // East
                        if (grid[r][c] in EAST_LIST && grid[r][c+1] in WEST_LIST) {
                            add(Point(r, c + 1))
                        }
                    }
                }
                val point = Point(r, c)
                if (grid[r][c] == 'S') start = point
                nodes[point] = Node(point, edges)
            }
        }
        return Maze(nodes, start!!, height, width)
    }

    override fun partOne(input: String): Int =
        input.toMaze().getFurthestDistance()

    override fun partTwo(input: String): Int =
        input.lineSequence().count()
}