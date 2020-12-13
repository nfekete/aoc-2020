package me.nfekete.adventofcode.y2020.day13

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = classpathFile("input.txt").readLines()
        part1(lines)
        part2(lines[1])
    }

    fun part2(input: String): Long {
        val mapIndexed = input.split(",").mapIndexed(::Pair)
        val buses = mapIndexed.filter { it.second != "x" }.map { (a, b) -> a.toLong() to b.toLong() }.also {
            it.unzip().second.checkAllRelativePrimes()
        }
        val (time, _) = buses.fold(0L to 1L) { (time, step), (remainder, divisor) ->
            val nextTime = generateSequence(time) { time -> time + step }
                .first { time -> (time + remainder) % divisor == 0L }
            nextTime to step * divisor
        }
        println("Buses: $buses, time: $time")
        return time
    }

    private fun List<Long>.checkAllRelativePrimes() {
        val relativePrimes =
            flatMap { b1 -> map { b2 -> b1 to b2 } }
                .filter { (a, b) -> a != b }
                .all { (a, b) -> gcd(a, b) == 1L }
        if (!relativePrimes) throw IllegalStateException("Not all relative primes")
    }

    private fun gcd(a: Long, b: Long): Long =
        generateSequence(a to b) { (a, b) -> b to a % b }.first { (_, b) -> b == 0L }.first

    private fun part1(lines: List<String>) {
        val (earliestTime, busIds) = lines[0].toInt() to lines[1].split(",").filter { it != "x" }.map { it.toInt() }
        val earliestBus = busIds.map { id -> id to id - earliestTime % id }.minByOrNull { it.second }!!
        val (earliestBusId, waitTime) = earliestBus
        println("Earliest bus ID=$earliestBusId, wait time=$waitTime. Result=${earliestBusId * waitTime}")
    }
}
