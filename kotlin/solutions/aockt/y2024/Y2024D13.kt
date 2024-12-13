package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D13 : Solution {
    data class Vector(val x: Long, val y: Long) {
        operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
        operator fun times(k: Long) = Vector(k * x, k * y)
    }
    operator fun Long.times(v: Vector) = v * this

    data class ClawMachine(val buttonA: Vector, val buttonB: Vector, val prize: Vector) {
        val det: Long = buttonA.x * buttonB.y - buttonA.y * buttonB.x
        val detA: Long = prize.x * buttonB.y - prize.y * buttonB.x
        val detB: Long = buttonA.x * prize.y - buttonA.y * prize.x

        /**
         * We can formulate the problem as a system of 2 linear equations with 2 unknowns.
         * Let a be the number of A presses and b the number of B presses.
         *
         * buttonA.x * a + buttonB.x * b = prize.x
         * buttonA.y * a + buttonB.y * b = prize.y
         *
         * Solve with respect to a and b.
         */
        fun solve(): Long? {
            if (det == 0L) {
                // If the determinant is 0 there may be either none or infinitely many
                // real solutions. However, in the input this never happens.
                throw IllegalArgumentException()
            }

            // Since the determinant was non-zero there exists exactly one real solution.
            // Let's check if it's an integer solution.
            if (detA % det == 0L && detB % det == 0L) {
                // Exactly one integer solution by Cramer's rule.
                val a = detA / det
                val b = detB / det

                // Compute the cost of the solution.
                return 3*a + b
            } else {
                // No integer solution.
                return null
            }
        }
    }

    private val regex = """X.(\d+), Y.(\d+)""".toRegex()
    private fun parseMachine(machineLines: List<String>, isPart2: Boolean = false): ClawMachine {
        val buttonA: Vector = regex.find(machineLines[0])!!.groups.let {
            Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong())
        }
        val buttonB: Vector = regex.find(machineLines[1])!!.groups.let {
            Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong())
        }
        val prize: Vector = regex.find(machineLines[2])!!.groups.let {
            val prizeX = (if (isPart2) 10000000000000L else 0) + it[1]!!.value.toLong()
            val prizeY = (if (isPart2) 10000000000000L else 0) + it[2]!!.value.toLong()
            Vector(prizeX, prizeY)
        }
        return ClawMachine(buttonA, buttonB, prize)
    }

    private fun parseInput(input: String, isPart2: Boolean = false): List<ClawMachine> =
        input.split("\n\n").map { parseMachine(it.lines(), isPart2) }

    override fun partOne(input: String) =
        parseInput(input).sumOf { it.solve() ?: 0 }
            .also { println(it) }

    override fun partTwo(input: String) =
        parseInput(input, isPart2 = true).sumOf { it.solve() ?: 0 }
            .also { println(it) }
}
