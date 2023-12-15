package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private fun String.holidayHash() : Int =
    this.fold(initial = 0) { acc, ch ->
        ((acc + ch.code)*17) % 256
    }

object Y2023D15 : Solution {
    class Lens(val label: String, val focalLength: Int? = null) {
        val boxId: Int by lazy { label.holidayHash() }
    }

    override fun partOne(input: String) =
        input.split(',').sumOf(String::holidayHash).also { println(it) }

    override fun partTwo(input: String) : Int {
        val boxes: List<LinkedHashMap<String, Lens>> = (0..255).map { LinkedHashMap() }

        for (step in input.split(',')) {
            when {
                '=' in step -> {
                    val lens = Lens(step.substringBefore('='), step.substringAfter('=').toInt())
                    boxes[lens.boxId][lens.label] = lens
                }
                '-' in step -> {
                    val lens = Lens(step.substringBefore('-'))
                    boxes[lens.boxId].remove(lens.label)
                }
            }
        }

        return boxes.withIndex().sumOf {
            box -> box.value.values.withIndex().sumOf {
                lens -> box.index.inc() * lens.index.inc() * lens.value.focalLength!!
            }
        }.also { println(it) }
    }
}
