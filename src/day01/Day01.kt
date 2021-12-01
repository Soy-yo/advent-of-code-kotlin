package day01

import readInput

fun main() {
    fun part1(input: List<String>): Int =
        input.map(String::toInt)
            .zipWithNext()
            .count { (x, y) -> y > x }

    fun part2(input: List<String>): Int =
        input.map(String::toInt)
            .windowed(3, transform = List<Int>::sum)
            .zipWithNext()
            .count { (x, y) -> y > x }

    val testInput = readInput("day01/Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("day01/Day01")
    println(part1(input))
    println(part2(input))
}
