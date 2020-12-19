package me.nfekete.adventofcode.y2020.day19

import me.nfekete.adventofcode.y2020.common.chunkBy
import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.splitByDelimiter

sealed class Rule
data class RuleSequence(val list: List<Rule>) : Rule()
data class OrRule(val alternates: List<Rule>) : Rule()
data class RuleReference(val ruleNumber: Int) : Rule()
data class TerminalRule(val char: Char) : Rule()

data class RuleSet(val rules: Map<Int, Rule>) {
    private fun match(input: String): Boolean = consume(rules[0]!!, input).contains("")
    private fun consume(rule: Rule, input: String): Sequence<String> {
        return when (rule) {
            is RuleSequence -> rule.list.fold(sequenceOf(input)) { inputs, subRule ->
                inputs.flatMap { input -> consume(subRule, input) }
            }
            is OrRule -> rule.alternates.asSequence().flatMap { subRule -> consume(subRule, input) }
            is RuleReference -> consume(rules[rule.ruleNumber]!!, input)
            is TerminalRule -> when {
                input.startsWith(rule.char) -> sequenceOf(input.substring(1))
                else -> emptySequence()
            }
        }
    }

    fun countMatches(input: List<String>) = input.count(::match)

    companion object {
        fun from(list: List<String>) = list.map { it.parseRule() }.toMap().run(::RuleSet)

        private fun String.parseRule(): Pair<Int, Rule> {
            val (ruleNumber, ruleDescription) = splitByDelimiter(": ")
            val rule = when {
                ruleDescription.startsWith("\"") -> TerminalRule(ruleDescription[1])
                else -> ruleDescription.split(" | ").map { ruleSequenceStr ->
                    ruleSequenceStr.split(" ").map(String::toInt).map(::RuleReference).let(::RuleSequence)
                }.let(::OrRule)
            }
            return ruleNumber.toInt() to rule
        }
    }
}

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        classpathFile("input.txt").lineSequence().chunkBy { it.isBlank() }.iterator().let {
            val stringRules = it.next()
            val input = it.next()

            RuleSet.from(stringRules).countMatches(input).let(::println)
            RuleSet.from(stringRules.toMutableList().apply {
                add("8: 42 | 42 8")
                add("11: 42 31 | 42 11 31")
            }).countMatches(input).let(::println)
        }
    }
}
