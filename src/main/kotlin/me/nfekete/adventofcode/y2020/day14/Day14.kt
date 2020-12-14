package me.nfekete.adventofcode.y2020.day14

import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.translate

sealed class Instruction
data class SetMask(val mask: String) : Instruction()
data class SetMemory(val address: Long, val value: Long) : Instruction()

data class Mask1(val andMask: Long, val orMask: Long) {
    fun apply(value: Long) = value.and(andMask).or(orMask)

    companion object {
        fun parse(input: String) = input.removePrefix("mask = ").run {
            Mask1(
                replace('X', '1').toLong(2),
                replace('X', '0').toLong(2)
            )
        }
    }
}

data class Mask2(val mask: String) {
    fun apply(state: State2, address: Long, value: Long) {
        fun recurse(mask: String) {
            if (mask.contains('X')) {
                recurse(mask.replaceFirst('X', 'A'))
                recurse(mask.replaceFirst('X', 'B'))
            } else {
                val translatedMask = Mask1.parse(mask.translate('0' to 'X', 'A' to '0', 'B' to '1'))
                val actualAddress = translatedMask.apply(address)
                state.memory[actualAddress] = value
            }
        }
        recurse(mask)
    }

    companion object {
        fun parse(input: String) = Mask2(input.removePrefix("mask = "))
    }
}

class State1 {
    lateinit var mask: Mask1
    val memory: MutableMap<Long, Long> = mutableMapOf()
}

class State2 {
    lateinit var mask: Mask2
    val memory: MutableMap<Long, Long> = mutableMapOf()
}

interface Interpreter<S> {
    val initialState: S
    fun execute(state: S, instruction: Instruction): S
    fun run(lines: Iterable<String>) = lines.map { it.parseInstruction() }
        .fold(initialState) { state, instruction -> execute(state, instruction) }
}

class Part1 : Interpreter<State1> {
    override val initialState = State1()
    override fun execute(state: State1, instruction: Instruction): State1 {
        when (instruction) {
            is SetMask -> state.mask = Mask1.parse(instruction.mask)
            is SetMemory -> state.memory[instruction.address] = state.mask.apply(instruction.value)
        }
        return state
    }
}

class Part2 : Interpreter<State2> {
    override val initialState = State2()
    override fun execute(state: State2, instruction: Instruction): State2 {
        when (instruction) {
            is SetMask -> state.mask = Mask2.parse(instruction.mask)
            is SetMemory -> state.mask.apply(state, instruction.address, instruction.value)
        }
        return state
    }
}

val memSetRegex = "^mem\\[(?<addr>\\d+)] ?= ?(?<value>\\d+)$".toRegex()
fun String.parseInstruction(): Instruction = when {
    startsWith("mem") -> memSetRegex.matchEntire(this)?.groups?.run {
        SetMemory(get("addr")!!.value.toLong(), get("value")!!.value.toLong())
    }!!
    startsWith("mask = ") -> SetMask(this.removePrefix("mask = "))
    else -> throw IllegalArgumentException("Unknown instruction: $this")
}

object Day14 {
    @JvmStatic
    fun main(args: Array<String>) {
        val lines = classpathFile("input.txt").readLines()
        Part1().run(lines).memory.values.sum().also(::println)
        Part2().run(lines).memory.values.sum().also(::println)
    }
}
