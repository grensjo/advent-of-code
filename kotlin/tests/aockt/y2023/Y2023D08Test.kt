package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 8, "Haunted Wasteland")
class Y2023D08Test : AdventSpec<Y2023D08>({

    val exampleInput = """
            RL

            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)`
        """.trimIndent()

    val exampleInput2 = """
            LLR

            AAA = (BBB, BBB)
            BBB = (AAA, ZZZ)
            ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    partOne {
        exampleInput shouldOutput 2
        exampleInput2 shouldOutput 6
    }

//    partTwo {
//        exampleInput shouldOutput 5905
//    }

})
