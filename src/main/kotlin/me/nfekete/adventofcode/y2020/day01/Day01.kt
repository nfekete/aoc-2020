package me.nfekete.adventofcode.y2020.day01

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day01 {

    @JvmStatic
    fun main(args: Array<String>) {
        val year = 2020L
        val set = classpathFile("input.txt")
            .readLines()
            .map { it.toLong() }
            .toSet()

        set.findElementsWithSum(year)
            ?.run { printSumAndProduct() }

        set.mapNotNull { first ->
            set.findElementsWithSum(year - first)
                ?.let { it.prepend(first) }
        }.first().run { printSumAndProduct() }
    }

    private fun Set<Long>.findElementsWithSum(sum: Long) =
        find { contains(sum - it) }
            ?.let { it to (sum - it) }

    private fun <A, B, C> Pair<B, C>.prepend(element: A) = Triple(element, first, second)

    private fun Pair<Long, Long>.printSumAndProduct() {
        val (a, b) = this
        println("$a + $b = ${a + b}, $a * $b = ${a * b}")
    }

    private fun Triple<Long, Long, Long>.printSumAndProduct() {
        val (a, b, c) = this
        println("$a + $b + $c = ${a + b + c}, $a * $b * c = ${a * b * c}")
    }
}
