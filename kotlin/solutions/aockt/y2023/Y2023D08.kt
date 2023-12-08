package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.lang.IllegalArgumentException

private data class Node(val label: String, val edges: List<String>)

object Y2023D08 : Solution {

    private fun solve(input: String) : Int {
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

        var currentNode = "AAA"
        var numSteps = 0
        return generateSequence("AAA") {
            val next = nodeMap.getValue(it).edges[path[numSteps % path.size]]
            numSteps += 1
            if (next == "ZZZ") null else next
        }.count()
    }

    override fun partOne(input: String) = solve(input)

//    override fun partTwo(input: String) = solve(input)
}
