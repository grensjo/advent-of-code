package aockt.y2023

import aockt.y2023.Y2023D20.Signal.*
import io.github.jadarma.aockt.core.Solution
import java.math.BigInteger
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
        val history: MutableMap<String, MutableList<MutableList<Char>>> = mutableMapOf()

        init {
            for (module in modules.values) {
                module.destinations.forEach { it -> modules[it]?.registerInput(module.label) }
            }
            modules.keys.forEach {
                history.putIfAbsent(it, mutableListOf())
            }
        }

        private fun simulationStep(checkForLabelHigh : String? = null) : Boolean {
            var foundLabelHigh = false
            val pulseQueue: ArrayDeque<Pulse> = ArrayDeque()
            pulseQueue += Pulse.BUTTON_PULSE

            modules.keys.forEach {
                history.getValue(it).add(mutableListOf())
            }

            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                signalCounts.computeIfPresent(pulse.signal) { _, value -> value + 1L }

                val toAdd = modules[pulse.to]?.process(pulse) ?: listOf()
                if (checkForLabelHigh != null && toAdd.getOrNull(0)?.from == checkForLabelHigh) {
                    if (toAdd[0].signal == HIGH) foundLabelHigh = true
                }
                pulseQueue.addAll(toAdd)
            }

            return foundLabelHigh
        }

        fun simulateFixedTimes(numButtonPresses: Int) : Long {
            for (i in 0 until numButtonPresses) {
                simulationStep()
            }
            return signalCounts.values.reduce(Long::times).also { println("$it\n\n") }
        }

        fun simulateToFindHighPeriod(label: String) : Long {
            var step = 0L
            var firstHighStep: Long? = null

            while (true) {
                if (simulationStep(checkForLabelHigh = label)) {
                    if (firstHighStep == null) {
                        firstHighStep = step
                    } else {
                        return step - firstHighStep
                    }
                }
                step++
            }
        }

    }

    private fun String.toSimulation() = Simulation(lineSequence().map { it.toModule() }.associateBy { it.label })

    override fun partOne(input: String) = input.toSimulation().simulateFixedTimes(1000)

    override fun partTwo(input: String): Long {
        val sim = input.toSimulation()

        // Assumptions about structure:
        // - rx is the output of exactly one module, and that module is a conjunction.
        // - The inputs to that conjunction turn HIGH with a fixed period, small enough to find the period length
        //   through simulation.
        val conjunctionLabel =
            sim.modules.values.filter { "rx" in it.destinations }.also { assert(it.size == 1) }.first().label
        val inputLabels = sim.modules.values.filter { conjunctionLabel in it.destinations }.map(Module::label)

        // An extra assumption... the first time the inputs turn HIGH is after the same amount of steps as the period.
        // That means we can just use lcm instead of the Chinese Remainder Theorem.
        var ans = 1L
        for (label in inputLabels) {
            ans = ans.lcm(sim.simulateToFindHighPeriod(label))
        }
        return ans.also { println(it) }
    }
}

private fun BigInteger.lcm(b: BigInteger) =
    (this.abs() / this.gcd(b)) * b.abs()

private fun Long.lcm(b: Long) = BigInteger.valueOf(this).lcm(BigInteger.valueOf(b)).toLong()
