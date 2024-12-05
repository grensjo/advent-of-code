package aockt.y2024

import aockt.y2024.Y2024D05.toRuleBook
import io.github.jadarma.aockt.core.Solution
import java.util.*

object Y2024D05 : Solution {
    private fun String.toRule(): Rule {
        val (a: Int, b: Int) = split("|").map(String::toInt)
        return Rule(a, b)
    }
    data class Rule(val before: Int, val after: Int)

    data class RuleBook(val mustComeBefore: Map<Int, Set<Int>>, val mustComeAfter: Map<Int, Set<Int>>)

    private fun List<Rule>.toRuleBook() : RuleBook {
        val mustComeBefore: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
        val mustComeAfter: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

        for (rule in this) {
            mustComeBefore.getOrPut(rule.after) { mutableSetOf() }.add(rule.before)
            mustComeAfter.getOrPut(rule.before) { mutableSetOf() }.add(rule.after)
        }

        return RuleBook(mustComeBefore, mustComeAfter)
    }

    data class Update(val pages: List<Int>) {
        init {
            assert(pages.size % 2 == 1)
        }

        fun isValid(ruleBook: RuleBook): Boolean {
            val before: MutableSet<Int> = mutableSetOf()

            for (page in pages) {
                if ((ruleBook.mustComeAfter[page]?.intersect(before)?.size ?: 0) > 0) {
                    return false
                }
                before.add(page)
            }

            return true
        }

        fun topologicallySorted(ruleBook: RuleBook): Update {
            val visited: MutableSet<Int> = mutableSetOf()
            val stack = Stack<Int>()

            fun dfs(current: Int) {
                visited.add(current)
                for (next in ruleBook.mustComeAfter[current]?.filter { it in pages } ?: setOf()) {
                    if (next !in visited) {
                        dfs(next)
                    }
                }
                stack.push(current)
            }

            for (start in pages.filter { (ruleBook.mustComeBefore[it]?.filter { it in pages } ?: setOf()).isEmpty() }) {
                dfs(start)
            }

            val sortedList: MutableList<Int> = mutableListOf()
            while (!stack.empty()) {
                sortedList.add(stack.pop())
            }
            return Update(sortedList)
        }

        val middlePage: Int
            get() = pages[pages.size / 2]
    }
    private fun String.toUpdate() = Update(split(",").map(String::toInt))

    private fun parseInput(input: String): Pair<List<Rule>, List<Update>>{
        val (rulesString: String, updatesString: String) = input.split("\n\n")
        return rulesString.lines().map { it.toRule() } to updatesString.lines().map { it.toUpdate() }
    }

    override fun partOne(input: String): Int {
        val (rules, updates) = parseInput(input)
        val ruleBook = rules.toRuleBook()

        return updates.filter { it.isValid(ruleBook) }.sumOf { it.middlePage }
            .also { println(it) }
    }

    override fun partTwo(input: String): Int {
        val (rules, updates) = parseInput(input)
        val ruleBook = rules.toRuleBook()

        return updates.filterNot { it.isValid(ruleBook) }.map { it.topologicallySorted(ruleBook) }.sumOf { it.middlePage }
            .also { println(it) }
    }
}
