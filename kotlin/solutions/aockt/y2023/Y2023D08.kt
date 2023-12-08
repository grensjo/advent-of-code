package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException
import java.math.BigInteger

private data class Node(val label: String, val edges: List<String>)

object Y2023D08 : Solution {

    private fun parseInput(input: String): Pair<List<Int>, Map<String, Node>> {
        val lines = input.lineSequence().filterNot { it.isBlank() }.toList()
        val path = lines[0].trim().map {
            when (it) {
                'L' -> 0
                'R' -> 1
                else -> throw IllegalArgumentException()
            }
        }
        val nodeMap: Map<String, Node> = lines
            .drop(1)
            .map {
                it.split(' ', '=', '(', ',', ')').filterNot { it.isBlank() }
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

    private fun getNumStepsToEndNode(
        path: List<Int>,
        nodeMap: Map<String, Node>,
        startNode: String,
        isEndNode: (String) -> Boolean
    ): Long {
        var numSteps = 0
        return generateSequence(startNode) { currentNode ->
            val next = nodeMap.getValue(currentNode).edges[path[numSteps % path.size]]
            numSteps += 1
            if (isEndNode(next)) null else next
        }.count().toLong()
    }

    override fun partOne(input: String): Long {
        val (path, nodeMap) = parseInput(input)
        return getNumStepsToEndNode(path, nodeMap, startNode = "AAA", isEndNode = { it == "ZZZ" })
    }

    override fun partTwo(input: String): Long {
        val (path, nodeMap) = parseInput(input)
        val ans = nodeMap.keys.filter { it[2] == 'A' }.map { startNode ->
            getNumStepsToEndNode(path, nodeMap, startNode, isEndNode = { it[2] == 'Z' })
        }.reduce(Long::lcm)
        return ans
    }
}

private fun BigInteger.lcm(b: BigInteger) =
    (this.abs() / this.gcd(b)) * b.abs()

private fun Long.lcm(b: Long) = BigInteger.valueOf(this).lcm(BigInteger.valueOf(b)).toLong()
