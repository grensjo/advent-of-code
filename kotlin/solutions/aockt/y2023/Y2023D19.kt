package aockt.y2023

import aockt.y2023.Y2023D19.Category
import aockt.y2023.Y2023D19.Category.*
import aockt.y2023.Y2023D19.Condition
import aockt.y2023.Y2023D19.Part
import aockt.y2023.Y2023D19.Rule
import aockt.y2023.Y2023D19.Workflow
import io.github.jadarma.aockt.core.Solution

private fun String.toRule() : Rule {
    return split('<', '>', ':').let {
        val value = it[1].toInt()
        val allowedValues = when {
            '<' in this -> 1 until value
            '>' in this -> (value + 1)..4000
            else -> throw IllegalArgumentException("Unknown operation in rule $this.")
        }
        Rule(Condition(valueOf(it[0].uppercase()), allowedValues), it[2])
    }
}

private fun String.toWorkflow() : Workflow {
    val label = substringBefore('{')
    val steps = substringAfter('{').substringBefore('}').split(',')
    val rules = steps.filter {':' in it}.map(String::toRule)
    val default = steps.last()
    return Workflow(label, rules, default)
}

private fun String.toWorkflowMap() : Map<String, Workflow> =
    split('\n').map(String::toWorkflow).associateBy { it.label }

private fun String.toPart() : Part =
    Part(
        substringAfter('{').substringBefore('}').split(',').map { partString ->
            partString.split('=').let {
                Category.valueOf(it[0].uppercase()) to it[1].toInt()
            }
        }.toMap()
    )

private fun String.parse() : Pair<Map<String, Workflow>, List<Part>> =
    substringBefore("\n\n").toWorkflowMap() to
            substringAfter("\n\n").split('\n').map(String::toPart)


object Y2023D19 : Solution {

    enum class Category { X, M, A, S }

    data class Part(val ratings: Map<Category, Int>)

    data class PartSet(val constraints: Map<Category, IntRange>) {

        fun applyCondition(condition: Condition) =
            PartSet(
                constraints.map {
                    if (it.key == condition.category) {
                        it.key to (condition.intersect(it.value))
                    } else {
                        it.key to it.value
                    }
                }.toMap()
            )

        fun size() = constraints.values.map(IntRange::count).map(Int::toLong).reduce(Long::times)

        companion object {
            val UNIVERSE = PartSet(
                mapOf(
                    X to 1..4000,
                    M to 1..4000,
                    A to 1..4000,
                    S to 1..4000,
                )
            )
        }
    }

    data class Condition(val category: Category, val allowedValues: IntRange) {
        init {
            assert(allowedValues.first == 1 || allowedValues.last == 4000)
        }

        fun complement() =
            Condition(
                category,
                when {
                    allowedValues.first == 1 -> allowedValues.last.inc()..4000
                    allowedValues.last == 4000 -> 1..allowedValues.first.dec()
                    else -> throw IllegalArgumentException("Not a simple > or < condition.")
                },
            )

        fun acceptsPart(part: Part) : Boolean =
            part.ratings.getValue(category) in allowedValues

        fun intersect(range: IntRange) : IntRange =
            when {
                allowedValues.first == 1 -> when {
                    // This condition is of the form "<X", where X is `allowedValues.last`. Handle
                    // the three cases when the range is completely disjoint with the condition,
                    // when the entire range fulfills the condition, and when there is partial
                    // overlap.
                    allowedValues.last < range.first -> IntRange.EMPTY
                    allowedValues.last >= range.last -> range
                    else -> range.first..allowedValues.last
                }
                allowedValues.last == 4000 -> when {
                    // This condition is of the form ">X", where X is `allowedValues.first`. Handle
                    // the three cases when the range is completely disjoint with the condition,
                    // when the entire range fulfills the condition, and when there is partial
                    // overlap.
                    allowedValues.first > range.last -> IntRange.EMPTY
                    allowedValues.first <= range.first -> range
                    else -> allowedValues.first..range.last
                }
                else -> throw IllegalArgumentException("Not a simple > or < condition.")
            }
    }

    data class Rule(val condition: Condition, val destination: String)

    data class Workflow(val label: String, val rules: List<Rule>, val default: String) {
        // Process a part, and return its next workflow label.
        fun processPart(part: Part) : String {
            for (rule in rules) {
                if (rule.condition.acceptsPart(part)) {
                    return rule.destination
                }
            }
            return default
        }
    }

    override fun partOne(input: String) : Int {
        val (workflows, parts) = input.parse()

        return parts.filter { part ->
            var nextWorkflow = "in"
            while (nextWorkflow !in listOf("A", "R")) {
                nextWorkflow = workflows[nextWorkflow]!!.processPart(part)
            }
            nextWorkflow == "A"
        }.sumOf { part ->
            part.ratings.values.sum()
        }.also { println(it) }
    }

    private fun Map<String, Workflow>.countAccepted(partSet: PartSet, nextLabel: String) : Long {
        if (nextLabel == "A") {
            return partSet.size()
        } else if (nextLabel == "R") {
            return 0
        }

        val workflow = this[nextLabel]!!
        var currentSet = partSet
        var sum = 0L
        for (rule in workflow.rules) {
            sum += countAccepted(currentSet.applyCondition(rule.condition), rule.destination)
            currentSet = currentSet.applyCondition(rule.condition.complement())
        }
        sum += countAccepted(currentSet, workflow.default)
        return sum
    }

    override fun partTwo(input: String) : Long {
        val (workflows, _) = input.parse()
        return workflows.countAccepted(PartSet.UNIVERSE, "in").also { println(it) }
    }
}
