package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import java.util.LinkedList

object Y2024D11 : Solution {
    private fun Long.splitIntoTwo(): Pair<Long, Long> {
        val stoneStr = toString()
        val len = stoneStr.length
        return stoneStr.take(len/2).toLong() to stoneStr.takeLast(len/2).toLong()
    }

    private fun Long.computeNext(): Pair<Long, Long?> =
        when {
            (this == 0L) -> 1L to null
            (this.toString().length %2 == 0) -> splitIntoTwo()
            else -> this * 2024L to null
        }

    private fun solve(input:String, numBlinks: Int): Long {
        val stones: LinkedList<Long> = LinkedList(input.strip().split(" ").map(String::toLong))
        val cache: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()

        fun computeNumberOfStones(stone: Long, numBlinks: Int): Long {
            if (numBlinks == 0) { return 1 }
            if ((stone to numBlinks) in cache) {
                return cache[stone to numBlinks]!!
            }

            val (stone1, stone2) = stone.computeNext()
            var result = computeNumberOfStones(stone1, numBlinks - 1)
            if (stone2 != null) {
                result += computeNumberOfStones(stone2, numBlinks - 1)
            }

            cache[stone to numBlinks] = result
            return result
        }

        return stones.sumOf { computeNumberOfStones(it, numBlinks) }.also { println(it) }
    }

    override fun partOne(input: String): Long = solve(input, 25)
    override fun partTwo(input: String): Long = solve(input, 75)
}
