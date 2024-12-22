package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 22, "Monkey Market")
class Y2024D22Test : AdventSpec<Y2024D22>({

    partOne {
        """
            1
            10
            100
            2024
        """.trimIndent() shouldOutput 37327623
    }

    partTwo {
        """
            1
            2
            3
            2024
        """.trimIndent() shouldOutput 23
    }

})
