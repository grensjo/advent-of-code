package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 4, "Ceres Search")
class Y2024D04Test : AdventSpec<Y2024D04>({
    val smallExamplePart1 = """
        ..X...
        .SAMX.
        .A..A.
        XMAS.S
        .X....
    """.trimIndent()

    val bigExample = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent()

    partOne {
        smallExamplePart1 shouldOutput 4
        bigExample shouldOutput 18
    }

    partTwo {
        bigExample shouldOutput 9
    }
})
