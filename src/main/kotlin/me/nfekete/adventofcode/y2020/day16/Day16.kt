package me.nfekete.adventofcode.y2020.day16

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.map2
import me.nfekete.adventofcode.y2020.common.splitByDelimiter

data class Rule(val name: String, val ranges: List<ClosedRange<Int>>)

fun Rule.validate(value: Int) = ranges.any { range -> range.contains(value) }
fun List<Rule>.validate(value: Int) = any { rule -> rule.validate(value) }

fun parseRule(line: String) = line.splitByDelimiter(": ").map2 { ranges ->
    ranges.split(" or ").map { range -> range.splitByDelimiter('-') }.map { (from, to) -> from.toInt()..to.toInt() }
}.let { (name, ranges) -> Rule(name, ranges) }

fun String.parseTicket() = split(",").map(String::toInt)

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val it = classpathFile("sample.txt").readLines().asSequence().chunkBy { it.isBlank() }.iterator()
        val rules = it.next().map(::parseRule)
        val myTicket = it.next().drop(1).first().parseTicket()
        val otherTickets = it.next().drop(1).map(String::parseTicket)

        part1(rules, myTicket, otherTickets).let(::println)
    }

    private fun part1(rules: List<Rule>, myTicket: List<Int>, otherTickets: List<List<Int>>): Int =
        otherTickets.flatten().filter { value -> !rules.validate(value) }.sum()
}
