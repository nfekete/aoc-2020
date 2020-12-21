package me.nfekete.adventofcode.y2020.day20

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class Day20KtTest: FunSpec({
    test("List<Boolean>.toInt() should convert Boolean array to Int") {
        listOf(false, false).toInt() shouldBe 0
        listOf(false, true).toInt() shouldBe 1
        listOf(true, false).toInt() shouldBe 2
        listOf(true, true).toInt() shouldBe 3
    }
})
