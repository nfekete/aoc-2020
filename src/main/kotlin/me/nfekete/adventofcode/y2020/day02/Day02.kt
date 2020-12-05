package me.nfekete.adventofcode.y2020.day02

import me.nfekete.adventofcode.y2020.common.classpathFile

object Day02 {

    private val passwordLineRegex = "^(?<min>\\d+)-(?<max>\\d+) (?<char>[a-z]): (?<password>.*)$".toRegex()

    data class PasswordLine(val min: Int, val max: Int, val char: Char, val password: String) {
        val isValidPart1: Boolean = password.count { it == char } in min..max
        val isValidPart2: Boolean = (password[min - 1] == char) xor (password[max - 1] == char)
    }

    private fun String.toPasswordLine() = passwordLineRegex.matchEntire(this)?.let {
        with(it.groups) {
            PasswordLine(
                get("min")!!.value.toInt(),
                get("max")!!.value.toInt(),
                get("char")!!.value.first(),
                get("password")!!.value
            )
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val passwordLines = classpathFile("input.txt").readLines().mapNotNull { it.toPasswordLine() }.toList()
        passwordLines.count { it.isValidPart1 }.run { println(this) }
        passwordLines.count { it.isValidPart2 }.run { println(this) }
    }
}
