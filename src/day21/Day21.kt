package day21

import readInput

fun parseInput(input: List<String>): Pair<Int, Int> =
    input.map { it.split(": ").last().toInt() }.let { (x, y) -> Pair(x, y) }

fun main() {
    fun part1(input: List<String>): Int {
        var (p1, p2) = parseInput(input)
        val sequence1 = generateSequence(6) { (it - 2).mod(10) }
        val sequence2 = generateSequence(5) { (it - 2).mod(10) }
        var points1 = 0
        var points2 = 0
        var i = 0
        for ((die1, die2) in sequence1.zip(sequence2)) {
            i += 3
            p1 += die1
            if (p1 > 10) {
                p1 -= 10
            }
            points1 += p1
            if (points1 >= 1000) {
                return points2 * i
            }

            i += 3
            p2 += die2
            if (p2 > 10) {
                p2 -= 10
            }
            points2 += p2
            if (points2 >= 1000) {
                return points1 * i
            }
        }
        return -1
    }

    fun part2(input: List<String>): Long {
        val dice = buildList {
            for (i in 1..3) {
                for (j in 1..3) {
                    for (k in 1..3) {
                        add(i + j + k)
                    }
                }
            }
        }.groupingBy { it }.eachCount()

        fun simulate(p1: Int, p2: Int, points1: Int = 0, points2: Int = 0, turn: Int = 0): Pair<Long, Long> {
            if (points1 >= 21) {
                return 1L to 0L
            }
            if (points2 >= 21) {
                return 0L to 1L
            }
            var counts1 = 0L
            var counts2 = 0L
            val players = intArrayOf(p1, p2)
            val points = intArrayOf(points1, points2)
            for ((die, times) in dice) {
                players[turn] += die
                if (players[turn] > 10) {
                    players[turn] -= 10
                }
                points[turn] += players[turn]

                val (c1, c2) = simulate(players[0], players[1], points[0], points[1], 1 - turn)
                counts1 += c1 * times
                counts2 += c2 * times

                points[turn] -= players[turn]
                players[turn] -= die
                if (players[turn] <= 0) {
                    players[turn] += 10
                }
            }
            return counts1 to counts2
        }
        val (p1, p2) = parseInput(input)
        return simulate(p1, p2).let { (x, y) -> maxOf(x, y) }
    }

    val testInput = readInput("day21/Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInput("day21/Day21")
    println(part1(input))
    println(part2(input))
}
