package me.nfekete.adventofcode.y2020.day06

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day06 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().asSequence()
            .chunkBy { it.isBlank() }.toList()
        val sumYes = input
            .map { group ->
                group.flatMap { personYesAnswers ->
                    personYesAnswers.asSequence()
                }.toCollection(HashSet())
                    .size
            }.sum()
        println("Sum of YES answers from each group: $sumYes")
        val sumAllYes = input
            .map { group ->
                val answerGroups = group.flatMap { personYesAnswers ->
                    personYesAnswers.asSequence()
                }.groupingBy { it }
                    .eachCount()
                val countOfAnswersThatYesByAllMembers = answerGroups.values.filter { it == group.size }.count()
                countOfAnswersThatYesByAllMembers
            }.sum()
        println("Sum of the count of answers that were all yes from each group: $sumAllYes")
    }

    private fun Sequence<String>.chunkBy(predicate: (String) -> Boolean) = sequence {
        val currentChunk = mutableListOf<String>()
        for (line in this@chunkBy) {
            if (predicate(line)) {
                yield(currentChunk.toList())
                currentChunk.clear()
            } else {
                currentChunk.add(line)
            }
        }
        yield(currentChunk.toList())
    }
}
