package aockt.y2024

import io.github.jadarma.aockt.core.Solution

object Y2024D14 : Solution {
    data class Vector(val x: Long, val y: Long) {
        operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
        operator fun times(k: Long) = Vector(k * x, k * y)
    }
    private operator fun Long.times(v: Vector) = v * this
    private fun Vector.mod(w: Long, h: Long) =
        Vector(x % w, y % h)

    enum class Quadrant(val x: Long, val y: Long) {
        NORTHWEST(-1, -1),
        NORTHEAST(1, -1),
        SOUTHEAST(1, 1),
        SOUTHWEST(-1, 1),
    }

    private infix fun Long.sameSign(n: Long) = (this < 0 && n < 0) || (this > 0 && n > 0)

    private fun Vector.getQuadrant(w: Long, h: Long): Quadrant? =
        Quadrant.entries.find { (x - w/2) sameSign it.x && (y - h/2) sameSign it.y }

    private data class Robot(val pos: Vector, val velocity: Vector) {
        fun move(time: Long, w: Long, h: Long): Robot =
            Robot(((pos + time * velocity).mod(w, h) + Vector(w, h)).mod(w, h), velocity)
        fun getFinalQuadrant(time: Long, w: Long, h: Long): Quadrant? =
            move(time, w, h).pos.getQuadrant(w, h)
    }

    private val regex = """(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()
    private fun String.toRobot(): Robot =
        regex.find(this)!!.groups.let {
            Robot(
                Vector(it[1]!!.value.toLong(), it[2]!!.value.toLong()),
                Vector(it[3]!!.value.toLong(), it[4]!!.value.toLong())
            )
        }

    // Not used in the final solution, but useful to figure out the strategy for part 2.
    private fun List<Robot>.print(w: Long, h: Long) {
        val positions = map { it.pos }.toSet()
        for (i: Long in 0..<w) {
            for (j: Long in 0..<h) {
                if (Vector(i, j) in positions) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    override fun partOne(input: String): Long {
        val lines = input.lines()
        val (w, h) = lines[0].split(" ").map { it.toLong() }
        val robots = lines.drop(1).map { it.toRobot() }
        val quadrants = robots.map { it.getFinalQuadrant(100, w, h) }
        return quadrants
            .groupingBy { it }.eachCount().filterKeys { it != null }.values
            .map{ it.toLong() }.reduce(Long::times)
            .also { println(it) }
    }

    override fun partTwo(input: String): Int {
        val lines = input.lines()
        val (w, h) = lines[0].split(" ").map { it.toLong() }
        var robots = lines.drop(1).map { it.toRobot() }

        // As it turns out, the robots form a Christmas tree if and only if they are all in distinct positions.
        var steps = 0
        do {
            robots = robots.map { it.move(1, w, h) }
            val positions = robots.map { it.pos }.toSet()
            steps++
        } while (positions.size != robots.size)

        return steps.also { println(it) }
    }
}
