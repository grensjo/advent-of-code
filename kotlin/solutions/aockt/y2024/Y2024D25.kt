package aockt.y2024

import aockt.y2024.Y2024D25.Type.*
import io.github.jadarma.aockt.core.Solution

object Y2024D25 : Solution {
    private enum class Type { LOCK, KEY }
    private data class LockOrKey(val type: Type, val height: Int, val cols: List<Int>) {
        infix fun matches(other: LockOrKey): Boolean =
            this.cols.zip(other.cols).all { (a, b) -> a + b <= height }
    }

    private fun parseLockOrKey(lines: List<String>): LockOrKey {
        val type = when {
            lines.first().all { it == '#' } -> LOCK
            lines.last().all { it == '#' } -> KEY
            else -> throw IllegalArgumentException()
        }

        val filteredLines = when (type) {
            LOCK -> lines.drop(1)
            KEY -> lines.reversed().drop(1)
        }

        return LockOrKey(type, filteredLines.size - 1, buildList<Int> {
            for (i in 0..<filteredLines.first().length) {
                var num = filteredLines.size

                for (j in filteredLines.indices) {
                    if (filteredLines[j][i] != '#') {
                        num = j
                        break
                    }
                }

                add(num)
            }
        })
    }

    private fun parseInput(input: String): List<LockOrKey> =
        input.split("\n\n").map { parseLockOrKey(it.lines()) }


    override fun partOne(input: String): Int {
        val lockOrKeys = parseInput(input)
        val locks = lockOrKeys.filter { it.type == LOCK }
        val keys = lockOrKeys.filter { it.type == KEY }

        var matchingPairs = 0
        for (lock in locks) {
            for (key in keys) {
                if (key matches lock) {
                    matchingPairs++
                }
            }
        }
        return matchingPairs.also { println(it) }
    }
}
