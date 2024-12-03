package aockt.y2024

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2024D02 : Solution {

    override fun partOne(input: String) =
        input.to2DLongList().count { it.isSafe() }.also {println(it)}

    override fun partTwo(input: String) =
        input.to2DLongList().count { it.isSafeWithDampener() }.also {println(it)}

    private fun String.to2DLongList() = lines().map { it.trim().split("\\s+".toRegex()).map { it.toLong() } }

    private fun List<Long>.isMonotone() = zipWithNext().all { (a,b) -> a > b } || zipWithNext().all { (a,b) -> a < b }
    private fun List<Long>.isSafe() = isMonotone() && zipWithNext().map { (a, b) -> (a - b).absoluteValue }.all { it in 1..3 }
    private fun List<Long>.isSafeWithDampener() =
        (0..<size).toList().map {subList(0, it) + subList(it+1, size)}.any { it.isSafe() }
}
