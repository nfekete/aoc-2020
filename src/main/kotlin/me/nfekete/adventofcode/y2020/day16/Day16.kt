package me.nfekete.adventofcode.y2020.day16

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.map2
import me.nfekete.adventofcode.y2020.common.splitByDelimiter

data class Rule(val name: String, val ranges: List<ClosedRange<Int>>)

private fun Rule.validate(value: Int) = ranges.any { range -> range.contains(value) }
private fun List<Rule>.validate(value: Int) = any { rule -> rule.validate(value) }
private fun String.parseTicket() = split(",").map(String::toInt)
private fun parseRule(line: String) = line.splitByDelimiter(": ").map2 { ranges ->
    ranges.split(" or ").map { range -> range.splitByDelimiter('-') }.map { (from, to) -> from.toInt()..to.toInt() }
}.let { (name, ranges) -> Rule(name, ranges) }

private fun List<List<Int>>.column(column: Int) = map { it[column] }

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val it = classpathFile("input.txt").readLines().asSequence().chunkBy { it.isBlank() }.iterator()
        val rules = it.next().map(::parseRule)
        val myTicket = it.next().drop(1).first().parseTicket()
        val otherTickets = it.next().drop(1).map(String::parseTicket)

        part1(rules, otherTickets).let(::println)
        part2(rules, myTicket, otherTickets).let(::println)
    }

    private fun part1(rules: List<Rule>, otherTickets: List<List<Int>>): Int =
        otherTickets.flatten().filter { value -> !rules.validate(value) }.sum()

    private fun part2(rules: List<Rule>, myTicket: List<Int>, otherTickets: List<List<Int>>): Long {
        val validTickets = otherTickets.filter { values -> values.all { value -> rules.validate(value) } }

        val columns = validTickets.first().indices
        val rulesWithPossibleColumns = rules.map { rule ->
            rule to columns.filter { columnIndex ->
                val column = validTickets.column(columnIndex)
                column.all { rule.validate(it) }
            }
        }.sortedBy { it.second.size }
        val columnsWithMatchingRules = mutableSetOf<Int>()
        val rulesToColumnsMap =
            rulesWithPossibleColumns.map { (rule, columns) ->
                val pair = rule to (columns.toSet() - columnsWithMatchingRules)
                columnsWithMatchingRules.addAll(columns)
                pair
            }.map { it.map2 { set -> set.single() } }
        val departureFields = rulesToColumnsMap.filter { (rule, _) -> rule.name.startsWith("departure") }
        departureFields.forEach(::println)
        return departureFields.map { (_, column) -> myTicket[column] }.fold(1L) { a, b -> a * b }
    }
}
