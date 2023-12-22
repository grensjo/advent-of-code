package aockt.y2023

import aockt.y2023.Y2023D22.Brick
import aockt.y2023.Y2023D22.Point3D
import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

object Y2023D22 : Solution {
    data class Point3D(val x: Int, val y: Int, val z: Int) {
        operator fun get(i: Int) = when(i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IllegalArgumentException("Point coordinate index out of bounds.")
        }
    }
    data class Point2D(val x: Int, val y: Int) {
        operator fun get(i: Int) = when(i) {
            0 -> x
            1 -> y
            else -> throw IllegalArgumentException("Point coordinate index out of bounds.")
        }
    }
    data class Brick(val id: Int, val a: Point3D, val b: Point3D) {
        init {
            // At least two coordinates should be the same.
            assert((0 .. 2).count { a[it] == b[it] } >= 2)
        }
        val length = (0..2).sumOf { (a[it] - b[it]).absoluteValue }

        val minZ: Int
            get() = min(a.z, b.z)

        val maxZ: Int
            get() = max(a.z, b.z)

        private fun getXYPoints() : Set<Point2D> {
            if (a.x != b.x) {
                assert(a.y == b.y)
                return (min(a.x, b.x)..max(a.x, b.x)).map { Point2D(it, a.y) }.toSet()
            } else if (a.y != b.y) {
                assert(a.x == b.x)
                return (min(a.y, b.y)..max(a.y, b.y)).map { Point2D(a.x, it) }.toSet()
            } else {
                return setOf(Point2D(a.x, a.y))
            }
        }

        infix fun intersectsXY(other: Brick) =
            getXYPoints().intersect(other.getXYPoints()).isNotEmpty()

        infix fun supportedBy(other: Brick) =
            other.maxZ == (minZ - 1) && this intersectsXY other

    }

    override fun partOne(input: String) : Int {
        val bricks = input.toBrickList().sortedBy { max(it.a.z, it.b.z) } // max or min shouldn't matter
        val zMax = bricks.maxOf { it.maxZ }
        val bricksByEndZ: List<MutableList<Brick>> = buildList(zMax+1) {
            for (i in 0..zMax) add(mutableListOf())
        }

        for (brick in bricks) {
            val newMinZ =
                ((1 until brick.minZ).reversed()
                    .firstOrNull {z ->
                        bricksByEndZ[z].any { brick intersectsXY it }
                    } ?: 0) + 1
            val newMaxZ = newMinZ + brick.maxZ - brick.minZ
            val newBrick =
                Brick(
                    brick.id,
                    Point3D(brick.a.x, brick.a.y, newMinZ),
                    Point3D(brick.b.x, brick.b.y, newMaxZ)
                )
            bricksByEndZ[newMaxZ].add(newBrick)
        }

        val nonSafe : MutableSet<Int> = mutableSetOf()
        for (brick in bricksByEndZ.flatten()) {
            val supportingBricks = bricksByEndZ[brick.minZ - 1].filter { brick supportedBy it }
            if (supportingBricks.size == 1) {
                nonSafe.add(supportingBricks.first().id)
            }
        }

        return (bricks.size - nonSafe.size).also { println(it) }
    }

//    override fun partTwo(input: String) = input.length
}

private fun String.toPoint3D() =
    split(',').map(String::toInt).let { Point3D(it[0], it[1], it[2]) }
private fun IndexedValue<String>.toBrick() =
    value.split('~').map(String::toPoint3D).let { Brick(index, it[0], it[1]) }
private fun String.toBrickList() =
    lineSequence().withIndex().map { it.toBrick() }.toList()
