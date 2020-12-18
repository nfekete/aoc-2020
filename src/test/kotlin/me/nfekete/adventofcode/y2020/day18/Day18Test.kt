package me.nfekete.adventofcode.y2020.day18

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

internal class Day18Test : FunSpec({
    context("part1 provided examples should pass") {
        forAll(
            row("1 + 2 * 3 + 4 * 5 + 6", 71),
            row("1 + (2 * 3) + (4 * (5 + 6))", 51),
            row("2 * 3 + (4 * 5)", 26),
            row("5 + (8 * 3 + 9 + 3 * 4 * 3)", 437),
            row("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 12240),
            row("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 13632),
        ) { expression, expected ->
            Day18.part1(expression) shouldBe expected
        }
    }
    context("part2 provided examples should pass") {
        forAll(
            row("1 + 2 * 3 + 4 * 5 + 6", 231),
            row("1 + (2 * 3) + (4 * (5 + 6))", 51),
            row("2 * 3 + (4 * 5)", 46),
            row("5 + (8 * 3 + 9 + 3 * 4 * 3)", 1445),
            row("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 669060),
            row("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 23340),
        ) { expression, expected ->
            Day18.part2(expression) shouldBe expected
        }
    }
})
