package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2024D01 : Solution {
    private fun String.split() : List<String> = split("""\s+""".toRegex())

    private fun List<Int>.toPair() : Pair<Int, Int> =
        when (size) {
            2 -> this[0] to this[1]
            else -> throw IllegalArgumentException()
        }

    private fun List<Pair<Int, Int>>.transpose() : Pair<List<Int>, List<Int>> =
        map { it.first } to map {it.second}

    private fun List<Int>.toOccurrenceMap() =
        groupingBy { it }.eachCount()

    private fun parseInput(input: String): Pair<List<Int>, List<Int>> =
        input.lines().map { it.trim().split().map(String::toInt).toPair() }.transpose()

    override fun partOne(input: String) : Int {
        val (l1, l2) = parseInput(input)
        assert(l1.size == l2.size)
        return l1.sorted().zip(l2.sorted()).sumOf { (a, b) -> (a - b).absoluteValue }
            .also { println(it) }
    }

    override fun partTwo(input: String) : Int {
        val (l1, l2) = parseInput(input)
        val occurrenceMap = l2.toOccurrenceMap()
        return l1.sumOf { it * occurrenceMap.getOrDefault(it, 0) }
            .also { println(it) }
    }
}
