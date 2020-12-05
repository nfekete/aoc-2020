package me.nfekete.adventofcode.y2020.day04

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day04 {

    private val requiredKeys = setOf("ecl", "pid", "eyr", "hcl", "byr", "iyr", "hgt")
    private val validEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

    @JvmStatic
    fun main(args: Array<String>) {
        val passportCandidates = loadPassports("input.txt")

        val completePassports = passportCandidates.filterIncomplete()
        val validPassports = completePassports.filterInvalid()

        println("Number of passwords having all required fields in the batch: ${completePassports.count()}")
        println("Number of valid passwords in the batch: ${validPassports.count()}")
    }

    internal fun List<Map<String, String>>.filterInvalid() =
        filter { passport -> passport.entries.all { isValidField(it.toPair()) } }

    private fun List<Map<String, String>>.filterIncomplete() =
        filter { it.keys.containsAll(requiredKeys) }

    internal fun loadPassports(path: String) = classpathFile(path)
        .useLines { lines ->
            lines.chunkBy { it.isBlank() }
                .map { splitToKeyValuePairs(it) }
                .map { it.toMap() }
                .toList()
        }

    internal fun isValidField(pair: Pair<String, String>): Boolean {
        val (key, value) = pair
        return when (key) {
            "byr" -> value.isNumberInRange(1920..2002)
            "iyr" -> value.isNumberInRange(2010..2020)
            "eyr" -> value.isNumberInRange(2020..2030)
            "hgt" -> isValidHeight(value)
            "hcl" -> value.matches("^#[0-9a-f]{6}$".toRegex())
            "ecl" -> value in validEyeColors
            "pid" -> value.matches("^[0-9]{9}$".toRegex())
            "cid" -> true
            else -> false
        }
    }

    private fun isValidHeight(value: String) = when {
        value.endsWith("cm") -> value.removeSuffix("cm").isNumberInRange(150..193)
        value.endsWith("in") -> value.removeSuffix("in").isNumberInRange(59..76)
        else -> false
    }

    private fun String.isNumberInRange(range: ClosedRange<Int>) = toIntOrNull()?.let(range::contains) ?: false

    private fun splitToKeyValuePairs(it: List<String>) =
        it.flatMap { string -> splitToKeyValuePairs(string) }

    private fun splitToKeyValuePairs(string: String) =
        string.splitToSequence(" ")
            .map { keyValuePair -> keyValuePair.splitByDelimiter(':') }

    private fun String.splitByDelimiter(delimiter: Char) =
        indexOf(delimiter)
            .let { index -> take(index) to substring(index + 1) }

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
