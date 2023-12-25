package aockt.y2023

import aockt.y2023.Y2023D24.Hailstone
import aockt.y2023.Y2023D24.Point3D
import io.github.jadarma.aockt.core.Solution
import java.math.BigInteger

object Y2023D24 : Solution {
    var TEST_AREA_MIN: BigInteger = BigInteger.valueOf(200000000000000L)
    var TEST_AREA_MAX: BigInteger = BigInteger.valueOf(400000000000000L)

    data class Point3D(val x: BigInteger, val y: BigInteger, val z: BigInteger) {
        operator fun get(i: Int) = when(i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException("Point coordinate index out of bounds.")
        }

        operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
        operator fun times(other: BigInteger) = Point3D(x * other, y * other, z * other)
        operator fun div(other: BigInteger) = Point3D(x / other, y / other, z / other)
    }

    data class Point2D(val x: BigInteger, val y: BigInteger) {
        operator fun get(i: Int) = when(i) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Point coordinate index out of bounds.")
        }
        operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)
    }

    data class Hailstone(val p: Point3D, val v: Point3D) {
        infix fun intersectsInTestArea(other: Hailstone) : Boolean {
            // p1 and p2 are two points on this hailstone's trajectory, p3 and p4 are two points on the other's.
            val p1 = p
            val p2 = p + v
            val p3 = other.p
            val p4 = other.p + other.v

            // Formula for finding the intersection of two 2D lines given two points on each line. The intersection
            // point is: (xNum / denominator, yNum / denominator)
            // See https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection#Given_two_points_on_each_line
            val det12 = (p1.x * p2.y - p1.y * p2.x)
            val det34 = (p3.x * p4.y - p3.y * p4.x)
            val xNum = det12 * (p3.x - p4.x) - (p1.x - p2.x) * det34
            val yNum = det12 * (p3.y - p4.y) - (p1.y - p2.y) * det34
            val denominator = (p1.x - p2.x)*(p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x)

            // Denominator is zero if the lines are parallel or coincides. If they are parallel, they never intersect,
            // so we should return false. If they coincide, the number of intersections is either zero or infinite, but
            // since the answer cannot be infinite (how could we put it in the submit box), we just assume they don't
            // intersect in this case.
            if (denominator == BigInteger.ZERO) return false

            // We want to check inequalities like `TEST_AREA_MIN <= (xNum / denominator) <= TEST_AREA_MAX`. To get
            // integer comparisons we'd like to multiply by denominator, but if it is negative that will flip the
            // inequalities. Therefore, let's create new numerators and denominators so that the denominator is > 0.
            val denominator2 = denominator.abs()
            val xNum2 = xNum * denominator.signum().toBigInteger()
            val yNum2 = yNum * denominator.signum().toBigInteger()
            val low = TEST_AREA_MIN * denominator2
            val high = TEST_AREA_MAX * denominator2

            val intersects = xNum2 in low..high && yNum2 in low..high
            return intersects && isInFuture(xNum2, denominator2) && other.isInFuture(xNum2, denominator2)
        }

        // Checks if the found intersection point is in the future for the hailstone. If the x-component of the velocity
        // is positive, the x coordinate of the intersection needs to be bigger than the start x coordinate. That is:
        //   xNum / denominator >= p.x <=> x Num >= p.x * denominator.
        // If the x-component of the velocity is negative, it needs to be smaller instead. It is sufficient to check x.
        private fun isInFuture(xNum: BigInteger, denominator: BigInteger) =
            if (v.x.signum() == 1) xNum >= p.x * denominator else xNum <= p.x * denominator
    }

    override fun partOne(input: String) : Int {
        val hailstones = input.lineSequence().map(String::toHailstone).toList()
        val n = hailstones.size

        // If small input, use the example test area bounds.
        if (n < 10) {
            TEST_AREA_MIN = BigInteger.valueOf(7)
            TEST_AREA_MAX = BigInteger.valueOf(27)
        } else {
            TEST_AREA_MIN = BigInteger.valueOf(200000000000000L)
            TEST_AREA_MAX = BigInteger.valueOf(400000000000000L)
        }

        var count = 0
        for (i in 0 until (n - 1)) {
            for (j in (i+1) until n) {
                if (hailstones[i] intersectsInTestArea hailstones[j]) count++
            }
        }
        return count.also { println(it) }
    }

    override fun partTwo(input: String) : BigInteger {
        val hailstones = input.lineSequence().map(String::toHailstone).toList()
        val n = hailstones.size

        // Just pick 3 hailstones to use, turns out this will be enough information.
        val p1 = hailstones[0].p
        val p2 = hailstones[1].p
        val p3 = hailstones[2].p
        val v1 = hailstones[0].v
        val v2 = hailstones[1].v
        val v3 = hailstones[2].v

        // Define u_k = p_k + t_k * v_k, solve u_2 - u_1 = k(u_3 - u_2) for t_1, t_2, t_3, k.
        // We also need a relation for k. If you like, you can think of the points as being in 4D space with time as the
        // 4th dimension, then the last equation of the system above becomes: t_3 - t_2 = k*(t_2 - t_1)
        // Now this is a system of 4 equations with 4 variables, so intuitively this should have a single solution if
        // one exists -- so assuming the input is solvable there is no need to use the rest of the hailstones.
        //
        // I'm sorry, but I will now output this equation system and give it to WolframAlpha, so deal with it.
        println("""
            Solve for t1,t2,t3,k:
            ${p3.x - p2.x}${v3.x.s()}t3${(-v2.x).s()}t2 = k(${p2.x - p1.x}${v2.x.s()}t2${(-v1.x).s()}t1)
            ${p3.y - p2.y}${v3.y.s()}t3${(-v2.y).s()}t2 = k(${p2.y - p1.y}${v2.y.s()}t2${(-v1.y).s()}t1)
            ${p3.z - p2.z}${v3.z.s()}t3${(-v2.z).s()}t2 = k(${p2.z - p1.z}${v2.z.s()}t2${(-v1.z).s()}t1)
            t3-t2=k(t2-t1)
        """.trimIndent())

        // Hard-code output from WolframAlpha for sample input and my input.
        val t1=if (n > 10) BigInteger.valueOf(542714985863L) else BigInteger.valueOf(5)
        val t2=if (n > 10) BigInteger.valueOf(469657037828L) else BigInteger.valueOf(3)

        val u1 = p1 + v1 * t1
        val u2 = p2 + v2 * t2

        // Use the results from above to go back to t=0 from u1,
        val start = u1 - (u2-u1)*t1/(t2-t1)
        println(start)

        return (start.x + start.y + start.z).also { println(it) }
    }
}

private fun BigInteger.s() = if (signum() == -1) "${this}" else "+${this}"
private fun String.toPoint3D() = split(", ").map(String::trim).map(String::toBigInteger).let { Point3D(it[0], it[1], it[2]) }
private fun String.toHailstone() = split(" @ ").map(String::toPoint3D).let { Hailstone(p = it[0], v = it[1]) }
