package me.nfekete.adventofcode.y2020.day13

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import me.nfekete.adventofcode.y2020.day13.Day13.part2

internal class Day13Test : FunSpec({
    context("provided examples should pass") {
        forAll(
            row("7,13,x,x,59,x,31,19", 1068781L),
            row("17,x,13,19", 3417L),
            row("67,7,59,61", 754018L),
            row("67,x,7,59,61", 779210L),
            row("67,7,x,59,61", 1261476L),
            row("1789,37,47,1889", 1202161486L),
        ) { busSpec, expected ->
            part2(busSpec) shouldBe expected
        }
    }
})
