package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException
import java.math.BigInteger

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

    private fun getCandidateSteps(startNode: String, path: List<Int>, nodeMap: Map<String, Node>): Long {
        var numSteps = 0L
        var idx: Int = 0
        val candidates: MutableList<Long> = mutableListOf()
        val visited: MutableSet<Pair<String, Int>> = mutableSetOf()

        generateSequence(startNode) {currentNode ->
            visited.add(currentNode to idx)
            val next = nodeMap.getValue(currentNode).edges[path[idx]]
            numSteps += 1
            idx += 1
            if (idx == path.size) idx = 0
            if ((next to idx) in visited) {
                null
            } else if (next[2] == 'Z') {
                candidates.add(numSteps)
                next
            } else {
                next
            }
        }.last()

        println(candidates)
//        assert(candidates.size == 1)
        return candidates[0]
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
            getCandidateSteps(it, path, nodeMap)
        }.map(BigInteger::valueOf).reduce(BigInteger::times)
        println(ans)
        return ans
//        var divisor = 0L
//        for (cycle in cycles) {
//            if (divisor == 0L) {
//                divisor = cycle
//            } else {
//                divisor = gcd(divisor, cycle)
//            }
//        }
//        return cycles.reduce(Long::times)
    }
}

//private fun gcd(a: Long, b: Long): Long {
//    return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).toLong()
//}
