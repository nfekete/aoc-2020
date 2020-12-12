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

interface Coords {
    val x: Int
    val y: Int
    operator fun component1() = x
    operator fun component2() = y
}

data class PositionAndHeading(override val x: Int, override val y: Int, val heading: Heading) : Coords
data class PositionAndWaypoint(override val x: Int, override val y: Int, val wpx: Int, val wpy: Int) : Coords

interface Interpreter<S> {
    fun move(state: S, amount: Int, direction: Heading): S
    fun forward(state: S, amount: Int): S
    fun turn(state: S, turn: Turn): S
    fun update(state: S, instruction: Instruction): S =
        when (instruction) {
            is North -> move(state, instruction.amount, Heading.N)
            is East -> move(state, instruction.amount, Heading.E)
            is West -> move(state, instruction.amount, Heading.W)
            is South -> move(state, instruction.amount, Heading.S)
            is Forward -> forward(state, instruction.amount)
            is Turn -> turn(state, instruction)
        }

    fun run(initialState: S, instructions: List<Instruction>): S =
        instructions.fold(initialState, { state, instruction -> update(state, instruction) })
}

class Part1 : Interpreter<PositionAndHeading> {
    override fun move(state: PositionAndHeading, amount: Int, direction: Heading): PositionAndHeading =
        state.copy(x = state.x + direction.x * amount, y = state.y + direction.y * amount)

    override fun forward(position: PositionAndHeading, amount: Int): PositionAndHeading =
        position.copy(x = position.x + position.heading.x * amount, y = position.y + position.heading.y * amount)

    override fun turn(position: PositionAndHeading, turn: Turn): PositionAndHeading {
        assert(turn.degrees % 90 == 0) { "Only right angles allowed" }
        val nrOfHeadings = Heading.values().size
        return position.copy(heading = Heading.values()[(nrOfHeadings + position.heading.ordinal + turn.degrees / 90) % nrOfHeadings])
    }
}

class Part2 : Interpreter<PositionAndWaypoint> {
    override fun move(state: PositionAndWaypoint, amount: Int, direction: Heading) =
        state.copy(
            wpx = state.wpx + direction.x * amount,
            wpy = state.wpy + direction.y * amount
        )

    override fun forward(state: PositionAndWaypoint, amount: Int) =
        state.copy(
            x = state.x + state.wpx * amount,
            y = state.y + state.wpy * amount
        )

    private val PositionAndWaypoint.rotate90cw get() = copy(wpx = wpy, wpy = -wpx)
    override fun turn(state: PositionAndWaypoint, turn: Turn): PositionAndWaypoint =
        when ((360 + turn.degrees) % 360) {
            0 -> state
            90 -> state.rotate90cw
            180 -> state.rotate90cw.rotate90cw
            270 -> state.rotate90cw.rotate90cw.rotate90cw
            else -> throw IllegalArgumentException("Turn angle (${turn.degrees}) not multiple of 90")
        }
}

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { it.parseInstruction() }.toList()
        val print: (Coords) -> Unit = { (x, y) ->
            println("New position: x=$x, y=$y, Manhattan distance=${x.absoluteValue + y.absoluteValue}")
        }
        Part1().run(PositionAndHeading(0, 0, Heading.E), input).let(print)
        Part2().run(PositionAndWaypoint(0, 0, 10, 1), input).let(print)
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
