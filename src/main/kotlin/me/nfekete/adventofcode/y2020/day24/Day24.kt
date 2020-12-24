package me.nfekete.adventofcode.y2020.day24

import me.nfekete.adventofcode.y2020.common.classpathFile

private enum class Direction(private val x: Int, private val y: Int) {
    E(1, 0), SE(0, 1), SW(-1, 1), W(-1, 0), NW(0, -1), NE(1, -1);

    val coordinate get() = Coordinate(x, y)
}

private data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)

    companion object {
        val origin = Coordinate(0, 0)
    }
}

private val regex = Direction.values().joinToString("|") { it.name.toLowerCase() }.toRegex()

private fun String.readDirections(): Sequence<Direction> =
    regex.findAll(this).map { Direction.valueOf(it.value.toUpperCase()) }

private fun Set<Coordinate>.iterate(): Set<Coordinate> {
    val potentialCoordinates = flatMap { coordinate -> Direction.values().map { coordinate + it.coordinate } }.toSet()
    return potentialCoordinates.mapNotNull { coordinate ->
        val isBlack = contains(coordinate)
        val neighbors = Direction.values().map { coordinate + it.coordinate }.count { contains(it) }
        val newBlack = when {
            isBlack && (neighbors == 0 || neighbors > 2) -> false
            !isBlack && neighbors == 2 -> true
            else -> isBlack
        }
        coordinate.takeIf { newBlack }
    }.toSet()
}

private object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { it.readDirections().toList() }

        val tiles = input.map { directions ->
            directions.fold(Coordinate.origin) { acc, direction -> acc + direction.coordinate }
        }.fold(mutableMapOf<Coordinate, Boolean>()) { acc, coordinate ->
            acc.merge(coordinate, true) { old, new -> old xor new }
            acc
        }.filterValues { it }.also { println(it.size) }

        generateSequence(tiles.keys, Set<Coordinate>::iterate).drop(100).first().size.also(::println)
    }
}
