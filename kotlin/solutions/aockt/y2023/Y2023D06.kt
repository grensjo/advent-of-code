package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.ceil
import kotlin.math.sqrt

private const val EPSILON = 1e-6

private data class Race(val time: Long, val distance: Long) {

    // The lowest and highest press times to receive a record are the solutions to the equation
    //    x + d/x = T    <=>    x = T/2 +- sqrt(T^2/4 - d)
    val pqTerm1 : Double
        get() = time.toDouble() / 2.0

    // Subtract EPSILON to ensure we beat the record, not just perform exactly the same (see the last sample race).
    val pqTerm2 : Double
        get() = sqrt(pqTerm1*pqTerm1 - distance - EPSILON)

    val lowestRecordPress : Long
        get() = ceil(pqTerm1 - pqTerm2).toLong()

    val highestRecordPress : Long
        get() = (pqTerm1 + pqTerm2).toLong()
}

object Y2023D06 : Solution {

    private fun parseIntList(input: String, badKerning: Boolean) : List<Long> =
        if (badKerning) {
           listOf(input.replace(" ", "").toLong())
        } else {
            input.split(' ').filter { it.isNotBlank() }.map { it.toLong() }
        }

    private fun parseInput(input: String, badKerning: Boolean): List<Race> {
        val (timeString, distanceString) = input.lineSequence().toList().let {
            it[0] to it[1]
        }
        val timeList = timeString.substringAfter("Time:").trim()
        val distanceList = distanceString.substringAfter("Distance:").trim()
        return parseIntList(timeList, badKerning)
            .zip(parseIntList(distanceList, badKerning))
            .map { (time, distance) -> Race(time, distance) }
    }

    private fun solve(input: String, badKerning: Boolean) =
        parseInput(input, badKerning).map { it.highestRecordPress - it.lowestRecordPress + 1 }.reduce(Long::times)

    override fun partOne(input: String) : Long =
        solve(input, badKerning = false)

    override fun partTwo(input: String) : Long =
        solve(input, badKerning = true)

}
