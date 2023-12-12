package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D12 : Solution {

    data class TestCase(val str: String, val groups: List<Int>) {
        val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()
        /**
         * Returns the number of ways we can place the i:th and following groups of damaged springs,
         * with the i:th group starting on position pos.
         */
        fun dp(i: Int, pos: Int): Long {
            if (i to pos in cache.keys) {
                return cache.getValue(i to pos)
            }

            // We cannot start a group here if the position before was also damaged. We should never
            // get here though.
            assert(pos == 0 || str[pos - 1] != '#')

            val groupSize = groups[i]
            if (pos + groupSize > str.length) return 0
            // Check that none of the groupSize following positions is known to be undamaged
            if (str.slice(pos until (pos + groupSize)).any { it == '.' }) return 0
            // The next position is known to be damaged, so the group cannot be this size.
            if (pos + groupSize < str.length && str[pos+groupSize] == '#') return 0

            if (i + 1 == groups.size) {
                return if (pos + groupSize >= str.length || str.indexOf('#', pos + groupSize) == -1) {
                    1
                } else {
                    // There are additional # after this group, so this assignment is not valid.
                    0
                }
            }

            var sum = 0L
            for (next in getNextStartOptions(pos + groupSize + 1)) {
                sum += dp(i + 1, next)
            }
            cache[i to pos] = sum
            return sum
        }

        fun getNumPossibilities(): Long {
            var sum = 0L
            for (next in getNextStartOptions(0)) {
                sum += dp(0, next)
            }
            return sum
        }

        private fun getNextStartOptions(pos: Int): List<Int> {
            val end = str.indexOf('#', pos).takeIf { it != -1 } ?: (str.length - 1)
            return str.slice(pos..end)
                .withIndex()
                .filter { it.value in listOf('?', '#') }
                .map { it.index + pos }
        }

        fun applyRepeat(repeats: Int) =
            TestCase(
                (0 until 5).joinToString("?") { str },
                (0 until 5).flatMap { groups },
            )
    }

    private fun String.toTestCase(): TestCase=
        split(' ').let {
            TestCase(it[0].trim(), it[1].trim().split(',').map { l -> l.trim().toInt() })
        }

    override fun partOne(input: String) =
            input.lineSequence()
                    .map {it.toTestCase()}
                    .sumOf(TestCase::getNumPossibilities)
                    .also{println(it)}

    override fun partTwo(input: String) =
            input.lineSequence()
                    .map {it.toTestCase().applyRepeat(5)}
                    .sumOf(TestCase::getNumPossibilities)
                    .also{println(it)}
}

