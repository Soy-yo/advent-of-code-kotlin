package day10

import readInput

val CLOSING = mapOf(
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<'
)

val SCORES_SYNTAX = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)

val SCORES_AUTOCOMPLETE = mapOf(
    '(' to 1,
    '[' to 2,
    '{' to 3,
    '<' to 4
)

fun <T> matchString(line: String, returnValue: (Iterable<Char>, Char?) -> T): T {
    val stack = ArrayDeque<Char>()
    for (char in line) {
        if (char !in CLOSING) {
            stack.addLast(char)
        } else {
            val last = stack.removeLast()
            if (last != CLOSING[char]) {
                return returnValue(stack, char)
            }
        }
    }
    return returnValue(stack, null)
}

fun computeScore(line: Iterable<Char>) =
    line.fold(0L) { acc, c -> acc * 5 + SCORES_AUTOCOMPLETE.getValue(c) }

fun main() {
    fun part1(input: List<String>): Int =
        input.sumOf { line ->
            matchString(line) { _, c -> SCORES_SYNTAX[c] ?: 0 }
        }

    fun part2(input: List<String>): Long =
        input.map { line ->
            matchString(line) { rem, c ->
                if (c === null) rem else emptyList()
            }.reversed().let { remaining -> computeScore(remaining) }
        }.filter { it > 0 }
            .sorted()
            .let { it[it.size / 2] }

    val testInput = readInput("day10/Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("day10/Day10")
    println(part1(input))
    println(part2(input))
}
