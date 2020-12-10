package me.nfekete.adventofcode.y2020.day10

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { it.toInt() }
        val extendedInput = (listOf(0) + input.sorted() + (3 + input.maxOrNull()!!))
        val deltas = extendedInput.windowed(2, 1, partialWindows = false).map { (a, b) -> b - a }
        println("Extended input: $extendedInput")
        println("Joltage differences when applied in increased order: $deltas")
        println("Sum of the joltage differences: ${deltas.sum()}")
        (deltas.count { it == 1 } to deltas.count { it == 3 })
            .let { (a, b) -> println("Nr of 1-differences * Nr of 3 differences: $a * $b = ${a * b}") }

        val numberOfArrangements = extendedInput.numberOfArrangements()
        println(numberOfArrangements)
    }
}

private fun List<Int>.numberOfArrangements(): Long {
    val set = toSet()
    val max = maxOrNull()!!
    val dp = Array(max + 1) { 0L }.also { it[0] = 1 }
    (1..max).forEach { index ->
        val a = if (index - 1 in set) dp[index - 1] else 0
        val b = if (index - 2 in set) dp[index - 2] else 0
        val c = if (index - 3 in set) dp[index - 3] else 0
        dp[index] = a + b + c
    }
    return dp[max]
}
