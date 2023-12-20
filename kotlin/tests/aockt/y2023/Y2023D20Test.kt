package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 20, "Pulse Propagation")
class Y2023D20Test : AdventSpec<Y2023D20>({

    partOne {
        """
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a
        """.trimIndent() shouldOutput 32000000

        """
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output
        """.trimIndent() shouldOutput 11687500
    }

    partTwo {}

})
