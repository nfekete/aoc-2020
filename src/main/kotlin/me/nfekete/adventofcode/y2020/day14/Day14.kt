package me.nfekete.adventofcode.y2020.day14

import me.nfekete.adventofcode.y2020.common.classpathFile

sealed class Instruction
data class Mask(val andMask: ULong, val orMask: ULong) : Instruction() {
    fun apply(value: ULong) = value.and(andMask).or(orMask)
}

data class SetMemory(val address: ULong, val value: ULong) : Instruction()
class State(var mask: Mask = Mask(0UL, 0UL), val memory: MutableMap<ULong, ULong> = mutableMapOf())

fun State.execute(instruction: Instruction) {
    when (instruction) {
        is Mask -> mask = instruction
        is SetMemory -> memory[instruction.address] = mask.apply(instruction.value)
    }
}

const val bits36 = 0b1111__1111_1111_1111_1111__1111_1111_1111_1111UL
fun String.parseMask(): Mask = removePrefix("mask = ").run {
    Mask(
        bits36.inv() or replace('X', '1').toULong(2),
        replace('X', '0').toULong(2)
    )
}

val memSetRegex = "^mem\\[(?<addr>\\d+)\\] ?= ?(?<value>\\d+)$".toRegex()
fun String.parseInstruction(): Instruction = when {
    startsWith("mem") -> memSetRegex.matchEntire(this)?.groups?.run {
        SetMemory(get("addr")!!.value.toULong(), get("value")!!.value.toULong())
    }!!
    startsWith("mask") -> this.parseMask()
    else -> throw IllegalArgumentException("Unknown instruction: $this")
}

object Day14 {
    @JvmStatic
    fun main(args: Array<String>) {
        val lines = classpathFile("input.txt").readLines()
        val mask = lines[0].parseMask()
//        println(mask)
        val state = State(mask)
        lines.drop(1).map { it.parseInstruction() }.forEach { state.execute(it) }
        state.memory.values.sum().also(::println)
    }
}
