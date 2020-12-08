package me.nfekete.adventofcode.y2020.day08

import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.day08.Mnemonic.*

enum class Mnemonic { nop, acc, jmp }
fun Mnemonic.canBeFlipped() = this == nop || this == jmp
data class Instruction(val mnemonic: Mnemonic, val argument: Int) {
    fun traced() = TraceableInstruction(this, false)
    fun flip() = when (mnemonic) {
        nop -> copy(mnemonic = jmp)
        jmp -> copy(mnemonic = nop)
        acc -> this
    }
}
data class TraceableInstruction(val instruction: Instruction, var executed: Boolean)
typealias Program = List<Instruction>
typealias TraceableProgram = List<TraceableInstruction>
fun Program.traced() = map { it.traced() }

data class CpuSimulator(
    var accumulator: Int,
    var instructionPointer: Int,
    val traceableProgram: TraceableProgram
) {
    constructor(program: Program) : this(0, 0, program.traced())

    private fun executeCurrentInstruction() {
        traceableProgram[instructionPointer].executed = true
        val (mnemonic, argument) = traceableProgram[instructionPointer].instruction
        when (mnemonic) {
            nop -> instructionPointer++
            acc -> {
                accumulator += argument
                instructionPointer++
            }
            jmp -> instructionPointer += argument
        }
    }

    val infiniteLoopDetected: Boolean get() = instructionPointer < traceableProgram.size && traceableProgram[instructionPointer].executed
    val terminatedNormally: Boolean get() = instructionPointer == traceableProgram.size
    fun simulate() = this.apply {
        while (!infiniteLoopDetected && !terminatedNormally) {
            executeCurrentInstruction()
        }
    }
}

private fun String.parseLine() = split(' ', limit = 2).let { Instruction(Mnemonic.valueOf(it[0]), it[1].toInt()) }
private fun Program.mutations() =
    indices.filter { this[it].mnemonic.canBeFlipped() }.map { index -> flipInstruction(index) }

private fun Program.flipInstruction(indexToFlip: Int) =
    mapIndexed { index, instruction -> if (index == indexToFlip) instruction.flip() else instruction }

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines()
            .map { it.parseLine() }
            .toList()
        input.forEach { println(it) }
        println(CpuSimulator(input).simulate().accumulator)

        val mutations = input.mutations()
        val accumulator = mutations.map {
            CpuSimulator(it).simulate()
        }.filter {
            it.terminatedNormally
        }.map { it.accumulator }.single()
        println(accumulator)
    }

}
