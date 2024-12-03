package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2024D01 : Solution {

    override fun partOne(input: String) : Long {
        val lines = input.lines().map { it.trim().split("\\s+".toRegex()).map { it.toLong() } }
        val l1 = lines.map { it[0] }.sorted()
        val l2 = lines.map { it[1] }.sorted()
        return l1.zip(l2).map { (a, b) -> (a-b).absoluteValue }.sum().also { println(it) }
    }


    override fun partTwo(input: String) : Long {
        val lines = input.lines().map { it.trim().split("\\s+".toRegex()).map { it.toLong() } }
        val l1 = lines.map { it[0] }.sorted()
        val l2 = lines.map { it[1] }.sorted()
        return computeSimilarityScore(l1, l2.toOccurrenceMap()).also { println(it) }
    }

    private fun List<Long>.toOccurrenceMap() : Map<Long, Long> {
        val m : MutableMap<Long, Long> = mutableMapOf()
        for (e in this) {
            m.compute(e) {k, v -> if (v == null) 1 else (v+1)}
        }
        return m
    }

    private fun computeSimilarityScore(l : List<Long>, m : Map<Long, Long>) =
        l.map { it * m.getOrDefault(it, 0) }.sum()
}
