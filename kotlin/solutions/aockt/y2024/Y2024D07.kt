package aockt.y2024

import aockt.y2024.Y2024D07.Operator.*
import aockt.y2024.Y2024D07.toEquation
import io.github.jadarma.aockt.core.Solution

object Y2024D07 : Solution {
    private fun String.split() : List<String> = split("""\s+""".toRegex())
    private fun String.toEquation(): Equation {
        val (testValueStr, termsStr) = split(":")
        return Equation(
            termsStr.trim().split().map(String::toLong),
            testValueStr.trim().toLong()
        )
    }

    enum class Operator {
        PLUS,
        TIMES,
        CONCAT,
    }
    private val partOneOperators = listOf(PLUS, TIMES)
    private val partTwoOperators = Operator.entries

    private data class Equation(val terms: List<Long>, val testValue: Long) {
        fun computeRecursive(partialResult: Long, nextIndex: Int, nextOperator: Operator, operators: List<Operator>): Boolean {
            val newPartialResult = when (nextOperator) {
                PLUS -> partialResult + terms[nextIndex]
                TIMES -> partialResult * terms[nextIndex]
                CONCAT -> (partialResult.toString() + terms[nextIndex].toString()).toLong()
            }

            if (nextIndex == terms.size - 1) {
                // We have computed the entire expression, let's see if we were successful!
                return (newPartialResult == testValue)
            }

            for (op in operators) {
                if (computeRecursive(newPartialResult, nextIndex + 1, op, operators)) {
                    return true
                }
            }

            // None of the recursive calls resulted in a valid result.
            return false
        }

        fun couldBeValid(operators: List<Operator>): Boolean =
            operators.any { it -> computeRecursive(terms.first(), 1, it, operators) }

    }

    private fun solve(input: String, operators: List<Operator>): Long =
        input.lines().map { it.toEquation() }.filter { it.couldBeValid(operators) }.sumOf { it.testValue }
            .also { println(it) }

    override fun partOne(input: String) : Long =
        solve(input, partOneOperators)

    override fun partTwo(input: String) : Long =
        solve(input, partTwoOperators)
}
