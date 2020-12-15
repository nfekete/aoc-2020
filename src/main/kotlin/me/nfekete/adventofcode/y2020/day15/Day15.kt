package me.nfekete.adventofcode.y2020.day15

import me.nfekete.adventofcode.y2020.common.classpathFile

sealed class Occurrence
data class First(val round: Int) : Occurrence()
data class Repeated(val lastRound: Int, val roundBefore: Int) : Occurrence()

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLine().split(",").map(String::toInt)
        part1(input)
        part2(input)
    }

    fun part1(input: List<Int>): Int = iterate(input, 2020)
    fun part2(input: List<Int>): Int = iterate(input, 30000000)

    private fun iterate(input: List<Int>, rounds: Int): Int {
        val map = input
            .mapIndexed { index, num -> num to First(index) }
            .toMap(LinkedHashMap<Int, Occurrence>())
        var lastSpoken: Int = input.last()
        var round = map.size
        while (round < rounds) {
            val nextToSpeak = when (val occurrenceForLast = map[lastSpoken]!!) {
                is First -> 0
                is Repeated -> occurrenceForLast.lastRound - occurrenceForLast.roundBefore
            }
            val newEntry = map.newEntryFor(nextToSpeak, round)
            map[nextToSpeak] = newEntry
            round++
            lastSpoken = nextToSpeak
        }
        println(lastSpoken)
        return lastSpoken
    }

    private fun Map<Int, Occurrence>.newEntryFor(num: Int, round: Int) = this[num].let {
        when (it) {
            null -> First(round)
            is First -> Repeated(round, it.round)
            is Repeated -> Repeated(round, it.lastRound)
        }
    }
}
