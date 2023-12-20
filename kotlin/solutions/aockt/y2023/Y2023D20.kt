package aockt.y2023

import aockt.y2023.Y2023D20.Signal.*
import io.github.jadarma.aockt.core.Solution
import kotlin.math.sign

object Y2023D20 : Solution {

    enum class Signal {
        LOW {
            override fun invert() = HIGH
            override fun toString() = "-low"
        },
        HIGH {
            override fun invert() = LOW
            override fun toString() = "-high"
        };

        abstract fun invert(): Signal

    }

    data class Pulse(val from: String, val to: String, val signal: Signal) {
        override fun toString() = "$from $signal-> $to"
        companion object {
            val BUTTON_PULSE = Pulse("button", "broadcaster", LOW)
        }
    }

    abstract class Module(val label: String, val destinations: List<String>) {
        abstract fun process(pulse: Pulse): List<Pulse>
        fun createPulses(signal: Signal) = destinations.map { Pulse(from = label, to = it, signal = signal) }

        open fun registerInput(inputLabel: String) {}
    }

    class BroadcastModule(label: String, destinations: List<String>) : Module(label, destinations) {
        override fun process(pulse: Pulse): List<Pulse> = createPulses(pulse.signal)
    }

    class FlipFlopModule(label: String, destinations: List<String>) : Module(label, destinations) {
        private var state = LOW

        override fun process(pulse: Pulse): List<Pulse> =
            when (pulse.signal) {
                LOW -> {
                    state = state.invert()
                    createPulses(state)
                }
                HIGH -> listOf()
            }
    }

    class ConjunctionModule(label: String, destinations: List<String>) : Module(label, destinations) {
        private val lastReceived : MutableMap<String, Signal> = mutableMapOf()

        override fun registerInput(inputLabel: String) {
            lastReceived[inputLabel] = LOW
        }

        override fun process(pulse: Pulse): List<Pulse> {
            lastReceived[pulse.from] = pulse.signal
            return createPulses(if (lastReceived.values.all { it == HIGH }) LOW else HIGH)
        }
    }

    private fun String.toModule() : Module {
        val lhs = substringBefore(" -> ")
        val rhs = substringAfter(" -> ")
        val destinations : List<String> = rhs.split(", ")
        return when {
            lhs == "broadcaster" -> BroadcastModule(lhs, destinations)
            lhs[0] == '%' -> FlipFlopModule(lhs.substring(1), destinations)
            lhs[0] == '&' -> ConjunctionModule(lhs.substring(1), destinations)
            else -> throw IllegalArgumentException("Unknown module type")
        }
    }

    data class Simulation(val modules: Map<String, Module>) {
        val signalCounts = mutableMapOf(LOW to 0L, HIGH to 0L)

        init {
            for (module in modules.values) {
                module.destinations.forEach { it -> modules[it]?.registerInput(module.label) }
            }
        }

        private fun simulationStep(checkForRx : Boolean = false, debuggingOutput : Boolean = false) : Boolean {
            val pulseQueue: ArrayDeque<Pulse> = ArrayDeque()
            pulseQueue += Pulse.BUTTON_PULSE

            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                if (debuggingOutput) println(pulse)
                if (pulse.to == "rx" && pulse.signal == LOW) return true
                signalCounts.computeIfPresent(pulse.signal) { _, value -> value + 1L }

                pulseQueue.addAll(modules[pulse.to]?.process(pulse) ?: listOf())
            }

            return false
        }

        fun simulateFixedTimes(numButtonPresses: Int) : Long {
            for (i in 0 until numButtonPresses) {
                simulationStep(debuggingOutput = i == 0)
            }
            println("${signalCounts[LOW]} low pulses, ${signalCounts[HIGH]} high pulses")
            return signalCounts.values.reduce(Long::times).also { println("$it\n\n") }
        }

        fun simulateUntilRxLow() : Long {
            var i = 0L

            while (true) {
                i++
                if (i % 1000000L == 0L) println("${i / 1000000} million presses...")
                if (simulationStep(checkForRx = true)) {
                    println(i)
                    return i
                }
            }
        }
    }

    private fun String.toSimulation() = Simulation(lineSequence().map { it.toModule() }.associateBy { it.label })

    override fun partOne(input: String) = input.toSimulation().simulateFixedTimes(1000)

    override fun partTwo(input: String) = input.toSimulation().simulateUntilRxLow()
}
