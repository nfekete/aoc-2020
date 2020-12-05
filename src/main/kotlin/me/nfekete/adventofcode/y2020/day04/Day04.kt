package me.nfekete.adventofcode.y2020.day04

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day04 {

    internal enum class Key { ecl, pid, eyr, hcl, byr, iyr, hgt, cid }
    private val requiredKeys = Key.values().toSet().minus(Key.cid)
    private val validEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

    @JvmStatic
    fun main(args: Array<String>) =
        loadPassports("input.txt").filterIncomplete().run {
            println("Number of passwords having all required fields in the batch: $size")
            this
        }.filterInvalid().run {
            println("Number of valid passwords in the batch: $size")
        }

    internal fun List<Map<Key, String>>.filterInvalid() =
        filter { passport -> passport.entries.all { isValidField(it.toPair()) } }

    private fun List<Map<Key, String>>.filterIncomplete() =
        filter { it.keys.containsAll(requiredKeys) }

    internal fun loadPassports(path: String) = classpathFile(path)
        .useLines { lines ->
            lines.chunkBy { it.isBlank() }
                .map { splitToKeyValuePairs(it) }
                .map { it.toMap() }
                .toList()
        }

    internal fun isValidField(pair: Pair<Key, String>): Boolean {
        val (key, value) = pair
        return when (key) {
            Key.byr -> value.isNumberInRange(1920..2002)
            Key.iyr -> value.isNumberInRange(2010..2020)
            Key.eyr -> value.isNumberInRange(2020..2030)
            Key.hgt -> isValidHeight(value)
            Key.hcl -> value.matches("^#[0-9a-f]{6}$".toRegex())
            Key.ecl -> value in validEyeColors
            Key.pid -> value.matches("^[0-9]{9}$".toRegex())
            Key.cid -> true
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
            .map { (key, value) -> Key.valueOf(key) to value }

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
