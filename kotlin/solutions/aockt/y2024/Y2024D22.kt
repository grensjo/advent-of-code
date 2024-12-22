package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D22 : Solution {
    private fun Long.mix(n: Long) = this xor n
    private fun Long.prune() = this and ((1L shl 24) - 1L)
    private fun Long.step1() = this.mix(this shl 6).prune()
    private fun Long.step2() = this.mix(this shr 5).prune()
    private fun Long.step3() = this.mix(this shl 11).prune()
    private fun Long.next() = this.step1().step2().step3()
    private fun Long.getBananas() = toString().last().digitToInt()

    override fun partOne(input: String): Long {
        val initialNumbers = input.lines().map(String::toLong)

        var sum = 0L
        for (initial in initialNumbers) {
            var current = initial
            for (i in 1..2000) {
                current = current.next()
            }
            sum += current
        }

        return sum.also { println(it) }
    }

    override fun partTwo(input: String): Int {
        val initialNumbers = input.lines().map(String::toLong)
        val sequenceToBananas: MutableMap<List<Int>, Int> = mutableMapOf()

        for (initial in initialNumbers) {
            val currentSequence: MutableList<Int> = mutableListOf()
            val seenSequences: MutableSet<List<Int>> = mutableSetOf()
            var current = initial
            var prevBananas = initial.getBananas()
            for (i in 1..2000) {
                current = current.next()
                val currentBananas = current.getBananas()
                currentSequence.addLast(currentBananas - prevBananas)
                if (currentSequence.size > 4) { currentSequence.removeFirst() }
                if (currentSequence.size == 4) {
                    if (currentSequence !in seenSequences) {
                        seenSequences.add(currentSequence.toList())
                        sequenceToBananas.merge(currentSequence.toList(), currentBananas, Int::plus)
                    }
                }
                prevBananas = currentBananas
            }
        }

        return sequenceToBananas.values.max().also { println(it) }
    }
}
