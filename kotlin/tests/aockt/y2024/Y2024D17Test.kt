package aockt.y2024

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2024, 17, "Chronospatial Computer")
class Y2024D17Test : AdventSpec<Y2024D17>({

    partOne {
        """
            Register A: 10
            Register B: 0
            Register C: 0
            
            Program: 5,0,5,1,5,4
        """.trimIndent() shouldOutput "0,1,2"

        """
            Register A: 2024
            Register B: 0
            Register C: 0
            
            Program: 0,1,5,4,3,0
        """.trimIndent() shouldOutput "4,2,5,6,7,7,7,7,3,1,0"

        """
            Register A: 729
            Register B: 0
            Register C: 0

            Program: 0,1,5,4,3,0
        """.trimIndent() shouldOutput "4,6,3,5,6,3,5,2,1,0"
    }

//    partTwo {
//        """
//            a
//        """.trimIndent() shouldOutput 1
//    }

})
