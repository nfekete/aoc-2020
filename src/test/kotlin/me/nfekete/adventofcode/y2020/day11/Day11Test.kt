package me.nfekete.adventofcode.y2020.day11

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11Test {

    @Test
    fun testPart2Setup() {
        assertEquals(8, Part2().surroundingOccupancy(SeatMap.read("test1.txt"), 4, 3))
        assertEquals(0, Part2().surroundingOccupancy(SeatMap.read("test2.txt"), 1, 1))
        assertEquals(1, Part2().surroundingOccupancy(SeatMap.read("test2.txt"), 1, 3))
    }

}
