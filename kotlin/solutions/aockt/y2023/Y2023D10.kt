package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D10 : Solution {
    data class Point(val r: Int, val c: Int) {
        fun toModified() = ModifiedPoint(r * 2, c * 2)
    }

    data class ModifiedPoint(val r: Int, val c: Int) {
        val isReal: Boolean
            get() = r%2 == 0 && c %2 == 0

        fun getBetween(other: ModifiedPoint): ModifiedPoint {
            assert(isReal)
            assert(other.isReal)
            return ModifiedPoint((r + other.r) / 2, (c + other.c) / 2)
        }
    }

    data class Node(val point: Point, val edges: List<Point>)

    data class Maze(val nodes: Map<Point, Node>, val start: Point, val height: Int, val width: Int) {
        fun getCycle(): List<Point> {
            val visited: MutableSet<Point> = mutableSetOf()
            data class WorkItem(val current: Point, val last: Point, val steps: Int, val path: List<Point>)
            val stack = ArrayDeque<WorkItem>()
            stack += WorkItem(start, start, 0, listOf(start))

            while (stack.isNotEmpty()) {
                val (current, last, steps, path) = stack.removeLast()
                if (current == start && last != start) {
                    return path
                }
                if (current in visited) continue
                visited += current
                nodes.getValue(current).edges.filterNot { it == last }.forEach { next ->
                    stack += WorkItem(next, current, steps + 1, path + next)
                }
            }

            return listOf()
        }

        // The modified maze is scaled by a factor 2, to allow for flood fill that takes "squeezing between pipes" into
        // account.
        fun toModifiedMaze(path: List<Point>): ModifiedMaze {
            val grid = (0 until (height*2-1)).map {
                (0 until (width*2-1)).map { false }.toMutableList()
            }.toMutableList()

            path.map(Point::toModified).zipWithNext().forEach { (p1, p2) ->
                val mid = p1.getBetween(p2)
                grid[p1.r][p1.c] = true
                grid[p2.r][p2.c] = true
                // Set the new in-between cell (due to 2x scaling) to true, since it is also on the cycle path.
                grid[mid.r][mid.c] = true
            }

            return ModifiedMaze(grid)
        }
    }

    // The modified maze is scaled by a factor 2, to allow for flood fill that takes "squeezing between pipes" into
    // account. The data structure is just a bool grid, where a cell is `true` iff it is part of the cycle.
    data class ModifiedMaze(val grid: List<List<Boolean>>) {
        private val height = grid.size
        private val width = grid[0].size

        private fun ModifiedPoint.getNeighbours(): List<ModifiedPoint> =
            buildList {
                // North
                if (r > 0) add(ModifiedPoint(r - 1, c))
                // South
                if (r < height - 1) add(ModifiedPoint(r + 1, c))
                // West
                if (c > 0) add(ModifiedPoint(r, c - 1))
                // East
                if (c < width - 1) add(ModifiedPoint(r, c + 1))
            }

        fun countOutside(): Int {
            val startPoints: List<ModifiedPoint> =
                (
                    (0 until height).map { ModifiedPoint(it, 0) }.toList() +
                    (0 until height).map { ModifiedPoint(it, width - 1) }.toList() +
                    (0 until width).map { ModifiedPoint(0, it) }.toList() +
                    (0 until width).map { ModifiedPoint(height - 1, it) }.toList()
                ).filter { it.isReal && !grid[it.r][it.c] }
            val visited: MutableSet<ModifiedPoint> = mutableSetOf()
            val queue = ArrayDeque<ModifiedPoint>()
            queue.addAll(startPoints)

            for (r in 0 until height) {
                for (c in 0 until width) {
                    if (grid[r][c]) visited += ModifiedPoint(r, c)
                }
            }

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current in visited) continue
                visited += current
                if (grid[current.r][current.c]) continue
                queue.addAll(current.getNeighbours())
            }

            return visited.count(ModifiedPoint::isReal)
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
        input.toMaze().getCycle().size / 2

    override fun partTwo(input: String): Int {
        val maze = input.toMaze()
        val path = maze.getCycle()
        val modifiedMaze = maze.toModifiedMaze(path)
        return (maze.height * maze.width - modifiedMaze.countOutside()).also { println(it) }
    }
}
