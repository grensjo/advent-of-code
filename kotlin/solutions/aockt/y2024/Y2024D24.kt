package aockt.y2024

import aockt.y2024.Y2024D24.Operation.*
import io.github.jadarma.aockt.core.Solution
import java.text.ParseException

object Y2024D24 : Solution {

    private enum class Operation {
        AND,
        OR,
        XOR,
    }

    private data class Gate(val inWire1: String, val op: Operation, val inWire2: String, val outWire: String) {
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

    private fun solve(input: String): Long {
        val (inputState, inputGates) = input.split("\n\n")
        val state = parseState(inputState).toMutableMap()
        val gates = parseGates(inputGates)
        println(state.size)
        println(gates.size)

        val outputs = gates.values.map { it.outWire }.filter { it.startsWith('z') }.sortedDescending()

        var result = 0L
        for (outputWire in outputs) {
            result = result shl 1
            result = result or (gates[outputWire]?.getOutput(state, gates)?.toLong() ?: throw IllegalStateException())
        }

        return result.also { println(it) }
    }

    override fun partOne(input: String) = solve(input)

//    override fun partTwo(input: String): Long {
//        println(7L.toInputMap('x'))
//        println(7L.toInputMap('x').extractLong('x'))
//        return 0L
//    }
}
