package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D23 : Solution {
    private data class Graph(val nodes: List<String>, val edges: Set<Pair<String, String>>) {
        fun hasEdge(u: String, v: String) = u to v in edges || v to u in edges
    }

    private fun parseInput(input: String): Graph {
        val edges = input.lines().map { it.split('-') }.map { it[0] to it[1] }.toSet()
        val nodes = edges.flatMap { listOf(it.first, it.second) }.distinct()
        return Graph(nodes, edges)
    }

    private fun extendCliques(g: Graph, oldCliques: Set<List<String>>): Set<List<String>> {
        val cliques: MutableSet<List<String>> = mutableSetOf()
        for (c1 in oldCliques) {
            for (node in g.nodes) {
                if (node in c1) { continue }
                if (c1.all { g.hasEdge(it, node) }) {
                    cliques.add(c1.plus(node).sorted())
                }
            }
        }
        return cliques
    }

    override fun partOne(input: String): Int {
        val g: Graph = parseInput(input)
        return extendCliques(
            g, g.edges.map { listOf(it.first, it.second) }.toSet())
            .size.also { println(it) }
    }

    override fun partTwo(input: String): String {
        val g = parseInput(input)
        var prevCliques = g.edges.map { listOf(it.first, it.second) }.toSet()

        for (n in 3..g.nodes.size) {
            val newCliques = extendCliques(g, prevCliques)
            prevCliques = newCliques

            println("Number of $n-cliques: ${newCliques.size}")

            if (prevCliques.size == 1) {
                break
            }
        }

        return prevCliques.first().joinToString(separator = ",").also { println(it) }
    }
}
