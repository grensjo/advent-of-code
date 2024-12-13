package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D13 : Solution {
    data class Vector(val x: Long, val y: Long) {
        operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
        operator fun times(k: Long) = Vector(k * x, k * y)
    }
    operator fun Long.times(v: Vector) = v * this

    data class ClawMachine(val buttonA: Vector, val buttonB: Vector, val prize: Vector) {
        fun solve(): Long? {
            var cheapest: Long? = null

            for (i in 0L..<100L) {
                for (j in 0L..<100L) {
                    if (buttonA * i + buttonB * j == prize) {
                        val cost = 3 * i + j
                        if (cheapest == null || cost < cheapest) {
                            cheapest = cost
                        }
                    }
                }
            }

            return cheapest
        }
    }

    private val regex = """X.(\d+), Y.(\d+)""".toRegex()
    private fun parseMachine(machineLines: List<String>): ClawMachine {
        val buttonA: Vector = regex.find(machineLines[0])!!.groups.let {
            Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong())
        }
        val buttonB: Vector = regex.find(machineLines[1])!!.groups.let {
            Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong())
        }
        val prize: Vector = regex.find(machineLines[2])!!.groups.let {
            Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong())
        }
        return ClawMachine(buttonA, buttonB, prize)
    }

    private fun parseInput(input: String): Long {
        val machines = input.split("\n\n").map { parseMachine(it.lines()) }
        return machines.sumOf { it.solve() ?: 0 }
            .also { println(it) }
    }

    override fun partOne(input: String) = parseInput(input)

//    override fun partTwo(input: String) = input.length
}
