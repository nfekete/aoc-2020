package me.nfekete.adventofcode.y2020.day13

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = classpathFile("input.txt").readLines()

        val (earliestTime, busIds) = lines[0].toInt() to lines[1].split(",").filter { it != "x" }.map { it.toInt() }
        val earliestBus = busIds.map { id -> id to id - earliestTime % id }.minByOrNull { it.second }!!
        val (earliestBusId, waitTime) = earliestBus
        println("Earliest bus ID=$earliestBusId, wait time=$waitTime. Result=${earliestBusId * waitTime}")
    }
}
