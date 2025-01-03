package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 8, "TODO")
class Y2024D08Test : AdventSpec<Y2024D08>({
    val exampleInput = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent()

    partOne {
        """
            ..........
            ..........
            ..........
            ....a.....
            ..........
            .....a....
            ..........
            ..........
            ..........
            ..........
        """.trimIndent() shouldOutput 2

        exampleInput shouldOutput 14
    }

    partTwo {
        """
            T.........
            ...T......
            .T........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
        """.trimIndent() shouldOutput 9

        exampleInput shouldOutput 34
    }
})
