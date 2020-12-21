package me.nfekete.adventofcode.y2020.day20

import me.nfekete.adventofcode.y2020.common.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun List<Boolean>.toInt() = fold(0) { acc, b -> acc shl 1 or if (b) 1 else 0 }
fun List<List<Boolean>>.rotate() = indices.map { row ->
    indices.map { column -> this[size - 1 - column][row] }
}

fun List<List<Boolean>>.flipV() = indices.map { row ->
    indices.map { column -> this[row][size - 1 - column] }
}

fun List<List<Boolean>>.flipH() = indices.map { row ->
    indices.map { column -> this[size - 1 - row][column] }
}

typealias TileFunction = (Tile) -> Tile

private fun TileFunction.compose(f: TileFunction) = { tile: Tile -> invoke(f(tile)) }
private fun identity(tile: Tile) = tile

data class Sides(val top: Int, val right: Int, val bottom: Int, val left: Int) {
    val all get() = listOf(top, right, bottom, left)
}

data class Tile(val id: Long, val bitmap: List<List<Boolean>>) {

    private fun extractSides(binLines: List<List<Boolean>>): Sides {
        val top = binLines.first().toInt()
        val right = binLines.map { it.last() }.toInt()
        val bottom = binLines.last().toInt()
        val left = binLines.map { it.first() }.toInt()
        return Sides(top, right, bottom, left)
    }

    val sides: Sides = extractSides(bitmap)

    fun orientations(): Set<Tile> {
        val rotations = generateSequence<TileFunction>(::identity) { it.compose(Tile::rotate90cw) }.take(4)
        val flips = sequenceOf(::identity, Tile::flipV, Tile::flipH)
        return rotations.flatMap { f ->
            flips.map { g -> f.compose(g) }
        }.map { function -> function.invoke(this) }.toSet()
    }

    private fun rotate90cw() = Tile(id, bitmap.rotate())
    private fun flipV() = Tile(id, bitmap.flipV())
    private fun flipH() = Tile(id, bitmap.flipH())

    fun possibleSideValues(): Set<Int> = orientations().flatMap { it.sides.all }.toSet()

    companion object {
        private fun binLines(lines: List<String>) =
            lines.map { it.translate('#' to '1', '.' to '0') }

        fun from(lines: List<String>): Tile {
            val binLines = binLines(lines.drop(1))
            return Tile(
                lines.first().splitByDelimiter(' ').second.trimEnd(':').toLong(),
                binLines.map { line -> line.map { ch -> ch == '1' } }
            )
        }
    }
}

private fun Collection<Tile>.sidePatternSetToTileMap(): Map<Int, Set<Tile>> {
    return flatMap { tile ->
        tile.possibleSideValues().map { side -> side to tile }
    }
        .groupBy { it.first }
        .mapValues { (_, list) -> list.map { it.second }.toSet() }
}

private fun Map<Int, Set<Tile>>.tilesWithNonSharedEdgePatterns(): Map<Tile, Int> {
    val sidesWithSingleTiles = filter { (_, set) -> set.size == 1 }.mapValues { (_, set) -> set.single() }
    // among the tiles that are around the edge, some are associated with 4 such edge patterns
    // (2 edges patterns flipped), some with only 2 (1 edge pattern flipped)
    // the ones with 4 (2 flipped) such edges are the corners
    return sidesWithSingleTiles.values.groupingBy { it }.eachCount()
}

private fun Map<Tile, Int>.filterToNonSharedCount(desiredCount: Int) =
    filter { (_, count) -> count == desiredCount }.map { (k, _) -> k }

fun findCornerTiles(tiles: List<Tile>): List<Tile> {
    val mapBySide = tiles.sidePatternSetToTileMap()
    // sides that belong to a single tile - these must belong to tiles that are on the edge
    val tilesWithCounts = mapBySide.tilesWithNonSharedEdgePatterns()
    return tilesWithCounts.filterToNonSharedCount(4).windowed(4).single() //assert the size
}

private fun Collection<Tile>.mapSize() = sqrt(count().toDouble()).roundToInt()

private fun validTileArrangements(corners: Collection<Tile>, tiles: Collection<Tile>): Sequence<List<Tile>> {
    val size = tiles.mapSize()
    val range = 0 until size

    fun tryFill(map: List<Tile>, set: Set<Tile>, index: Int): Sequence<List<Tile>> {
        fun Tile.rightSideNeighbors() = set.flatMap { it.orientations() }.filter { it.sides.left == sides.right }
        fun Tile.bottomSideNeighbors() = set.flatMap { it.orientations() }.filter { it.sides.top == sides.bottom }
        if (set.isEmpty()) return sequenceOf(map.toList())
        if (index !in tiles.indices) return sequenceOf(map.toList())
        val row = index / size
        val column = index % size
        val left = if (column - 1 in range) map[index - 1] else null
        val up = if (row - 1 in range) map[index - size] else null
        val candidates = when {
            up == null && left != null -> left.rightSideNeighbors()
            up != null && left == null -> up.bottomSideNeighbors()
            left != null && up != null -> left.rightSideNeighbors().intersect(up.bottomSideNeighbors())
            else -> corners.flatMap { it.orientations() }
        }
        return candidates.asSequence().flatMap { tryFill(map + it, set - it.orientations(), index + 1) }
    }
    return tryFill(listOf(), tiles.toSet(), 0)
}

data class Bitmap(val bits: List<List<Boolean>>) {
    val onBitsCount get() = bits.flatten().count { it }
    val size get() = bits.size
    fun isSet(row: Int, column: Int) = bits[row][column]
    fun findMonster(monster: Monster): List<Pair<Int, Int>> {
        val (height, width) = monster.mask.run { maxOfOrNull { it.first }!! + 1 to maxOfOrNull { it.second }!! + 1 }
        return (0..size - height).flatMap { row ->
            (0..size - width).mapNotNull { column ->
                val found = monster.mask.all { (drow, dcolumn) -> isSet(row + drow, column + dcolumn) }
                if (found) row to column else null
            }
        }
    }

    override fun toString(): String =
        bits.joinToString("\n") { row -> row.map { if (it) '#' else '.' }.joinToString("") }
}

private fun List<Tile>.toBitmap(): Bitmap {
    fun isSet(row: Int, column: Int): Boolean {
        val mapSize = mapSize()
        val tileRow = row / 8
        val tileColumn = column / 8
        val rowOffsetWithinTile = row % 8
        val columnOffsetWithinTile = column % 8
        val tileIndex = tileRow * mapSize + tileColumn
        return get(tileIndex).bitmap[1 + rowOffsetWithinTile][1 + columnOffsetWithinTile]
    }

    val size = mapSize() * 8
    return (0 until size).map { row ->
        (0 until size).map { column -> isSet(row, column) }
    }.let(::Bitmap)
}

data class Monster(val mask: List<Pair<Int, Int>>)

private fun List<String>.toMonster(): Monster = flatMapIndexed { row, line ->
    line.mapIndexedNotNull { column, char -> if (char == '#') row to column else null }
}.let(::Monster)

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val tiles = classpathFile("input.txt").lineSequence().chunkBy { it.isBlank() }.map(Tile::from).toList()
        findCornerTiles(tiles).map { it.id }.onEach(::println).product().also { println("Product of corner ids: $it") }

        val monster = classpathFile("monster.txt").readLines().toMonster()
        val bitmaps = validTileArrangements(findCornerTiles(tiles), tiles).map { it.toBitmap() }
        bitmaps.map { it to it.findMonster(monster) }
            .filter { it.second.isNotEmpty() }
            .map { it.map2 { list -> list.count() } }
            .onEach { println("${it.first}\n${it.second}\n") }
            .first().let {
                println(it.first.onBitsCount - it.second * monster.mask.size)
            }
    }
}
