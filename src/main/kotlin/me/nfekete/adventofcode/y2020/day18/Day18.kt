package me.nfekete.adventofcode.y2020.day18

import me.nfekete.adventofcode.y2020.common.classpathFile

sealed class Token
class Number(val value: Long) : Token()
object OpeningParen : Token()
object ClosingParen : Token()
object Plus : Token()
object Multiply : Token()

private val combinedRegex = listOf("\\d+", "\\(", "\\)", "\\+", "\\*").joinToString("|") { "($it)" }.toRegex()
private fun String.tokenize() = combinedRegex.findAll(this).map { matchResult ->
    val (number, openingParen, closingParen, plus, multiply) = matchResult.destructured
    when {
        number.isNotBlank() -> Number(number.trim().toLong())
        openingParen.isNotBlank() -> OpeningParen
        closingParen.isNotBlank() -> ClosingParen
        plus.isNotBlank() -> Plus
        multiply.isNotBlank() -> Multiply
        else -> throw IllegalArgumentException(matchResult.toString())
    }
}

abstract class Interpreter(sequence: Sequence<Token>) {
    private val deque = sequence.toCollection(ArrayDeque())

    protected fun next() = deque.removeFirstOrNull()
    protected fun hasNext() = deque.isNotEmpty()
    protected fun peek(lookAhead: Int = 0) = deque.getOrNull(lookAhead)
}

class Part1(sequence: Sequence<Token>) : Interpreter(sequence) {

    /*
    rules:
    expression = term ( ('+'|'*') term )*
    term = number | '(' expression ')'
    */

    private fun expression(): Long {
        var acc = term()
        while (hasNext()) {
            when (peek()) {
                is Plus -> {
                    next()
                    acc += term()
                }
                is Multiply -> {
                    next()
                    acc *= term()
                }
                else -> return acc
            }
        }
        return acc
    }

    private fun term(): Long = when (val next = next()) {
        is Number -> next.value
        is OpeningParen -> {
            val value = expression()
            next() as ClosingParen
            value
        }
        else -> throw IllegalStateException("Unexpected token: $next")
    }

    fun interpret() = expression()
}

class Part2(sequence: Sequence<Token>) : Interpreter(sequence) {

    /*
    rules:
    expression = addition ( '*' addition )*
    addition = term ( '+' term )*
    term = number | '(' expression ')'
    */

    private fun expression(): Long {
        var acc = addition()
        while (hasNext()) {
            when (peek()) {
                is Multiply -> {
                    next()
                    acc *= addition()
                }
                else -> return acc
            }
        }
        return acc
    }

    private fun addition(): Long {
        var acc = term()
        while (hasNext()) {
            when (peek()) {
                is Plus -> {
                    next()
                    acc += term()
                }
                else -> return acc
            }
        }
        return acc
    }

    private fun term(): Long = when (val next = next()) {
        is Number -> next.value
        is OpeningParen -> {
            val value = expression()
            next() as ClosingParen
            value
        }
        else -> throw IllegalStateException("Unexpected token: $next")
    }

    fun interpret() = expression()
}


object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = classpathFile("input.txt").readLines()
        lines.map(::part1).sum().run(::println)
        lines.map(::part2).sum().run(::println)
    }

    fun part1(expression: String) = expression.tokenize().run(::Part1).interpret()
    fun part2(expression: String) = expression.tokenize().run(::Part2).interpret()
}
