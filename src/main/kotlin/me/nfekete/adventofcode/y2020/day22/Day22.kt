package me.nfekete.adventofcode.y2020.day22

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile

object Day22 {

    private fun part1(p1: List<Int>, p2: List<Int>): Long {
        val winning = generateSequence(p1 to p2) { (d1, d2) ->
            val c1 = d1.first()
            val c2 = d2.first()
            if (c1 > c2) {
                d1.drop(1) + c1 + c2 to d2.drop(1)
            } else {
                d1.drop(1) to d2.drop(1) + c2 + c1
            }
        }.first { (d1, d2) -> d1.isEmpty() || d2.isEmpty() }
            .let { (d1, d2) -> if (d1.isEmpty()) d2 else d1 }
        println(winning)
        return winning.reversed().foldIndexed(0L) { index, acc, i -> acc + i * (index + 1) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        listOf(
            "sample.txt",
            "input.txt",
        ).forEach { filename ->
            val chunkBy = classpathFile(filename).lineSequence().chunkBy { it.isBlank() }
            val input = chunkBy
                .map { it.drop(1).map { it.toInt() } }.toList()
            val d1 = input[0]
            val d2 = input[1]
            part1(d1, d2).let(::println)
        }
    }
}
