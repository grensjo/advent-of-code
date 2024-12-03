package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D03 : Solution {
    private val valid_regex = """mul\(\d{1,3},\d{1,3}\)|do\(\)|don't\(\)""".toRegex()

    sealed class Symbol
    data class Mul(val a: Long, val b: Long) : Symbol() {
        fun product() = a * b
    }
    data object Enable : Symbol()
    data object Disable : Symbol()

    private fun String.toSymbol(): Symbol =
        when {
            this.startsWith("mul") -> this.toMul()
            this == "do()" -> Enable
            this == "don't()" -> Disable
            else -> throw IllegalArgumentException()
        }

    private fun String.toMul(): Mul {
        val (aRaw, bRaw) = this.split(",")
        assert(this.startsWith("mul("))
        assert(this.endsWith(")"))
        val a = aRaw.substringAfter("(").toLong()
        val b = bRaw.substringBefore(")").toLong()
        return Mul(a, b)
    }

    private fun List<Symbol>.execute(withConditionals: Boolean) : Long {
        var enabled = true
        var sum: Long = 0

        for (symbol in this) {
            when (symbol) {
                Enable -> enabled = true
                Disable -> if (withConditionals) { enabled = false }
                is Mul -> if (enabled) { sum += symbol.product() }
            }
        }

        return sum
    }

    private fun String.solve(withConditionals: Boolean = false) =
        valid_regex.findAll(this).toList().map { it.value.toSymbol() }.execute(withConditionals)

    override fun partOne(input: String) = input.solve(withConditionals = false).also { println(it) }
    override fun partTwo(input: String) = input.solve(withConditionals = true).also { println(it) }
}
