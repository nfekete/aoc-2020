package me.nfekete.adventofcode.y2020.day12

import me.nfekete.adventofcode.y2020.common.classpathFile
import kotlin.math.absoluteValue

enum class Heading(val x: Int, val y: Int) {
    E(+1,  0),
    S( 0, -1),
    W(-1,  0),
    N( 0, +1),
}

sealed class Instruction
sealed class Move(val amount: Int) : Instruction()
class North(amount: Int) : Move(amount)
class South(amount: Int) : Move(amount)
class East(amount: Int) : Move(amount)
class West(amount: Int) : Move(amount)
class Forward(amount: Int) : Move(amount)
sealed class Turn(val degrees: Int) : Instruction()
class Left(degrees: Int) : Turn(360 - degrees)
class Right(degrees: Int) : Turn(degrees)

data class Position(val x: Int, val y: Int, val heading: Heading) {
    private fun move(amount: Int, direction: Heading): Position =
        copy(x = x + direction.x * amount, y = y + direction.y * amount)

    private fun turn(turn: Turn): Position {
        assert(turn.degrees % 90 == 0) { "Only right angles allowed" }
        val currentDirectionIndex = heading.ordinal
        val quarterTurns = turn.degrees / 90
        val nrOfHeadings = Heading.values().size
        val newHeading = Heading.values()[(nrOfHeadings + currentDirectionIndex + quarterTurns) % nrOfHeadings]
        return copy(heading = newHeading)
    }

    fun update(instruction: Instruction): Position =
        when (instruction) {
            is North -> move(instruction.amount, Heading.N)
            is East -> move(instruction.amount, Heading.E)
            is West -> move(instruction.amount, Heading.W)
            is South -> move(instruction.amount, Heading.S)
            is Forward -> move(instruction.amount, heading)
            is Left -> turn(instruction)
            is Right -> turn(instruction)
        }
}

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { it.parseInstruction() }.toList()
        val (x, y) = input.fold(Position(0, 0, Heading.E), { position, instruction -> position.update(instruction) })
        println("New position: x=$x, y=$y, Manhattan distance=${x.absoluteValue + y.absoluteValue}")
    }

    private fun String.parseInstruction(): Instruction {
        val (mnemonic, amount) = first() to drop(1).toInt()
        return when (mnemonic) {
            'N' -> North(amount)
            'S' -> South(amount)
            'E' -> East(amount)
            'W' -> West(amount)
            'F' -> Forward(amount)
            'L' -> Left(amount)
            'R' -> Right(amount)
            else -> throw IllegalArgumentException("Invalid instruction: $this")
        }
    }
}
