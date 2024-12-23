package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D23 : Solution {
    private fun parseInput(input: String): List<Pair<String, String>> =
        input.lines().map { it.split('-') }.map { it[0] to it[1] }

    private fun getMapToIds(stringIds: List<String>): Map<String, Int> {
        val map: MutableMap<String, Int> = mutableMapOf()
        stringIds.distinct().sorted().withIndex().forEach { (i, str) -> map[str] = i }
        return map
    }

    val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()
    private fun choose(n: Int, k: Int): Long = cache.getOrPut(n to k) {
            if (n == k || k == 0) {
                1
            } else {
                choose(n-1, k-1) + choose(n - 1, k)
            }
        }

    private fun solve(input: String): Int {
        val edges = parseInput(input).toSet()
        val nodes = edges.flatMap { listOf(it.first, it.second) }.distinct()
        val cliques: MutableSet<List<String>> = mutableSetOf()

        println("nodes: ${nodes.size}, edges: ${edges.size}")

        for ((u, v) in edges.filter { it.first[0] == 't' || it.second[0] == 't' }) {
            for (w in nodes.filterNot { it == u || it == v } ) {
                if (
                    (u to w in edges || w to u in edges) &&
                    ((v to w in edges || w to v in edges))
                ) {
                    cliques.add(listOf(u, v, w).sorted())
                }
            }
        }

        return cliques.size.also { println(it) }
    }

    override fun partOne(input: String) = solve(input)

//    override fun partTwo(input: String) = input.length
}
