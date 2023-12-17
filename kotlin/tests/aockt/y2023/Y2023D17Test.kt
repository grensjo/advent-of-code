package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 17, "Clumsy Crucible")
class Y2023D17Test : AdventSpec<Y2023D17>({

    partOne {
        """
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
        """.trimIndent() shouldOutput 102
    }

//    partTwo {
//        """
//            a
//        """.trimIndent() shouldOutput 1
//    }

})
