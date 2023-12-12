package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D12 : Solution {

    data class TestCase(val damagedRecord: String, val groups: List<Int>) {
        val numUnknown: Int
            get() = damagedRecord.count { it ==  '?' }
        val maxMask: Int
            get() = (1 shl numUnknown)
        val isValid: Boolean
            get() {
//                println("damagedRecord: $damagedRecord")
                if ( '?' in damagedRecord ) return false //.also { println("FALSE, still ? in $this") }
                val actualGroups = damagedRecord.split('.').filterNot(String::isBlank)
                if (actualGroups.any { !it.all { ch -> ch == '#' } }) return false //.also { println("FALSE, not only # in $actualGroups") }
                return actualGroups.map { it.length } == groups
            }

        fun applyMask(mask: Int): TestCase {
            var currentMask = mask
            return TestCase(
                damagedRecord.map {
                    if (it == '?') {
                        var res = '.'
                        if ((currentMask and 1) == 1) {
                            res = '#'
                        }
                        currentMask = currentMask shr 1
                        res
                    } else {
                        it
                    }
                }.joinToString(""), groups)
        }
    }

    fun maskToList(mask: Int): List<Boolean> {
        var current = mask
        return (0 until 20).map {
            val res = ((it and current) == 1)
            current = current shr 1
            res
        }
    }

    fun solve(testCase: TestCase): Int =
        (0 until testCase.maxMask).count { testCase.applyMask(it).isValid }

    private fun String.toTestCase(): TestCase=
        split(' ').let {
            TestCase(it[0].trim(), it[1].trim().split(',').map { l -> l.trim().toInt() })
        }

    override fun partOne(input: String) = input.lineSequence().sumOf { solve(it.toTestCase()) }.also { println("$it") }
    override fun partTwo(input: String) = input.length
}