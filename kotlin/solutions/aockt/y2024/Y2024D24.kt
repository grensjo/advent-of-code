package aockt.y2024

import aockt.y2024.Y2024D24.Operation.*
import io.github.jadarma.aockt.core.Solution
import kotlin.math.max
import kotlin.random.Random

object Y2024D24 : Solution {

    private enum class Operation {
        AND,
        OR,
        XOR,
    }

    private data class Gate(val inWire1: String, val op: Operation, val inWire2: String, var outWire: String) {
        fun getOutput(state: MutableMap<String, Boolean>, gates: Map<String, Gate>): Boolean {
            state[outWire]?.also { return it }

            val in1 = state[inWire1] ?: gates[inWire1]!!.getOutput(state, gates)
            val in2 = state[inWire2] ?: gates[inWire2]!!.getOutput(state, gates)

            val out = when (op) {
                AND -> in1 && in2
                OR -> in1 || in2
                XOR -> in1 xor in2
            }

            state[outWire] = out
            return out
        }
    }

    private fun parseState(input: String): Map<String, Boolean> = buildMap {
        for (line in input.lines()) {
            val tokens = line.split(" ")
            assert(tokens.size == 2)
            put(tokens[0].dropLast(1), tokens[1].let { it == "1" })
        }
    }

    private fun parseGates(input: String): Map<String, Gate> = buildMap {
        for (line in input.lines()) {
            val tokens = line.split(" ")
            assert(tokens.size == 5)
            put(tokens[4], Gate(tokens[0], Operation.valueOf(tokens[1]), tokens[2], tokens[4]))
        }
    }

    private fun Boolean.toLong() = when (this) {
        true -> 1L
        false -> 0L
    }

    private fun solve(inputState: Map<String, Boolean>, gates: Map<String, Gate>): Long {
        val state = inputState.toMutableMap()
        val outputs = gates.values.map { it.outWire }.filter { it.startsWith('z') }.sortedDescending()

        var result = 0L
        for (outputWire in outputs) {
            result = result shl 1
            result = result or (gates[outputWire]?.getOutput(state, gates)?.toLong() ?: throw IllegalStateException())
        }

        return result
    }

    private fun Long.toInputMap(label: Char, nBits: Int): Map<String, Boolean> =
        buildMap {
            var tmp = this@toInputMap
            for (i in 0..<nBits) {
                put("${label}${i.toString().padStart(2, '0')}", (tmp and 1L) == 1L)
                tmp = tmp shr 1
            }
        }

    private fun Map<String, Boolean>.extractLong(label: Char) =
        filter { it.key.startsWith(label) }.toList().sortedBy{ it.first }.map { it.second}.foldRight(0L) {
            current, acc -> ((acc shl 1) or current.toLong())
        }

    private fun extractIncorrectOutputs(actual: Long, expected: Long): Set<String> =
        buildSet {
            var i = 0
            while (1L shl i < max(expected, actual)) {
                if ((actual and (1L shl i)) != (expected and (1L shl i))) {
                    add("z${i.toString().padStart(2, '0')}")
                }
                i++
            }
        }

    private fun swapGates(gates: MutableMap<String, Gate>, label1: String, label2: String) {
        val gate1 = gates[label2]!!
        val gate2 = gates[label1]!!
        gate1.outWire = label1
        gate2.outWire = label2
        gates[label1] = gate1
        gates[label2] = gate2
    }

    override fun partOne(input: String): Long {
        val (stateStr, gatesStr) = input.split("\n\n")
        val state = parseState(stateStr)
        val gates = parseGates(gatesStr)

        return solve(state, gates)
    }

    override fun partTwo(input: String): String {
        val (stateStr, gatesStr) = input.split("\n\n")
        val inputState = parseState(stateStr)
        val gates = parseGates(gatesStr).toMutableMap()
        val numInputBits = inputState.size / 2

        fun getIncorrectOutputs(x: Long, y: Long): Set<String> {
            val inputMap = x.toInputMap('x', numInputBits) + y.toInputMap('y', numInputBits)
            val result = solve(inputMap, gates)
            val expected =  x + y
            val incorrectBits = extractIncorrectOutputs(result, expected).toMutableSet()
            return incorrectBits
        }

        fun printIncorrectOutputsFromManyTestRuns() {
            val incorrectBits: MutableSet<String> = mutableSetOf()
            for (i in 0..20000) {
                val x = Random.nextLong(1L shl numInputBits)
                val y = Random.nextLong(1L shl numInputBits)
                incorrectBits += getIncorrectOutputs(x, y)
            }

            println("${incorrectBits.size} output bits are incorrect, " +
                    "The first incorrect one is ${incorrectBits.minOrNull() ?: "N/A"}.")
        }

        printIncorrectOutputsFromManyTestRuns()

        // We will iteratively find the output labels to swap. The method call above will get you the first incorrect
        // one. Inspect the input file manually to figure out which to swap. Add it to the list below and run the
        // program again.
        val toSwap = listOf(
            "z12" to "qdg",
            "z19" to "vvf",
            "fgn" to "dck",
            "z37" to "nvh",
        )
        for ((a, b) in toSwap) {
            swapGates(gates, a, b)
        }

        printIncorrectOutputsFromManyTestRuns()

        return toSwap.flatMap { (a, b) -> listOf(a, b) }.sorted().joinToString(",").also { println(it) }
    }
}
