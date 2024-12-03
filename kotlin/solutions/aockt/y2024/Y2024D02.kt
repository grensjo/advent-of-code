package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2024D02 : Solution {
    private fun String.split() : List<String> = split("""\s+""".toRegex())

    private fun parseInput(input: String): List<List<Long>> =
        input.lines().map { line -> line.trim().split().map(String::toLong) }

    private fun List<Long>.isMonotone() : Boolean {
        val pairs = zipWithNext()
        return pairs.all { (a,b) -> a >= b } || pairs.all { (a,b) -> a <= b }
    }

    private fun List<Long>.isSafe() =
        isMonotone() &&
                zipWithNext().map { (a, b) -> (a - b).absoluteValue }.all { it in 1..3 }

    private fun List<Long>.isSafeWithDampener() =
        indices.toList().map { slice(0..<it) + slice((it+1)..<size) }.any { it.isSafe() }

    override fun partOne(input: String) =
        parseInput(input).count { it.isSafe() }
            .also {println(it)}

    override fun partTwo(input: String) =
        parseInput(input).count { it.isSafeWithDampener() }
            .also {println(it)}
}
