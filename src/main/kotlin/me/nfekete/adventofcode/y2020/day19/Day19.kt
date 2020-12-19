package me.nfekete.adventofcode.y2020.day19

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.splitByDelimiter

sealed class Rule
data class RuleSequence(val list: List<RuleReference>) : Rule()
data class OrRule(val alternates: List<RuleSequence>) : Rule()
data class RuleReference(val ruleNumber: Int) : Rule()
data class TerminalRule(val char: Char) : Rule()

fun String.parseRule(): Pair<Int, Rule> {
    val (ruleNumber, ruleDescription) = splitByDelimiter(": ")
    val rule = when {
        ruleDescription.startsWith("\"") -> TerminalRule(ruleDescription[1])
        else -> ruleDescription.split(" | ").map { ruleSequenceStr ->
            ruleSequenceStr.split(" ").map(String::toInt).map(::RuleReference).let(::RuleSequence)
        }.let(::OrRule)
    }
    return ruleNumber.toInt() to rule
}

fun List<String>.parseRules() = map { it.parseRule() }.toMap()
fun Map<Int, Rule>.expandToRegexString(rule: Rule): String = when (rule) {
    is RuleSequence -> rule.list.joinToString("") { this.expandToRegexString(it) }
    is OrRule -> rule.alternates.joinToString("|", "(", ")") { this.expandToRegexString(it) }
    is RuleReference -> expandToRegexString(get(rule.ruleNumber)!!)
    is TerminalRule -> "${rule.char}"
}

fun Map<Int, Rule>.toRegex(): Regex {
    val regexp = this.expandToRegexString(get(0)!!)
    return regexp.toRegex()
}

object Day19 {

    private fun part1(rules: Map<Int, Rule>, input: List<String>) =
        rules.toRegex().let { regex -> input.filter { regex.matches(it) } }.count()

    @JvmStatic
    fun main(args: Array<String>) {
        classpathFile("input.txt").lineSequence().chunkBy { it.isBlank() }.iterator().let {
            val rules = it.next().parseRules()
            val input = it.next()

            part1(rules, input).let(::println)
        }
    }

}
