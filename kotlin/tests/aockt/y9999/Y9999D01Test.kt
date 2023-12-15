package aockt.y9999

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(9999, 1, "Example Puzzle")
class Y9999D01Test : AdventSpec<Y9999D01>({

    partOne {
        """
            a
        """.trimIndent() shouldOutput 1
    }

//    partTwo {
//        """
//            a
//        """.trimIndent() shouldOutput 1
//    }

})
