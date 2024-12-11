package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 10, "Hoof It")
class Y2024D10Test : AdventSpec<Y2024D10>({
    val exampleInput = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent()

    partOne {
        exampleInput shouldOutput 36
    }

   partTwo {
       exampleInput shouldOutput 81
   }

})
