package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 12, "TODO")
class Y2024D12Test : AdventSpec<Y2024D12>({

    partOne {
        """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent() shouldOutput 140

        """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent() shouldOutput 772

        """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
        """.trimIndent() shouldOutput 1930
    }

//    partTwo {
//        """
//            a
//        """.trimIndent() shouldOutput 1
//    }

})
