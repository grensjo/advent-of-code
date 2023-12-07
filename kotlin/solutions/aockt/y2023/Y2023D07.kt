package aockt.y2023

import io.github.jadarma.aockt.core.Solution

private fun strToIntList(str: String, withJokerRule: Boolean) =
    str.map { ch ->
        when {
            ch.isDigit() -> ch.digitToInt()
            ch == 'T' -> 10
            ch == 'J' -> if (withJokerRule) 1 else 11
            ch == 'Q' -> 12
            ch == 'K' -> 13
            ch == 'A' -> 14
            else -> throw IllegalArgumentException()
        }
    }

infix fun List<Int>.compareTo(other: List<Int>) =
    zip(other).map { (a, b) -> a compareTo b }.find { it != 0 } ?: 0

data class Hand(val cards: List<Int>) : Comparable<Hand> {
    constructor(str: String, withJokerRule: Boolean) : this(strToIntList(str, withJokerRule))

    private val typeStrength : Int
        get() {
            val cardsWithoutJokers = cards.filterNot { it == 1 }.ifEmpty { listOf(1, 1, 1, 1, 1) }
            val numJokers = 5 - cardsWithoutJokers.size
            val counts =
                cardsWithoutJokers
                    .groupingBy { it }
                    .eachCount()
                    .values
                    .sortedDescending()
                    .mapIndexed { // Treat the jokers as the currently most common card
                            index, count -> if (index == 0) count + numJokers else count
                    }.sortedDescending()
            return when (counts) {
                listOf(5) -> 7              // Five of a kind
                listOf(4, 1) -> 6           // Four of a kind
                listOf(3, 2) -> 5           // Full house
                listOf(3, 1, 1) -> 4        // Three of a kind
                listOf(2, 2, 1) -> 3        // Two pair
                listOf(2, 1, 1, 1) -> 2     // One pair
                listOf(1, 1, 1, 1, 1) -> 1  // High card
                else -> throw IllegalArgumentException()
            }
        }

    override infix fun compareTo(other: Hand) : Int =
        when (val typeStrengthComparison = typeStrength compareTo other.typeStrength) {
            0 -> cards compareTo other.cards
            else -> typeStrengthComparison
        }
}

private fun strToBid(str: String, withJokerRule: Boolean) =
    str.trim().split(' ').filter { it.isNotBlank() }.let {
        Bid(Hand(it[0], withJokerRule), it[1].toInt())
    }

data class Bid(val hand: Hand, val bid: Int) : Comparable<Bid> {
    override infix fun compareTo(other: Bid) =
        hand compareTo other.hand
}

object Y2023D07 : Solution {

    private fun solve(input: String, withJokerRule: Boolean) =
        input
            .lineSequence()
            .map { strToBid(it, withJokerRule) }
            .sorted()
            .withIndex()
            .map { (it.index + 1) * it.value.bid }
            .sum()

    override fun partOne(input: String) = solve(input, withJokerRule = false)

    override fun partTwo(input: String) = solve(input, withJokerRule = true)
}
