package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private fun String.holidayHash() : Int =
    this.fold(initial = 0) { acc, ch ->
        ((acc + ch.code)*17) % 256
    }

object Y2023D15 : Solution {
    class Lens(val label: String, val focalLength: Int) {
        val boxId: Int by lazy { label.holidayHash() }
    }

    override fun partOne(input: String) =
        input.split(',').sumOf(String::holidayHash).also { println(it) }

    override fun partTwo(input: String) : Int {
        val boxes: List<MutableList<Lens>> = (0..255).map { mutableListOf() }

        for (step in input.split(',')) {
            when {
                '=' in step -> {
                    val lens = Lens(step.substringBefore('='), step.substringAfter('=').toInt())
                    val match = boxes[lens.boxId].withIndex().find { it.value.label == lens.label }
                    if (match != null) {
                        boxes[lens.boxId][match.index] = lens
                    } else {
                        boxes[lens.boxId] += lens
                    }
                }
                '-' in step -> {
                    val toRemove = Lens(step.substringBefore('-'), -1)
                    val match = boxes[toRemove.boxId].withIndex().find { it.value.label == toRemove.label }
                    if (match != null) boxes[toRemove.boxId].removeAt(match.index)
                }
            }
        }

        return boxes.withIndex().sumOf {
            box -> box.value.withIndex().sumOf {
                lens -> box.index.inc() * lens.index.inc() * lens.value.focalLength
            }
        }.also { println(it) }
    }
}