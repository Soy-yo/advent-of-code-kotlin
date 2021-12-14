package day14

import readInput

fun splitInput(input: List<String>): Pair<List<String>, List<String>> =
    Pair(input.takeWhile { it.isNotBlank() }, input.takeLastWhile { it.isNotBlank() })

fun simulate(template: String, rules: Map<String, Char>, n: Int): Map<Char, Long> {
    val chars = template
        .windowed(1)
        .groupingBy { it }
        .eachCount()
        .map { (k, v) -> k[0] to v.toLong() }
        .toMap()
        .toMutableMap()
    var pairs = template
        .windowed(2)
        .groupingBy { it }
        .eachCount()
        .map { (k, v) -> k to v.toLong() }
        .toMap()

    repeat(n) {
        pairs = buildMap {
            for ((k, v) in pairs) {
                val (c1, c2) = k.toList()
                val insert = rules.getValue(k)
                val newPairLeft = "$c1$insert"
                val newPairRight = "$insert$c2"

                put(newPairLeft, getOrDefault(newPairLeft, 0L) + v)
                put(newPairRight, getOrDefault(newPairRight, 0L) + v)

                chars[insert] = chars.getOrDefault(insert, 0L) + v
            }
        }
    }

    return chars
}

fun main() {
    fun part1(input: List<String>): Int =
        splitInput(input).let { (template, maps) ->
            val rules = maps.associate {
                it.split(" -> ").let { (pair, insert) -> pair to insert[0] }
            }
            val counts = simulate(template[0], rules, 10)
            (counts.values.maxOrNull()!! - counts.values.minOrNull()!!).toInt()
        }

    fun part2(input: List<String>): Long =
        splitInput(input).let { (template, maps) ->
            val rules = maps.associate {
                it.split(" -> ").let { (pair, insert) -> pair to insert[0] }
            }
            val counts = simulate(template[0], rules, 40)
            counts.values.maxOrNull()!! - counts.values.minOrNull()!!
        }

    val testInput = readInput("day14/Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529)

    val input = readInput("day14/Day14")
    println(part1(input))
    println(part2(input))
}
