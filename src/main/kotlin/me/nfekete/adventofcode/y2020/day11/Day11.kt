package me.nfekete.adventofcode.y2020.day11

import me.nfekete.adventofcode.y2020.common.classpathFile

private val FLOOR = '.'
private val SEAT_EMPTY = 'L'
private val SEAT_OCCUPIED = '#'

data class SeatMap(val grid: Array<CharArray>) {
    private val maxRows = grid.size
    private val maxColumns = grid.first().size
    private val rows = 0 until maxRows
    private val columns = 0 until maxColumns
    override fun toString() = grid.joinToString("\n") { row -> row.joinToString("") }

    private fun Array<CharArray>.copy() = Array(size) { row -> this[row].copyOf() }

    private fun occupiedAdjacent(row: Int, column: Int): Int {
        var occupied = 0
        for (y in (row - 1).coerceAtLeast(0)..(row + 1).coerceAtMost(maxRows - 1)) {
            for (x in (column - 1).coerceAtLeast(0)..(column + 1).coerceAtMost(maxColumns - 1)) {
                if (y == row && x == column) {
                    continue
                }
                if (grid[y][x] == SEAT_OCCUPIED) {
                    occupied++
                }
            }
        }
        return occupied
    }

    private fun makeRound(): Pair<Boolean, SeatMap> {
        val newGrid = grid.copy()
        var changed = false
        for (row in rows) {
            for (column in columns) {
                val square = grid[row][column]
                val occupiedAdjacents = occupiedAdjacent(row, column)
                when {
                    square == SEAT_EMPTY && occupiedAdjacents == 0 -> {
                        newGrid[row][column] = SEAT_OCCUPIED
                        changed = true
                    }
                    square == SEAT_OCCUPIED && occupiedAdjacents >= 4 -> {
                        newGrid[row][column] = SEAT_EMPTY
                        changed = true
                    }
                }
            }
        }
        return changed to SeatMap(newGrid)
    }

    fun simulate(): SeatMap {
        val stableGrid = generateSequence(true to this) { (changed, newSeatMap) -> newSeatMap.makeRound() }
            .takeWhile { (changed, _) -> changed }
            .last().second
        return stableGrid
    }

    fun occupiedSeats(): Int = grid.sumOf { row -> row.count { it == SEAT_OCCUPIED } }
}

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines()
            .map { line -> line.toCharArray() }
            .let { SeatMap(it.toTypedArray()) }

        val finalGrid = input.simulate()
        println(finalGrid)
        val occupiedSeats = finalGrid.occupiedSeats()
        println("Occupied seats in grid: $occupiedSeats")
    }

}
