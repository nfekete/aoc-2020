package me.nfekete.adventofcode.y2020.day20

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class Day20KtTest: FunSpec({
    test("Int.flip should flip bit order") {
        1.flip(2) shouldBe 2
        2.flip(2) shouldBe 1
        3.flip(8) shouldBe 192
        192.flip(8) shouldBe 3
        0.flip(10) shouldBe 0
        1.flip(10) shouldBe 512
        512.flip(10) shouldBe 1
    }
})
