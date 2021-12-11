package day11

import readInput

fun adjacentPositions(i: Int, j: Int, n: Int, m: Int): List<Pair<Int, Int>> = buildList {
    if (i > 0) {
        add(Pair(i - 1, j))
        if (j > 0) {
            add(Pair(i - 1, j - 1))
        }
        if (j < m - 1) {
            add(Pair(i - 1, j + 1))
        }
    }
    if (j > 0) {
        add(Pair(i, j - 1))
    }
    if (j < m - 1) {
        add(Pair(i, j + 1))
    }
    if (i < n - 1) {
        add(Pair(i + 1, j))
        if (j > 0) {
            add(Pair(i + 1, j - 1))
        }
        if (j < m - 1) {
            add(Pair(i + 1, j + 1))
        }
    }
}

fun simulate(input: Array<IntArray>, n: Int): Int {
    var count = 0
    repeat(n) {
        count += simulate1(input)
    }
    return count
}

fun simulateUntil(input: Array<IntArray>, predicate: (Int) -> Boolean): Int =
    generateSequence(1) { it + 1 }
        .dropWhile { !predicate(simulate1(input)) }
        .first()

fun simulate1(input: Array<IntArray>): Int {
    var count = 0
    val queue = ArrayDeque<Pair<Int, Int>>()
    val seen = mutableSetOf<Pair<Int, Int>>()
    // Add 1 to all octopuses
    for ((i, row) in input.withIndex()) {
        for (j in row.indices) {
            input[i][j]++
            if (input[i][j] > 9) {
                queue.addFirst(Pair(i, j))
                seen.add(Pair(i, j))
            }
        }
    }
    // Expand
    while (queue.isNotEmpty()) {
        val (p, q) = queue.removeLast()
        for ((i, j) in adjacentPositions(p, q, input.size, input[0].size).filter { it !in seen }) {
            input[i][j]++
            if (input[i][j] > 9) {
                queue.addFirst(Pair(i, j))
                seen.add(Pair(i, j))
            }
        }
    }
    // Reset flashed octopuses
    for ((i, row) in input.withIndex()) {
        for (j in row.indices) {
            if (input[i][j] > 9) {
                count++
                input[i][j] = 0
            }
        }
    }
    return count
}

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            line.chunked(1).map(String::toInt).toIntArray()
        }.toTypedArray().let {
            simulate(it, 100)
        }

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.chunked(1).map(String::toInt).toIntArray()
        }.toTypedArray().let {
            simulateUntil(it) { count -> count == it.size * it[0].size }
        }


    val testInput = readInput("day11/Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("day11/Day11")
    println(part1(input))
    println(part2(input))
}
