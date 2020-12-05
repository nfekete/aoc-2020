package me.nfekete.adventofcode.y2020.day03

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day03 {

    data class State(val trees: Int, val x: Int, val y: Int) {
        companion object {
            val initial = State(0, 0, 0)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val slopeMap = classpathFile("input.txt").readLines()
        slopeMap.countTreesOnDescent(3, 1).also { println(it.trees) }   //part1
        listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)                              //part2
            .map { (right, down) -> slopeMap.countTreesOnDescent(right, down) }
            .map { it.trees.toLong() }
            .reduce { acc, i -> acc * i }
            .also { println(it) }
    }

    private fun List<String>.countTreesOnDescent(right: Int, down: Int) = chunked(down) { it.first() }
        .fold(State.initial, { state: State, line: String ->
            with(state) {
                State(
                    trees + line.hasTree(x).toInt(),
                    x + right,
                    y + 1
                )
            }
        })

    private fun String.hasTree(x: Int) = this[x % length] == '#'
    private fun Boolean.toInt() = if (this) 1 else 0
}
