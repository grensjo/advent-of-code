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

    override fun partOne(input: String): Int {
        val stones: LinkedList<Long> = LinkedList(input.strip().split(" ").map(String::toLong))
        println(stones)

        for (i in 0..<25) {
            var len = stones.size
            var pos = 0

            val iterator = stones.listIterator()

            while (iterator.hasNext()) {
                val (newVal, newStone) = iterator.next().computeNext()
                iterator.set(newVal)
                if (newStone != null) {
                    iterator.add(newStone)
                }
            }
        }

        println(stones.size)
        return stones.size
    }

//    override fun partTwo(input: String) = input.length
}
