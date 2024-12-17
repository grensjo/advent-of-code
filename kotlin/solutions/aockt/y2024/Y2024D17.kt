package aockt.y2024

import io.github.jadarma.aockt.core.Solution

private enum class Instruction(val opcode: Int) {
    ADV(0),
    BXL(1),
    BST(2),
    JNZ(3),
    BXC(4),
    OUT(5),
    BDV(6),
    CDV(7);

    companion object {
        val opcodeToInstruction = entries.associateBy { it.opcode }
    }
}
private fun Int.toInstruction() = Instruction.opcodeToInstruction[this]!!

private data class State(var a: Long, var b: Long, var c: Long)

private sealed class ComboOperand {
    abstract fun getValue(state: State): Long
}

private data class LiteralOperand(val literal: Int) : ComboOperand() {
    init {
        assert(literal in 0..7)
    }
    override fun getValue(state: State): Long = literal.toLong()
}

private enum class Register { A, B, C }

private data class RegisterOperand(val operand: Int) : ComboOperand() {
    val register = when (operand) {
        4 -> Register.A
        5 -> Register.B
        6 -> Register.C
        else -> throw IllegalArgumentException()
    }

    override fun getValue(state: State): Long =
        when (register) {
            Register.A -> state.a
            Register.B -> state.b
            Register.C -> state.c
        }

    override fun toString(): String {
        return "RegisterOperand(${register})"
    }
}

private data object LegacyOperand: ComboOperand() {
    override fun getValue(state: State): Long = throw AssertionError("Tried to get value of legacy operand.")
}

private fun Int.toComboOperand(): ComboOperand =
    when (this) {
        in 0..3 -> LiteralOperand(this)
        in 4..6 -> RegisterOperand(this)
        7 -> LegacyOperand
        else -> throw IllegalArgumentException("operand not in range 0..7")
    }

private fun Int.toLiteralOperand(): LiteralOperand =
    when (this) {
        in 0..7 -> LiteralOperand(this)
        else -> throw IllegalArgumentException("operand not in range 0..7")
    }

private class Program(val state: State, val programCode: List<Int>) {
    var pointer: Int = 0
    var output: MutableList<Int> = mutableListOf()

    fun step(): Boolean {
        if (pointer + 1 >= programCode.size) {
            return false
        }
        val instruction = programCode[pointer].toInstruction()

        when (instruction) {
            Instruction.ADV -> {
                // Integer division of A by 2 to the power of the _combo_ operand, stored in A.
                // A = A / 2^operand
                val operand = programCode[pointer + 1].toComboOperand()
                val exponent = operand.getValue(state)
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                state.a = state.a shr exponent.toInt()
            }

            Instruction.BXL -> {
                // B xor literal, stored in B
                // B = B xor literal
                val operand = programCode[pointer + 1].toLiteralOperand()
                state.b = state.b xor operand.getValue(state)
            }

            Instruction.BST -> {
                // Writes the combo operand mod 8 to B
                // B = operand % 8
                val operand = programCode[pointer + 1].toComboOperand()
                state.b = operand.getValue(state) % 8
            }

            Instruction.JNZ -> {
                if (state.a != 0L) {
                    val operand = programCode[pointer + 1].toLiteralOperand()
                    pointer = operand.getValue(state).toInt() - 2
                }
            }

            Instruction.BXC -> {
                // B xor C, stored in B (operand ignored)
                // B = B xor C
                state.b = state.b xor state.c
            }

            Instruction.OUT -> {
                val operand = programCode[pointer + 1].toComboOperand()
                output.add((operand.getValue(state) % 8).toInt())
            }

            Instruction.BDV -> {
                // Integer division of A by 2 to the power of the operand, stored in B.
                // B = A / 2^operand
                val operand = programCode[pointer + 1].toComboOperand()
                val exponent = operand.getValue(state)
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                state.b = state.a shr exponent.toInt()
            }

            Instruction.CDV -> {
                // Integer division of A by 2 to the power of the operand, stored in C.
                // C = A / 2^operand
                val operand = programCode[pointer + 1].toComboOperand()
                val exponent = operand.getValue(state)
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                state.c = state.a shr exponent.toInt()
            }
        }

        pointer += 2
        return true
    }

    fun print() {
        println(state)
        println(programCode)
        println("Output so far: ${output}")
        println("pointer: ${pointer}")
        if (pointer + 1 < programCode.size) {
            println()
            println("Next instruction: ${programCode[pointer].toInstruction()}(${programCode[pointer+1].toComboOperand()})")
        }
        else {
            println()
            println("No more instructions.")
        }
        println()
    }
}

object Y2024D17 : Solution {
    private fun parseInput(input: String): Program {
        val lines = input.lines()
        val a = lines[0].split(" ").last().toLong()
        val b = lines[1].split(" ").last().toLong()
        val c = lines[2].split(" ").last().toLong()
        val instructions = lines[4].split(" ").last().split(",").map(String::toInt)
        return Program(State(a, b, c), instructions)
    }

    override fun partOne(input: String): String {
        val program = parseInput(input)
        program.print()

        while (program.step()) {
            program.print()
        }

        return program.output.joinToString(separator=",").also { println(it) }
    }

    override fun partTwo(input: String): Long {
        val originalProgram = parseInput(input)

        fun dfs(codeIndexToOutput: Int, prevA: Long): Long? {
            if (codeIndexToOutput == -1) { return prevA }

            val outputToTrigger = originalProgram.programCode[codeIndexToOutput]
            val currentA = prevA shl 3

            for (nextThreeBits in 0L..7L) {
                val currentAToTest = currentA or nextThreeBits
                val programToTest = Program(
                    State(currentAToTest, originalProgram.state.b, originalProgram.state.c),
                    originalProgram.programCode)
                while (programToTest.step()) {}
                if (programToTest.output.first().toInt() == outputToTrigger) {
                    // Success!
                    println("$codeIndexToOutput - targetCode: ${outputToTrigger}, nextThreeBits: ${nextThreeBits}, output was: ${programToTest.output.first()}")
                    val finalA = dfs(codeIndexToOutput - 1, currentAToTest)
                    if (finalA != null) {
                        return finalA
                    }
                }
            }

            // This branch was bad - no solution was found.
            return null

        }
        val finalA = dfs(originalProgram.programCode.size - 1, 0)!!

        originalProgram.state.a = finalA
        while (originalProgram.step()) {}
        assert(originalProgram.output == originalProgram.programCode)

        return finalA.also { println(finalA) }
    }
}
