package aockt.y2024

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

    private class BitMask(val size: Int, val bitmask: Long = 0L) {
        operator fun get(pos: Int): Boolean =
            ((bitmask shr pos) and 1L) > 0L

        fun next(): BitMask = BitMask(size, bitmask + 1L)
        fun hasNext() = (bitmask + 1L) < (1L shl size)
    }

    private data class Equation(val terms: List<Long>, val testValue: Long) {
        fun test(operators: BitMask): Boolean {
            assert(operators.size == terms.size - 1)

            var result = terms.first()
            for ((i, term) in terms.drop(1).withIndex()) {
                when (operators[i]) {
                    false -> result += term
                    true -> result *= term
                }
            }

            return result == testValue
        }

        fun couldBeValid(): Boolean {
            var operators = BitMask(terms.size - 1)

            while (true) {
                if (test(operators)) { return true }

                if (operators.hasNext()) {
                    operators = operators.next()
                } else {
                    return false
                }
            }
        }
    }

    override fun partOne(input: String) : Long =
        input.lines().map { it.toEquation() }.filter { it.couldBeValid() }.sumOf { it.testValue }
            .also { println(it) }

//    override fun partTwo(input: String) = input.length
}
