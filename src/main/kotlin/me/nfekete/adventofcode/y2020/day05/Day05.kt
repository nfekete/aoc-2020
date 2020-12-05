package me.nfekete.adventofcode.y2020.day05

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day05 {

    @JvmStatic
    fun main(args: Array<String>) {
        val seatIds = classpathFile("input.txt").readLines().map { it.translateToBinary() }
        val minSeatNr = seatIds.minOrNull()!!
        val maxSeatNr = seatIds.maxOrNull()!!
        println("Max seat index: $maxSeatNr")
        (minSeatNr until maxSeatNr).singleOrNull { it !in seatIds }
            ?.let { println("Your seat index: $it") }
            ?: println("No empty seats or multiple empty seats found")
    }

    private fun String.translateToBinary() =
        fold(0) { acc, char ->
            when (char) {
                'B', 'R' -> acc shl 1 or 1
                else -> acc shl 1
            }
        }
}
