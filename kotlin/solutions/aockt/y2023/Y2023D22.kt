package aockt.y2023

import aockt.y2023.Y2023D22.Brick
import aockt.y2023.Y2023D22.Point3D
import io.github.jadarma.aockt.core.Solution
import java.util.PriorityQueue
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
            // At least two coordinates should be the same, since all bricks are along the grid lines.
            assert((0 .. 2).count { a[it] == b[it] } >= 2)
        }

        // A parent of this brick is defined as a brick that this brick supports.
        val parents: MutableSet<Int> = mutableSetOf()

        // A child of this brick is defined as a brick that supports this brick.
        val children: MutableSet<Int> = mutableSetOf()

        val minZ: Int
            get() = min(a.z, b.z)

        val maxZ: Int
            get() = max(a.z, b.z)

        // Returns all points in the projection of this brick onto the xy plane.
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

        // Returns true if the xy projections of the two bricks has at least one common point.
        infix fun intersectsXY(other: Brick) =
            getXYPoints().intersect(other.getXYPoints()).isNotEmpty()

        // Returns true if this brick is supported by the other brick. This is true if the other brick's top z
        // coordinate is right below this brick's lowest z coordinate, and the xy projections of the bricks intersect.
        infix fun isSupportedBy(other: Brick) =
            other.maxZ == (minZ - 1) && this intersectsXY other
    }

    class SandStack(bricksPreFall: List<Brick>) {
        private val heightUpperBound = bricksPreFall.maxOf { it.maxZ }

        // This can be seen as a topological sort of the graph formed by the parent pointers, since they only ever go
        // to bricks with a higher z coordinate.
        val bricksByZ: List<MutableList<Brick>> =
            buildList(heightUpperBound + 1) {
                for (i in 0..heightUpperBound) add(mutableListOf())
            }
        val bricksById: MutableMap<Int, Brick> = mutableMapOf()

        init {
            // Simulate the bricks falling down towards the ground, add bricks with the new post-fall coordinates to
            // bricksByZ and bricksById.
            for (brick in bricksPreFall) {
                val newMinZ =
                    ((1 until brick.minZ).reversed()
                        .firstOrNull { z ->
                            bricksByZ[z].any { brick intersectsXY it }
                        } ?: 0) + 1
                val newMaxZ = newMinZ + brick.maxZ - brick.minZ
                val newBrick =
                    Brick(
                        brick.id,
                        Point3D(brick.a.x, brick.a.y, newMinZ),
                        Point3D(brick.b.x, brick.b.y, newMaxZ)
                    )
                bricksByZ[newMaxZ].add(newBrick)
                bricksById[newBrick.id] = newBrick
            }

            // Populate child and parent pointers.
            for (brick in bricksByZ.asSequence().flatten()) {
                val children = bricksByZ[brick.minZ - 1].filter { brick isSupportedBy it }
                brick.children.addAll(children.map(Brick::id))
                for (child in children) {
                    child.parents.add(brick.id)
                }
            }
        }
    }

    override fun partOne(input: String) : Int {
        val sandStack = SandStack(input.toBrickList())
        // Return the number of bricks that do NOT have a parent with that brick as it's only child -- i.e. the number
        // of bricks that are not the sole supporting brick of any brick.
        return sandStack.bricksById.values.filterNot {
            it.parents.any {p -> sandStack.bricksById[p]!!.children.size == 1}
        }.count().also { println(it) }
    }

    override fun partTwo(input: String) : Int {
        val sandStack = SandStack(input.toBrickList())
        var sum = 0

        // Go through each brick to see what the consequences would be if that brick disintegrated. (This could be done
        // in any order.)
        for (removedBrick in sandStack.bricksByZ.asSequence().flatten()) {
            // Map that tracks for each brick how many of its supporting bricks have fallen/disintegrated. When
            // this is equal to the number of children, we know that all bricks that support this brick has fallen.
            val nodeToNumRemovedSupports: MutableMap<Int, Int> = mutableMapOf()

            // We will traverse all nodes reachable from removedBrick using parent pointers. Use a priority queue to
            // make sure we process the nodes in order of increasing upper z coordinate, and thus never process a bruck
            // until all it's reachable children have been processed. (Sorting by z corresponds to is a topological sort
            // of the graph formed by the parent edges.)
            val queue: PriorityQueue<Brick> = PriorityQueue(Comparator.comparing(Brick::maxZ))
            queue.add(removedBrick)

            var chainSize = 0
            while (queue.isNotEmpty()) {
                val currentBrick = queue.poll()

                if (currentBrick.id != removedBrick.id &&
                    nodeToNumRemovedSupports.getOrDefault(currentBrick.id, 0) != currentBrick.children.size) {
                    // This brick does not fall.
                    continue
                }

                // This brick does fall, increase the counter.
                chainSize++

                // Go through the parents and update nodeToNumRemovedSupports to reflect that this block will
                // dissolve/fall.
                for (parentId in currentBrick.parents) {
                    val parent = sandStack.bricksById[parentId]!!
                    if (parentId !in nodeToNumRemovedSupports) {
                        // Only add the parent to the queue once.
                        queue.add(parent)
                    }
                    nodeToNumRemovedSupports.merge(parentId, 1, Int::plus)
                }
            }

            // We need to subtract one since the dissolved brick was also counted, and we're looking for the sum of the
            // number of _other_ bricks that will fall.
            sum += chainSize - 1
        }

        return sum.also { println(it) }
    }
}

private fun String.toPoint3D() =
    split(',').map(String::toInt).let { Point3D(it[0], it[1], it[2]) }
private fun IndexedValue<String>.toBrick() =
    value.split('~').map(String::toPoint3D).let { Brick(index, it[0], it[1]) }
private fun String.toBrickList() =
    lineSequence().withIndex().map { it.toBrick() }.sortedBy(Brick::maxZ).toList() // maxZ or minZ shouldn't matter here
