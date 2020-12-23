package me.nfekete.adventofcode.y2020.day23

object Day23 {

    private val String.circular get() = this + this
    private fun String.shiftLeft(chars: Int) = circular.drop((length + chars) % length).take(length)
    private fun String.shiftRight(chars: Int) = shiftLeft(-chars)
    private fun String.insertAt(index: Int, insertion: String) = this.take(index) + insertion + this.drop(index)

    private fun rearrange(input: String, currentCupIndex: Int): Pair<String, Int> {
        val currentCupIndex = currentCupIndex % input.length
        val currentCup = input[currentCupIndex].also { println("Current cup: $it") }
        val takenOutCups = input.shiftLeft(currentCupIndex + 1).take(3).also { println("Picked: $it") }
        val remainingCups = input.shiftLeft(currentCupIndex + 1).drop(3)//.also { println("Remaining: $it") }
        val destinationCupLabel = generateSequence(currentCup) { label ->
            if (label > '1') label - 1 else remainingCups.maxOrNull()!!
        }.drop(1).first { it in remainingCups }.also { println("Destination cup: $it") }
        val destinationIndex = remainingCups.indexOf(destinationCupLabel)
        val reinserted = takenOutCups + remainingCups.shiftLeft(destinationIndex + 1)
        val nextCupIndex = (reinserted.indexOf(currentCup) + 1) % input.length
        return reinserted.shiftLeft(nextCupIndex) to 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val sample = "389125467"
        val input = "389547612"
        val steps = 100
        val (cups, _) = generateSequence(sample to 0) { (input, currentCupIndex) ->
            rearrange(
                input,
                currentCupIndex
            )
        }.onEach(::println)
            .take(1 + steps).last()
        cups.shiftLeft(cups.indexOf('1')).drop(1).also(::println)
    }
}
