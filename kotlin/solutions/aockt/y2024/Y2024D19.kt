package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import kotlin.math.min

object Y2024D19 : Solution {

    private fun parseInput(input: String): Pair<Set<String>, List<String>> {
        val lines = input.lines()
        val towels = lines[0].split(", ").toSet()
        val designs = lines.drop(2)
        return towels to designs
    }

    private fun numberOfWaysToCompleteDesign(towels: Set<String>, design: String): Long {
        // dp[i] = # of ways to lay out the first i characters of the design.
        val dp: MutableList<Long> = MutableList(design.length + 1, { 0 })
        dp[0] = 1
        val maxTowelLength = towels.maxOf { it.length }

        for (i in design.indices) {
            if (dp[i] == 0L) { continue }

            for (j in 1..min(maxTowelLength, design.length - i)) {
                val nextChars = design.slice(i..<(i+j))
                if (nextChars in towels) {
                    dp[i + j] += dp[i]
                }
            }
        }

        return dp[design.length]
    }

    override fun partOne(input: String): Int {
        val (towels, designs) = parseInput(input)
        return designs.count { numberOfWaysToCompleteDesign(towels, it) > 0 }.also { println(it) }
    }

    override fun partTwo(input: String): Long {
        val (towels, designs) = parseInput(input)
        return designs.sumOf { numberOfWaysToCompleteDesign(towels, it) }.also { println(it) }
    }

}
