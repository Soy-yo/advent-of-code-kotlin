package day06

import readInput

fun LongArray.shiftLeft(v: Long = 0) {
    for (i in 1 until size) {
        set(i - 1, get(i))
    }
    set(size - 1, v)
}

fun simulate(input: List<String>, n: Int): Long {
    val counts = input.first()
        .split(',')
        .map(String::toInt)
        .groupingBy { it }
        .eachCount()
    val lanternFish = LongArray(9) { counts.getOrDefault(it, 0).toLong() }
    repeat(n) {
        val new = lanternFish[0]
        lanternFish.shiftLeft()
        lanternFish[6] += new
        lanternFish[8] += new
    }
    return lanternFish.sum()
}

fun main() {
    fun part1(input: List<String>) = simulate(input, 80)
    fun part2(input: List<String>) = simulate(input, 256)

    val testInput = readInput("day06/Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539L)

    val input = readInput("day06/Day06")
    println(part1(input))
    println(part2(input))
}
