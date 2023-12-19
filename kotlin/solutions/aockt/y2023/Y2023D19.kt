package aockt.y2023

import aockt.y2023.Y2023D19.Operation.*
import aockt.y2023.Y2023D19.Part
import aockt.y2023.Y2023D19.Rule
import aockt.y2023.Y2023D19.Workflow
import io.github.jadarma.aockt.core.Solution

private fun String.toRule() : Rule {
    val op = when {
        '<' in this -> LESS_THAN
        '>' in this -> GREATER_THAN
        else -> throw IllegalArgumentException("Unknown operation in rule $this.")
    }
    return split('<', '>', ':').let {
        Rule(it[0][0], op, it[1].toInt(), it[2])
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
                it[0][0] to it[1].toInt()
            }
        }.toMap()
    )

private fun String.parse() : Pair<Map<String, Workflow>, List<Part>> =
    substringBefore("\n\n").toWorkflowMap() to
            substringAfter("\n\n").split('\n').map(String::toPart)


object Y2023D19 : Solution {

    enum class Operation { LESS_THAN, GREATER_THAN }
    data class Part(val ratings: Map<Char, Int>)

    data class Rule(val category: Char, val operation: Operation, val value: Int, val destination: String) {
        fun acceptsPart(part: Part) : Boolean =
            when (operation) {
                LESS_THAN -> part.ratings[category]!! < value
                GREATER_THAN -> part.ratings[category]!! > value
            }
    }

    data class Workflow(val label: String, val rules: List<Rule>, val default: String) {
        fun processPart(part: Part) : String {
            for (rule in rules) {
                if (rule.acceptsPart(part)) {
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
        }
    }

//    override fun partTwo(input: String) = input.length
}
