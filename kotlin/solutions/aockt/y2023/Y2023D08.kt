package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import java.math.BigInteger.valueOf

private data class Node(val label: String, val edges: List<String>)

object Y2023D08 : Solution {
    private fun parseInput(input: String): Pair<List<Int>, Map<String, Node>> {
        val lines = input.lineSequence().filterNot { it.isBlank() }.toList()
        val path = lines[0].trim().map {
            when(it) {
                'L' -> 0
                'R' -> 1
                else -> throw IllegalArgumentException()
            }
        }
        val nodeMap: Map<String, Node> = lines
            .drop(1)
            .map {
                it.split(' ', '=', '(', ',', ')').filterNot { it.isBlank()}
            }
            .map {
                Node(it[0], listOf(it[1], it[2]))
            }
            .groupBy { it.label }
            .mapValues { (label, nodeList) ->
                if (nodeList.size != 1) throw IllegalArgumentException()
                nodeList[0]
            }
        return path to nodeMap
    }

    private fun solve(path: List<Int>, nodeMap: Map<String, Node>, isGhost: Boolean) : Int {
        val startNodes = if (isGhost) {
            nodeMap.keys.filter { it[2] ==  'A' }.toHashSet()
        } else {
            setOf("AAA")
        }
        var numSteps = 0
        return generateSequence(startNodes) {currentNodes ->
            val next = currentNodes.map {
                nodeMap.getValue(it).edges[path[numSteps % path.size]]
            }.toHashSet()
            numSteps += 1
            if (isGhost) {
                if (next.all { it[2] == 'Z' }) null else next
            } else {
                if (next == hashSetOf("ZZZ")) null else next
            }
        }.count()
    }

    private data class State(val node: String, val pathIdx: Int)

    // Using the assumption that there is only one Z-node reachable from each A-node.
    private fun getCandidateSteps(startNode: String, path: List<Int>, nodeMap: Map<String, Node>): Pair<BigInteger, BigInteger> {
        var numSteps = 0L
        // var idx: Int = 0
        var distanceToZ: Long? = null
        var cycleLength: Long? = null
        val visited: MutableMap<State, Long> = mutableMapOf()

        generateSequence(State(startNode, 0)) {
            visited[it] = numSteps
            val nextState = State(
                nodeMap.getValue(it.node).edges[path[it.pathIdx]],
                if (it.pathIdx + 1 < path.size) it.pathIdx + 1 else 0
            )
            numSteps += 1

            if (nextState in visited) {
                cycleLength = numSteps - visited.getValue(nextState)
                null
            } else if (nextState.node[2] == 'Z') {
                // assert(distanceToZ == null)
                distanceToZ = numSteps
                nextState
            } else {
                nextState
            }
        }.last()

        return valueOf(distanceToZ!!) to valueOf(cycleLength!!)
    }

    override fun partOne(input: String): Int {
        val (path, nodeMap) = parseInput(input)
        val startNode = "AAA"
        var numSteps = 0
        return generateSequence(startNode) {currentNode ->
            val next = nodeMap.getValue(currentNode).edges[path[numSteps % path.size]]
            numSteps += 1
            if (next == "ZZZ") null else next
        }.count()
    }

    override fun partTwo(input: String): BigInteger {
        val (path, nodeMap) = parseInput(input)
        println(path)
        val startNodes = nodeMap.keys.filter { it[2] ==  'A' }.toHashSet()
        println("startNodes: $startNodes")
        val ans = startNodes.map {
            getCandidateSteps(it, path, nodeMap).first
        }.reduce(BigInteger::lcm)
        print(ans)
        return ans

        // var steps0 = ZERO
        // var cycle0 = ONE
        // // println("steps: $steps0, cylce: $cycle0")
        // for ((steps1, cycle1) in ans) {
        //     val a = cycle0
        //     val b = -cycle1
        //     val c = steps1 - steps0
        //     // Now solve ax + by = c
        //     val result = euclid(a, b)
        //     val d = result.gcd
        //     assert(c % d == ZERO)
        //     val s = c / d
        //     val x0 = s * result.x
        //     val y0 = s * result.y
        //     assert(cycle0 * x0 + steps0 == cycle1 * y0 + steps1)
        //
        //
        //     steps0 = cycle0 * x0 + steps0
        //     cycle0 = cycle0 * b / d
        //     // println("steps: $steps0, cycle: $cycle0")
        // }
        // return steps0
    }
}

private fun BigInteger.lcm(b: BigInteger) =
    (this*b).abs() / this.gcd(b)

// private fun euclid(a: BigInteger, b: BigInteger, x: BigInteger, y: BigInteger): EuclidResult {
//     if (b == BigInteger.ZERO) return EuclidResult(gcd = a, x = valueOf(1), y = valueOf(0))
//     val result = euclid(b, a % b, y, x)
//     return EuclidResult(result.gcd, result.x, result.y - a / b * result.x)
// }

data class EuclidResult(val gcd: BigInteger, val x: BigInteger, val y: BigInteger)
private fun euclid(a: BigInteger, b: BigInteger) : EuclidResult {
    if (a == ZERO) return EuclidResult(gcd = b, x = ZERO, y = ONE)
    val r = euclid(b % a, a)
    return EuclidResult(gcd = r.gcd, x = r.y - (b / a) * r.x, y = r.x)
}

//private fun gcd(a: Long, b: Long): Long {
//    return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).toLong()
//}
