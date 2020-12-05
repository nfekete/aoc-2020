package me.nfekete.adventofcode.y2020.day05

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day05 {

    @JvmStatic
    fun main(args: Array<String>) {
        val seatIds = classpathFile("input.txt").readLines()
            .map { it.translateToBinary() }
            .map { it.toInt(2) }
        val maxSeatNr = seatIds
            .maxOrNull()!!
        println("Max seat index: $maxSeatNr")
        (1 until maxSeatNr).filter { it - 1 in seatIds && it + 1 in seatIds && it !in seatIds }
            .forEach { println("Your seat index: $it") }
    }

    private fun String.translateToBinary() =
        replace('B', '1')
            .replace('F', '0')
            .replace('R', '1')
            .replace('L', '0')

}
