package me.nfekete.adventofcode.y2020.day23

object Day23 {

    val String.circular get() = this + this
    fun String.shift(chars: Int) = circular.drop(chars).take(length)
    fun String.insertAt(index: Int, insertion: String) = this.take(index) + insertion + this.drop(index)

    fun rearrange(input: String, currentCupIndex: Int): Pair<String, Int> {
        val currentCupIndex = currentCupIndex % input.length
        val takenOutCups = input.shift(currentCupIndex + 1).take(3).also { println("Picked: $it") }
        val remainingCups = input.shift(currentCupIndex + 1).drop(3).also { println("Remaining: $it") }
        val remainingCupLabels = remainingCups.map { "$it".toInt() }
        val currentCupLabel = "${input.circular[currentCupIndex]}".toInt()
        val nextCupLabel = '0' + generateSequence(currentCupLabel - 1) { label ->
            if (remainingCupLabels.contains(label - 1)) label - 1 else remainingCupLabels.maxOrNull()!!
        }.first { remainingCups.contains(('0' + it)) }
        val destinationIndex = remainingCups.indexOf(nextCupLabel)
        val reinserted = remainingCups.insertAt(destinationIndex + 1, takenOutCups)
        val nextCupIndex = (reinserted.indexOf('0' + currentCupLabel) + 1) % input.length
        return reinserted to nextCupIndex
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val sample = "389125467"
        val input = "389547612"
        val steps = 10
        val (cups, _) = generateSequence(sample to 0) { (input, currentCupIndex) ->
            rearrange(
                input,
                currentCupIndex
            )
        }.onEach(::println)
            .take(1 + steps).last()
        cups.shift(cups.indexOf('1')).drop(1).also(::println)
    }
}
