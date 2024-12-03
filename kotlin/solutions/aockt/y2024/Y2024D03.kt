package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D03 : Solution {
    val valid_regex = """mul\(\d{1,3},\d{1,3}\)""".toRegex()

    private fun String.solve() =
        valid_regex.findAll(this).toList().map { it.value }.sumOf { it.mul() }

    private fun String.mul(): Long {
       val (a_raw, b_raw) = this.split(",")
        assert(this.startsWith("mul("))
        assert(this.endsWith(")"))
        val a = a_raw.substringAfter("(").toLong()
        val b = b_raw.substringBefore(")").toLong()
        return a*b
    }

    override fun partOne(input: String) = input.solve().also { println(it) }

//    override fun partTwo(input: String) = input.length
}
