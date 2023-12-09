package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private data class NumberSequence(val list: List<Int>) {
    private fun getDiffSequence() =
        NumberSequence(list.zipWithNext().map { (a, b) -> b - a }).also { println(it) }

    fun predictNext() : Int = if (list.all { it == 0 }) 0 else list.last() + getDiffSequence().predictNext()
    fun predictPrevious() : Int = if (list.all { it == 0 }) 0 else list.first() - getDiffSequence().predictPrevious()
}

private fun String.toNumberSequence() = NumberSequence(trim().split(' ').map(String::toInt))

object Y2023D09 : Solution {

    override fun partOne(input: String): Int =
        input.lineSequence().map(String::toNumberSequence).onEach { println(it) }.map(NumberSequence::predictNext).onEach { println(it) }.sum().also { println(it) }

    override fun partTwo(input: String): Int =
        input.lineSequence().map(String::toNumberSequence).onEach { println(it) }.map(NumberSequence::predictPrevious).onEach { println(it) }.sum().also { println(it) }
}
