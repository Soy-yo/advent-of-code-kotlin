package day07

import readInput
import kotlin.math.*

fun List<Int>.median(): Int = sorted().let {
    if (it.size % 2 != 0) {
        it[it.size / 2]
    } else {
        (it[it.size / 2] + it[it.size / 2 - 1]) / 2
    }
}

fun Double.ceilToInt() = ceil(this).toInt()

fun geometricDistance(iter: Iterable<Int>, distanceTo: Int = 0): Int =
    iter.sumOf { x ->
        val dist = (x - distanceTo).absoluteValue
        dist * (dist + 1) / 2
    }

fun main() {
    fun part1(input: List<String>): Int =
        input[0].split(',').map(String::toInt).let {
            val median = it.median()
            it.sumOf { x -> (x - median).absoluteValue }
        }

    fun part2(input: List<String>): Int  =
        input[0].split(',').map(String::toInt).let {
            val (floorMean, ceilMean) = it.average().run { toInt() to ceilToInt() }
            min(geometricDistance(it, floorMean), geometricDistance(it, ceilMean))
        }

    val testInput = readInput("day07/Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("day07/Day07")
    println(part1(input))
    println(part2(input))
}
