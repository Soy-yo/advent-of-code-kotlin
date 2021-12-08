package day08

import readInput

val UNIQUE_SEGMENT_SIZES = setOf(2, 3, 4, 7)

val CORRECT_SEGMENTS = mapOf(
    "abcefg" to '0',
    "cf" to '1',
    "acdeg" to '2',
    "acdfg" to '3',
    "bcdf" to '4',
    "abdfg" to '5',
    "abdefg" to '6',
    "acf" to '7',
    "abcdefg" to '8',
    "abcdfg" to '9'
)

fun toFixedNumber(s: String, mapping: Map<Char, Char>): Char =
    CORRECT_SEGMENTS[s.map { c -> mapping[c] }.sortedBy { it }.joinToString("")]!!

fun main() {
    fun part1(input: List<String>): Int =
        input.flatMap { line ->
            line.split(" | ")
                .last()
                .split(' ')
                .map(String::length)
        }.count {
            it in UNIQUE_SEGMENT_SIZES
        }

    fun part2(input: List<String>): Int =
        input.sumOf { line ->
            line.split(" | ").map {
                it.split(' ')
            }.let { (left, right) ->
                left.groupBy { it.length }.let { group ->
                    buildMap<Char, Char> {
                        // 1
                        val cf = group.getValue(2).single().toSet()
                        // 7
                        val a = group.getValue(3).single().toSet() - cf
                        // 4
                        val bd = group.getValue(4).single().toSet() - cf
                        // 0, 6, 9
                        val numbers6 = group.getValue(6)
                        // Zero doesn't have d
                        val zero = numbers6.single { (bd - it.toSet()).size == 1 }.toSet()
                        val d = bd - zero
                        val b = bd - d
                        val eg = zero - a - cf - bd
                        // Six doesn't have c
                        val six = numbers6.single { (cf - it.toSet()).size == 1 }.toSet()
                        val c = cf - six
                        val f = cf - c
                        // Nine doesn't have e
                        val nine = numbers6.single { (eg - it.toSet()).size == 1 }.toSet()
                        val e = eg - nine
                        val g = eg - e
                        put(a.single(), 'a')
                        put(b.single(), 'b')
                        put(c.single(), 'c')
                        put(d.single(), 'd')
                        put(e.single(), 'e')
                        put(f.single(), 'f')
                        put(g.single(), 'g')
                    }
                }.let { mapping ->
                    right.map {
                        toFixedNumber(it, mapping)
                    }.joinToString("").toInt(10)
                }
            }
        }

    val testInput = readInput("day08/Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("day08/Day08")
    println(part1(input))
    println(part2(input))
}
