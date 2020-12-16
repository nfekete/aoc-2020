package me.nfekete.adventofcode.y2020.common

import java.io.BufferedReader
import java.io.InputStreamReader

inline fun <reified T : Any> T.classpathFile(path: String) =
    BufferedReader(
        InputStreamReader(
            javaClass.getResourceAsStream(path)
        )
    )

fun String.translate(vararg chars: Pair<Char, Char>) = chars.toMap().let { map ->
    this.map { map.getOrDefault(it, it) }.joinToString("")
}

fun Sequence<String>.chunkBy(predicate: (String) -> Boolean) = sequence {
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

fun String.splitByDelimiter(delimiter: Char) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + 1) }

fun String.splitByDelimiter(delimiter: String) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + delimiter.length) }

fun <A, B, C> Pair<A, B>.map1(fn: (A) -> C): Pair<C, B> = let { (a, b) -> fn(a) to b }
fun <A, B, C> Pair<A, B>.map2(fn: (B) -> C): Pair<A, C> = let { (a, b) -> a to fn(b) }
