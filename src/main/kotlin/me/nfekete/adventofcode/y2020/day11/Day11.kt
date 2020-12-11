package me.nfekete.adventofcode.y2020.day11

import me.nfekete.adventofcode.y2020.common.classpathFile

private const val FLOOR = '.'
private const val SEAT_EMPTY = 'L'
private const val SEAT_OCCUPIED = '#'
private val Char.isOccupied get() = this == SEAT_OCCUPIED
private val Char.isFloor get() = this == FLOOR
private val directions = (-1..1).flatMap { dy ->
    (-1..1).map { dx -> dy to dx }
}.filter { (dy, dx) -> !(dy == 0 && dx == 0) }

interface SimulationStrategy {
    fun surroundingOccupancy(seatMap: SeatMap, row: Int, column: Int): Int
    fun shouldFreeSeat(surroundingOccupancy: Int): Boolean
}

class Part1 : SimulationStrategy {
    override fun surroundingOccupancy(seatMap: SeatMap, row: Int, column: Int): Int =
        directions.map { (dy, dx) -> row + dy to column + dx }
            .filter { (row, column) -> row in seatMap.rows && column in seatMap.columns }
            .map { (row, column) -> seatMap.grid[row][column] }
            .count { it.isOccupied }

    override fun shouldFreeSeat(surroundingOccupancy: Int): Boolean = surroundingOccupancy >= 4
}

class Part2 : SimulationStrategy {
    override fun surroundingOccupancy(seatMap: SeatMap, row: Int, column: Int): Int {
        return directions.map { (dy, dx) -> //in all directions
            generateSequence(row to column) { (y, x) -> y + dy to x + dx }
                .drop(1) // the first element is the square itself
                .takeWhile { (y, x) -> y in seatMap.rows && x in seatMap.columns } //stay within the grid
                .map { (y, x) -> seatMap.grid[y][x] } //what do we see there?
                .firstOrNull { !it.isFloor } //we don't see through objects
        }.count { it?.isOccupied ?: false } //count the number of occupied seats we see
    }

    override fun shouldFreeSeat(surroundingOccupancy: Int): Boolean = surroundingOccupancy >= 5
}

class SeatMap(val grid: Array<CharArray>) {
    internal val maxRows = grid.size
    internal val maxColumns = grid.first().size
    internal val rows = 0 until maxRows
    internal val columns = 0 until maxColumns
    override fun toString() = grid.joinToString("\n") { row -> row.joinToString("") }

    private fun Array<CharArray>.copy() = Array(size) { row -> this[row].copyOf() }

    private fun makeRound(simulationStrategy: SimulationStrategy): Pair<Boolean, SeatMap> = rows.map { row ->
        columns.map { column ->
            val square = grid[row][column]
            val surroundingOccupancy = simulationStrategy.surroundingOccupancy(this, row, column)
            when {
                square == SEAT_EMPTY && surroundingOccupancy == 0 -> SEAT_OCCUPIED to true
                square == SEAT_OCCUPIED && simulationStrategy.shouldFreeSeat(surroundingOccupancy) -> SEAT_EMPTY to true
                else -> square to false
            }
        }.unzip().let { (newRow, changed) -> newRow.toCharArray() to changed.any { it } }
    }.unzip().let { (newGrid, changed) -> changed.any { it } to SeatMap(newGrid.toTypedArray()) }

    fun simulate(simulationStrategy: SimulationStrategy): SeatMap {
        return generateSequence(true to this) { (_, newSeatMap) -> newSeatMap.makeRound(simulationStrategy) }
            .takeWhile { (changed, _) -> changed }
            .last().second
    }

    fun occupiedSeats(): Int = grid.sumOf { row -> row.count { it == SEAT_OCCUPIED } }

    companion object {
        fun read(filename: String) = classpathFile(filename).readLines()
            .map { line -> line.toCharArray() }
            .let { SeatMap(it.toTypedArray()) }
    }
}

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = SeatMap.read("input.txt")

        val occupiedSeatsPart1 = input.simulate(Part1()).occupiedSeats()
        println("Occupied seats in grid part 1: $occupiedSeatsPart1")

        val occupiedSeatsPart2 = input.simulate(Part2()).occupiedSeats()
        println("Occupied seats in grid part 2: $occupiedSeatsPart2")
    }
}
