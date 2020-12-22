package me.nfekete.adventofcode.y2020.day22

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.day22.Player.*

enum class Player { PLAYER1, PLAYER2 }
data class GameResult(val winner: Player, val deck: List<Int>)

object Day22 {

    private val List<Int>.gameScore get() = reversed().foldIndexed(0L) { index, acc, i -> acc + i * (index + 1) }

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
        return winning.gameScore
    }

    private fun recursiveGame(p1: List<Int>, p2: List<Int>): GameResult {
        var d1 = p1
        var d2 = p2
        val previousConfigurations = HashSet<Pair<List<Int>, List<Int>>>()
        while (d1.isNotEmpty() && d2.isNotEmpty()) {
            if (d1 to d2 in previousConfigurations) {
                return GameResult(winner = PLAYER1, d1)
            } else {
                previousConfigurations.add(d1 to d2)
                val c1 = d1.first()
                val c2 = d2.first()
                val winner = if (d1.size > c1 && d2.size > c2) {
                    recursiveGame(d1.drop(1).take(c1), d2.drop(1).take(c2)).winner
                } else {
                    if (c1 > c2) PLAYER1 else PLAYER2
                }
                if (winner == PLAYER1) {
                    d1 = d1.drop(1) + c1 + c2
                    d2 = d2.drop(1)
                } else {
                    d1 = d1.drop(1)
                    d2 = d2.drop(1) + c2 + c1
                }
            }
        }
        return (if (d1.isEmpty()) GameResult(PLAYER2, d2) else GameResult(PLAYER1, d1))
    }

    private fun part2(p1: List<Int>, p2: List<Int>): Long {
        return recursiveGame(p1, p2).deck.gameScore
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").lineSequence().chunkBy { it.isBlank() }
            .map { lines ->
                lines.drop(1).map { line -> line.toInt() }
            }.toList()
        part1(input[0], input[1]).let(::println)
        part2(input[0], input[1]).let(::println)
    }
}
