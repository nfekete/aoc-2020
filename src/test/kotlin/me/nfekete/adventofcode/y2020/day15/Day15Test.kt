package me.nfekete.adventofcode.y2020.day15

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import me.nfekete.adventofcode.y2020.day15.Day15.part1
import me.nfekete.adventofcode.y2020.day15.Day15.part2
import org.junit.jupiter.api.Assertions.*

internal class Day15Test : FunSpec({
    context("part1 provided examples should pass") {
        forAll(
            row(listOf(1, 3, 2), 1),
            row(listOf(2, 1, 3), 10),
            row(listOf(1, 2, 3), 27),
            row(listOf(2, 3, 1), 78),
            row(listOf(3, 2, 1), 438),
            row(listOf(3, 1, 2), 1836),
        ) { list, expected ->
            part1(list) shouldBe expected
        }
    }

    context("part2 provided examples should pass") {
        forAll(
            row(listOf(0,3,6), 175594),
            row(listOf(1,3,2), 2578),
            row(listOf(2,1,3), 3544142),
            row(listOf(1,2,3), 261214),
            row(listOf(2,3,1), 6895259),
            row(listOf(3,2,1), 18),
        ) { list, expected ->
            part2(list) shouldBe expected
        }
    }
})







