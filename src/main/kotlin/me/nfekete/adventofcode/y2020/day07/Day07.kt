package me.nfekete.adventofcode.y2020.day07

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day07 {

    private val colorExtractorRegex = "^((?<number>\\d+) )?(?<color>\\p{Lower}+ \\p{Lower}+) bags?\\.?$".toRegex()

    data class ColorAndNumber(val color: String, val number: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        val ourColor = "shiny gold"

        val mandatoryContainment = classpathFile("input.txt").readLines()
            .map { line -> line.split("contain", limit = 2) }
            .map { array ->
                val key = array[0].trim().extractColor()
                val values = array[1].trim().split(", ?".toRegex()).mapNotNull { it.extractColorAndNumber() }
                key to values.toSet()
            }.toMap()

        val allowedContainment = mandatoryContainment.flatMap { (key, values) ->
            values.map { it.color to key }
        }.groupBy({ (key, _) -> key }, { (_, value) -> value })
            .mapValues { (_, value) -> value.toSet() }

        println(allowedContainment.possibleContainers(ourColor).size)
        println(mandatoryContainment.countNumberOfBags(ourColor))
    }

    private fun String.extractColor() =
        colorExtractorRegex.matchEntire(this)?.groups?.get("color")?.value!!

    private fun String.extractColorAndNumber() =
        colorExtractorRegex.matchEntire(this)?.groups?.run {
            val color = get("color")?.value!!
            val numberString = get("number")?.value
            if (numberString != null) {
                ColorAndNumber(color, numberString.toInt())
            } else {
                null
            }
        }

    private fun Map<String, Set<String>>.possibleContainers(start: String, includeStart: Boolean = false): Set<String> =
        getOrDefault(start, emptySet()).map { color ->
            possibleContainers(color, true)
        }.fold(if (includeStart) setOf(start) else emptySet()) { a, b ->
            a.union(b)
        }

    private fun Map<String, Set<ColorAndNumber>>.countNumberOfBags(start: String, includeStart: Boolean = false): Int =
        getOrDefault(start, emptySet()).map { (color, number) ->
            number * countNumberOfBags(color, true)
        }.sum() + if (includeStart) 1 else 0
}
