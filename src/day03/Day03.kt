package day03

import readInput

operator fun List<Int>.plus(other: List<Int>) = zip(other) { a, b -> a + b }

fun List<List<Int>>.sum() = reduce(List<Int>::plus)

fun binaryListToInt(x: List<Int>) = x.joinToString("").toInt(2)

fun getRating(input: List<List<Int>>, b: Int): List<Int> {
    var rating = input
    for (i in generateSequence(0, Int::inc)) {
        val n = rating.size
        if (n == 1) {
            break
        }
        val x = rating.fold(0) { acc, binaries ->
            acc + binaries[i]
        }.run {
            if (this >= (n + 1) / 2) b else (1 - b)
        }
        rating = rating.filter { it[i] == x }
    }
    return rating.first()
}

fun main() {
    fun part1(input: List<String>): Int {
        val n = input.size
        val gamma = input.map {
            it.toCharArray().map(Character::getNumericValue)
        }.sum().map {
            if (it >= (n + 1) / 2) 1 else 0
        }
        val epsilon = gamma.map { 1 - it }
        return binaryListToInt(gamma) * binaryListToInt(epsilon)
    }

    fun part2(input: List<String>): Int {
        val typedInput = input.map {
            it.toCharArray().map(Character::getNumericValue)
        }
        val oxygen = getRating(typedInput, 1)
        val co2 = getRating(typedInput, 0)

        return binaryListToInt(oxygen) * binaryListToInt(co2)
    }

    val testInput = readInput("day03/Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("day03/Day03")
    println(part1(input))
    println(part2(input))
}
