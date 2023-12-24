package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 21, "Step Counter")
class Y2023D21Test : AdventSpec<Y2023D21>({

    val exampleInput = """
            ...........
            .....###.#.
            .###.##..#.
            ..#.#...#..
            ....#.#....
            .##..S####.
            .##..#...#.
            .......##..
            .##.#.####.
            .##..##.##.
            ...........
        """.trimIndent()

    partOne {
        exampleInput shouldOutput 42
    }

    partTwo {
        // My solution does not solve the example. :(
    }

})
