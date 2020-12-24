package me.nfekete.adventofcode.y2020.day23

import me.nfekete.adventofcode.y2020.common.product

object Day23 {

    private fun String.shiftLeft(chars: Int) =
        ((length + chars) % length).let { this.drop(it).take(length) + this.take(it) }

    private fun rearrange(input: String): String {
        val currentCup = input.first().also { println("Current cup: $it") }
        val takenOutCups = input.shiftLeft(1).take(3).also { println("Picked: $it") }
        val remainingCups = input.shiftLeft(1).drop(3)//.also { println("Remaining: $it") }
        val destinationCupLabel = generateSequence(currentCup) { label ->
            if (label > '1') label - 1 else remainingCups.maxOrNull()!!
        }.drop(1).first { it in remainingCups }.also { println("Destination cup: $it") }
        val destinationIndex = remainingCups.indexOf(destinationCupLabel)
        val reinserted = takenOutCups + remainingCups.shiftLeft(destinationIndex + 1)
        val nextCupIndex = (reinserted.indexOf(currentCup) + 1) % input.length
        return reinserted.shiftLeft(nextCupIndex)
    }
    private val String.result get() = shiftLeft(indexOf('1')).drop(1)


    class CyclicMutableList<T>(var shift: Int, val list: MutableList<T>): AbstractMutableList<T>() {
        private fun address(index: Int) = (list.size + index + shift) % list.size
        fun move3(fromIndex: Int, toIndex: Int) = this.apply {
            val list = shift(fromIndex).take(3).also { shift(-fromIndex) }
            if (fromIndex < toIndex) {
                for (i in fromIndex until toIndex) {
                    this[i] = this[i + 3]
                }
                for (i in toIndex until toIndex + 3) {
                    this[i] = list[i-toIndex]
                }
            } else if (fromIndex > toIndex) {
                for (i in toIndex until toIndex + 3) {
                    this[i] = list[i-toIndex]
                }
                for (i in fromIndex + 3 downTo toIndex) {
                    this[i] = this[i - 3]
                }
            }
        }

        override fun add(index: Int, element: T) = list.add(address(index), element)

        override fun removeAt(index: Int): T = list.removeAt(address(index))

        override fun set(index: Int, element: T): T = list.set(address(index), element)

        override val size: Int get() = list.size

        override fun get(index: Int): T = list[address(index)]

        fun shift(shift: Int): CyclicMutableList<T> = this.apply {
            this.shift += shift
        }
    }

    private val CyclicMutableList<Int>.result
        get() = shift(indexOf(1)).asSequence().drop(1).take(2).map(Int::toLong).toList().also(::println).product()


    class Part2(input: String, val maxValue: Int = 1_000_000) {
        private val extendedProblem = input.let {
            val initialSequence = it.map(Char::toString).map(String::toInt)
            val rest = initialSequence.maxOrNull()!! + 1..maxValue
            CyclicMutableList(0, mutableListOf<Int>().apply {
                addAll(initialSequence)
                addAll(rest)
            })
        }

        private fun rearrange(input: CyclicMutableList<Int>): CyclicMutableList<Int> {
            val currentCup = input.first()
            val takenOutCups = input.asSequence().drop(1).take(3).toList()
            val destinationCupLabel = generateSequence(currentCup) { label ->
                if (label > 1) label - 1 else maxValue
            }.drop(1).first { it !in takenOutCups }
            val destinationIndex = input.indexOf(destinationCupLabel) - 2
            println("Destination index = $destinationIndex")
            input.move3(1, destinationIndex)
            input.shift(input.indexOf(currentCup) + 1)
            println("Next first element = ${input.first()}")
            return input
        }

        fun run(steps: Int = 10_000_000): CyclicMutableList<Int> = generateSequence(extendedProblem, ::rearrange).drop(steps).first()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val sample = "389125467"
        val input = "389547612"
        val steps = 100
//        val cups = generateSequence(sample, ::rearrange).onEach(::println).drop(steps).first()

        Part2(input).run(steps).also {
//            println(it)
            println("Part 2 result = ${it.result}")
        }
    }

}
