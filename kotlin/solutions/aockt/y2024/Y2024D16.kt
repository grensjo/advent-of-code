package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import java.util.Deque
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

private data class StepInfo(val state: ReindeerState, val costToHere: Long) {
    val previousState: MutableSet<ReindeerState> = mutableSetOf()
}

object Y2024D16 : Solution {
    private fun parseInput(input: String): Grid {
        val l = input.lines()
        val h = l.size
        val w = l[0].length
        assert(l.all { it.length == w })
        return Grid(h, w, input.lines().map { line -> line.toList() })
    }

    private fun Grid.dijkstra(): Pair<Long, Map<ReindeerState, StepInfo>> {
        val priorityQueue: PriorityQueue<StepInfo> =
            PriorityQueue(Comparator.comparingLong { it.costToHere })
        val costs: MutableMap<ReindeerState, StepInfo> = mutableMapOf()
        priorityQueue.add(StepInfo(startState, 0))
        var bestCostToEnd = Long.MAX_VALUE

        while (priorityQueue.isNotEmpty()) {
            val currentStep = priorityQueue.poll()
            if (this[currentStep.state.pos] == '#') { continue }
            if (currentStep.state in costs) {
                if(currentStep.costToHere > costs[currentStep.state]!!.costToHere) {
                    continue
                } else if (currentStep.costToHere == costs[currentStep.state]!!.costToHere) {
                    costs[currentStep.state]!!.previousState.addAll(currentStep.previousState)
                } else {
                    throw AssertionError("got to too high cost")
                }
            } else {
                costs[currentStep.state] = currentStep
            }
            if (this[currentStep.state.pos] == 'E') {
                if (bestCostToEnd > currentStep.costToHere) {
                    bestCostToEnd = currentStep.costToHere
                }
                continue
            }

            for ((nextState, nextCost) in currentStep.state.neighboursToCost) {
                if (this[nextState.pos] == '#') { continue }
                if (nextState in costs) { continue }
                val nextStepInfo = StepInfo(nextState, currentStep.costToHere + nextCost)
                nextStepInfo.previousState.add(currentStep.state)
                priorityQueue.add(nextStepInfo)
            }
        }

        return bestCostToEnd to costs
    }

    override fun partOne(input: String): Long {
        val grid = parseInput(input)
        return grid.dijkstra().first.also { println(it) }
    }

   override fun partTwo(input: String): Int {
       val grid = parseInput(input)
       val (bestCost, stepInfos) = grid.dijkstra()
       val queue: ArrayDeque<ReindeerState> = ArrayDeque()
       queue.addAll(
           grid.getAllCoords().filter { grid[it] == 'E' }
               .flatMap { pos -> Direction.entries.map { dir -> ReindeerState(pos, dir) } }
               .filter { stepInfos[it]?.costToHere == bestCost })

       val statesOnBestPath: MutableSet<ReindeerState> = mutableSetOf()

       while (queue.isNotEmpty()) {
           val currentState = queue.removeFirst()
           if (currentState in statesOnBestPath) continue

           statesOnBestPath.add(currentState)
           queue.addAll(stepInfos[currentState]!!.previousState)
       }

       return statesOnBestPath.map { it.pos }.distinct().count().also { println(it) }
   }
}
