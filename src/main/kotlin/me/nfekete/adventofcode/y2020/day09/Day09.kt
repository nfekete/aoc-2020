package me.nfekete.adventofcode.y2020.day09

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day09 {

    @JvmStatic
    fun main(args: Array<String>) {
        val spec = ("input.txt" to 25)
        val input = classpathFile(spec.first).readLines().map { it.toLong() }

        val firstInvalid = input.windowed(spec.second + 1)
            .filter { !it.checkValid() }
            .map { it.last() }
            .first()
        println("First invalid number: $firstInvalid")

        val sublistWithSameSum = input.findContiguousRangeWithSum(firstInvalid)
        println("Sublist: $sublistWithSameSum, sum: ${sublistWithSameSum.sum()}")
        val min = sublistWithSameSum.minOrNull()!!
        val max = sublistWithSameSum.maxOrNull()!!
        println("Sum min and max from sublist: $min + $max = ${min + max}")
    }

    private fun List<Long>.checkValid(): Boolean {
        val toCheck = last()
        val previousNumbers = subList(0, size - 1)
        val combinationsOfTwo = previousNumbers.indices.flatMap { a ->
            (a + 1 until previousNumbers.size).map { b ->
                previousNumbers[a] to previousNumbers[b]
            }
        }
        return combinationsOfTwo.any { (a, b) -> toCheck == a + b }
    }

    private fun List<Long>.findContiguousRangeWithSum(sum: Long): List<Long> {
        val sums = runningFold(0L) { acc, value -> acc + value }
        return indices.flatMap { a -> (a + 1 until size).map { b -> subList(a, b + 1) to sums[b + 1] - sums[a] } }
            .first { (_, listSum) -> sum == listSum }
            .first
    }

}
