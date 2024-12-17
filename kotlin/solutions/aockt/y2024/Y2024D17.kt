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

private class Program(val programCode: List<Int>, var a: Long, var b: Long, var c: Long) {
    var pointer: Int = 0
    var output: MutableList<Int> = mutableListOf()

    private data class LiteralOperand(val literal: Int) {
        init {
            assert(literal in 0..7)
        }
        fun getValue(): Long = literal.toLong()
    }

    private inner class ComboOperand(val operand: Int) {
        fun getValue(): Long =
            when (operand) {
                in 0..3 -> operand.toLong()
                4 -> a
                5 -> b
                6 -> c
                else -> throw IllegalArgumentException("operand out of bounds")
            }

        override fun toString(): String {
            return "ComboOperand(${operand})"
        }
    }

    private fun Int.toLiteralOperand() = LiteralOperand(this)
    private fun Int.toComboOperand() = ComboOperand(this)

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
                val exponent = operand.getValue()
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                a = a shr exponent.toInt()
            }

            Instruction.BXL -> {
                // B xor literal, stored in B
                // B = B xor literal
                val operand = programCode[pointer + 1].toLiteralOperand()
                b = b xor operand.getValue()
            }

            Instruction.BST -> {
                // Writes the combo operand mod 8 to B
                // B = operand % 8
                val operand = programCode[pointer + 1].toComboOperand()
                b = operand.getValue() % 8
            }

            Instruction.JNZ -> {
                if (a != 0L) {
                    val operand = programCode[pointer + 1].toLiteralOperand()
                    pointer = operand.getValue().toInt() - 2
                }
            }

            Instruction.BXC -> {
                // B xor C, stored in B (operand ignored)
                // B = B xor C
                b = b xor c
            }

            Instruction.OUT -> {
                val operand = programCode[pointer + 1].toComboOperand()
                output.add((operand.getValue() % 8).toInt())
            }

            Instruction.BDV -> {
                // Integer division of A by 2 to the power of the operand, stored in B.
                // B = A / 2^operand
                val operand = programCode[pointer + 1].toComboOperand()
                val exponent = operand.getValue()
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                b = a shr exponent.toInt()
            }

            Instruction.CDV -> {
                // Integer division of A by 2 to the power of the operand, stored in C.
                // C = A / 2^operand
                val operand = programCode[pointer + 1].toComboOperand()
                val exponent = operand.getValue()
                assert(exponent <= Int.MAX_VALUE) // fix if this happens
                c = a shr exponent.toInt()
            }
        }

        pointer += 2
        return true
    }

    fun run(): List<Int> {
        while(step()) {}
        return output
    }

    fun print() {
        println("a=$a, b=$b, c=$c")
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
        return Program(instructions, a, b, c)
    }

    override fun partOne(input: String): String {
        val program = parseInput(input)
        return program.run().joinToString(separator=",").also { println(it) }
    }

    override fun partTwo(input: String): Long {
        val originalProgram = parseInput(input)

        // I'm sorry, but the following probably doesn't make much sense if you haven't already solved the problem in a
        // similar way. A proper write-up is likely needed...
        //
        // We compute the initial value of A by choosing three bits at a time, such that the desired piece of code will
        // be output. The behavior of the program depends on more significant bits but not less, so we do it backwards.
        // If we paint ourselves into a corner we can backtrack and make another choice earlier. The search can be seen
        // as a dfs on the tree of possible choices for bit triplets in A.
        fun dfs(codeIndexToOutput: Int, prevA: Long): Long? {
            if (codeIndexToOutput == -1) { return prevA }

            val outputToTrigger = originalProgram.programCode[codeIndexToOutput]
            val currentA = prevA shl 3

            for (nextThreeBits in 0L..7L) {
                val currentAToTest = currentA or nextThreeBits
                val programToTest = Program(
                    originalProgram.programCode, currentAToTest, originalProgram.b, originalProgram.c)
                val output = programToTest.run()
                if (output.first() == outputToTrigger) {
                    // Success for this step! Recurse.
                    val finalA = dfs(codeIndexToOutput - 1, currentAToTest)
                    if (finalA != null) {
                        // We found a valid leaf node!
                        return finalA
                    }
                }
            }

            // This branch was bad - no solution was found.
            return null
        }

        val finalA = dfs(originalProgram.programCode.size - 1, 0)!!

        // Validate the solution.
        originalProgram.a = finalA
        assert(originalProgram.run() == originalProgram.programCode)

        return finalA.also { println(finalA) }
    }
}
