package me.nfekete.adventofcode.y2020.day23

import me.nfekete.adventofcode.y2020.common.product

private data class Node<T>(val value: T) {
    lateinit var next: Node<T>
}

private class LinkedListWithIndex<T>(list: List<T>) : Iterable<T> {
    private var first: Node<T>
    private val index: Map<T, Node<T>>

    init {
        val nodes = list.asReversed().map { Node(it) }.runningReduce { acc, node -> node.also { it.next = acc } }
        first = nodes.last()
        index = generateSequence(first) { it.next }.take(list.size).map { it.value to it }.toMap()
        nodes.first().next = first
    }

    fun shiftUntil(value: Int): LinkedListWithIndex<T> = this.apply {
        first = generateSequence(first) { it.next }.dropWhile { it.value != value }.first()
    }

    override fun iterator(): Iterator<T> =
        generateSequence(first) { it.next }.map { it.value }.take(index.size).iterator()

    fun moveNext3After(afterElement: T) {
        val fromSecond = first.next //the second element
        first.next = fromSecond.next.next.next
        val afterElementNode = index[afterElement]!!
        val oldNext = afterElementNode.next
        afterElementNode.next = fromSecond
        fromSecond.next.next.next = oldNext
        first = first.next // advance 1 step
    }

    override fun toString(): String = asSequence().joinToString(", ")
}

private val LinkedListWithIndex<Int>.result1
    get() = shiftUntil(1).drop(1).map { '0' + it }.joinToString("")
private val LinkedListWithIndex<Int>.result2
    get() = shiftUntil(1).drop(1).take(2).map(Int::toLong).toList().also(::println).product()

private class Game(input: String, private val maxValue: Int) {
    private val extendedProblem = input.let {
        val initialSequence = it.map(Char::toString).map(String::toInt)
        val rest = initialSequence.maxOrNull()!! + 1..maxValue
        LinkedListWithIndex(initialSequence + rest)
    }

    private fun rearrange(input: LinkedListWithIndex<Int>): LinkedListWithIndex<Int> {
        val currentCup = input.first()
        val takenOutCups = input.asSequence().drop(1).take(3).toList()
        val destinationCupLabel = generateSequence(currentCup) { label ->
            if (label > 1) label - 1 else maxValue
        }.drop(1).first { it !in takenOutCups }
        input.moveNext3After(destinationCupLabel)
        return input
    }

    fun run(steps: Int): LinkedListWithIndex<Int> =
        generateSequence(extendedProblem, ::rearrange).drop(steps).first()
}

object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = "389547612"

        Game(input, input.length).run(100).also {
            println("Part 1 result = ${it.result1}")
        }
        Game(input, 1_000_000).run(10_000_000).also {
            println("Part 2 result = ${it.result2}")
        }
    }
}
