package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private fun String.replaceSpelledDigits() =
    this
        .replace("zero", "zero0zero")
        .replace("one", "one1one")
        .replace("two", "two2two")
        .replace("three", "three3three")
        .replace("four", "four4four")
        .replace("five", "five5five")
        .replace("six", "six6six")
        .replace("seven", "seven7seven")
        .replace("eight", "eight8eight")
        .replace("nine", "nine9nine")

private fun String.extractCalibrationValue() =
    10 * find { it.isDigit() }!!.digitToInt() + findLast { it.isDigit() }!!.digitToInt()

object Y2023D01 : Solution {
    override fun partOne(input: String) =
        input.lineSequence().sumOf(String::extractCalibrationValue)

    override fun partTwo(input: String) =
        input.lineSequence().map(String::replaceSpelledDigits).sumOf(String::extractCalibrationValue)
}
