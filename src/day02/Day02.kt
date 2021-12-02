package day02

import readInput

data class Position(val depth: Int, val dist: Int, val aim: Int = 0)

fun main() {
    fun part1(input: List<String>): Int =
        input.map {
            val (dir, length) = it.split(' ')
            when (dir) {
                "down" -> {
                    Position(length.toInt(), 0)
                }
                "up" -> {
                    Position(-length.toInt(), 0)
                }
                else -> {
                    Position(0, length.toInt())
                }
            }
        }.reduce { (curDepth, curDist), (depth, dist) ->
            Position(curDepth + depth, curDist + dist)
        }.run {
            depth * dist
        }

    fun part2(input: List<String>): Int =
        input.map {
            val (dir, length) = it.split(' ')
            when (dir) {
                "down" -> {
                    Position(0, 0, length.toInt())
                }
                "up" -> {
                    Position(0, 0, -length.toInt())
                }
                else -> {
                    Position(0, length.toInt())
                }
            }
        }.reduce { (curDepth, curDist, curAim), (_, dist, aim) ->
            Position(curDepth + dist * curAim, curDist + dist, curAim + aim)
        }.run {
            depth * dist
        }

    val testInput = readInput("day02/Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("day02/Day02")
    println(part1(input))
    println(part2(input))
}
