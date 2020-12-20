package me.nfekete.adventofcode.y2020.day20

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.splitByDelimiter
import me.nfekete.adventofcode.y2020.common.translate
import kotlin.math.roundToInt
import kotlin.math.sqrt

private fun <T : Any> List<T>.rotate() = drop(1) + first()
fun Int.flip(bits: Int = 10): Int {
    val msbMask = 1 shl (bits - 1)
    var orig = this
    var result = 0
    for (i in 1..bits) {
        result = result shr 1 or (orig and msbMask)
        orig = orig shl 1
    }
    return result
}

private fun List<Int>.flipV() = listOf(get(0), get(1).flip(), get(2), get(3).flip())
private fun List<Int>.flipH() = listOf(get(0).flip(), get(1), get(2).flip(), get(3))

data class Tile(val id: Long, val sides: List<Int>) {

    fun orientations(): Set<Tile> =
        generateSequence(sides, List<Int>::rotate).take(4)
            .flatMap { sequenceOf(it, it.flipV(), it.flipH(), it.flipV().flipH()) }.map { Tile(id, it) }
            .toSet()

    companion object {
        private fun extractSides(lines: List<String>): List<Int> {
            val binLines = lines.map { it.translate('#' to '1', '.' to '0') }
            val v1 = binLines.first().toInt(2)
            val v2 = binLines.map { it.last() }.joinToString("").toInt(2)
            val v3 = binLines.last().toInt(2)
            val v4 = binLines.map { it.first() }.joinToString("").toInt(2)
            return listOf(v1, v2, v3, v4)
        }

        fun from(lines: List<String>): Tile =
            Tile(
                lines.first().splitByDelimiter(' ').second.trimEnd(':').toLong(),
                extractSides(lines.drop(1))
            )
    }
}

fun findEdgeTiles(tiles: List<Tile>) {
    val mapBySide = tiles.flatMap { tile ->
        tile.orientations().flatMap { orientation -> orientation.sides }.map { side -> side to tile.id }
    }
        .groupBy { it.first }
        .mapValues { (_, list) -> list.map { it.second }.toSet() }
    // sides that belong to a single tile - these must belong to tiles that are on the edge
    val sidesWithSingleTiles = mapBySide.filter { (_, set) -> set.size == 1 }.mapValues { (_, set) -> set.single() }
    mapBySide.forEach(::println)
    // among the tiles that are around the edge, some are associated with 4 such edge patterns
    // (2 edges patterns flipped), some with only 2 (1 edge pattern flipped)
    // the ones with 4 (2 flipped) such edges are the corners
    val tilesWithCounts = sidesWithSingleTiles.values.groupingBy { it }.eachCount()
    tilesWithCounts.forEach(::println)
    val cornerIds = tilesWithCounts.filter { (_, count) -> count == 4 }.map { (k, _) -> k }
    cornerIds.onEach(::println)
        .reduce { acc, l -> acc * l }.also { println("Product of corner ids: $it") }
}

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val tiles = classpathFile("input.txt").lineSequence().chunkBy { it.isBlank() }.map(Tile::from).toList()
        findEdgeTiles(tiles)
    }
}
