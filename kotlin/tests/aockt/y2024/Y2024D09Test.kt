package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 9, "TODO")
class Y2024D09Test : AdventSpec<Y2024D09>({
    val exampleInput = "2333133121414131402"

    partOne {
        exampleInput shouldOutput 1928
    }

    partTwo {
        exampleInput shouldOutput 2858
    }

})
