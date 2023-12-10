package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 10, "Pipe Maze")
class Y2023D10Test : AdventSpec<Y2023D10>({
    val exampleInput = """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
    """.trimIndent()

    partOne {
        """
            -L|F7
            7S-7|
            L|7||
            -L-J|
            L|-JF
        """.trimIndent() shouldOutput 4

        """
            7-F7-
            .FJ|7
            SJLL7
            |F--J
            LJ.LJ
        """.trimIndent() shouldOutput 8
    }

    partTwo {
    }
})
