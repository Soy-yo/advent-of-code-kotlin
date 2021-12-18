package day17

import readInput
import kotlin.math.ceil
import kotlin.math.sqrt

class Rect(val x0: Int, val x1: Int, val y0: Int, val y1: Int) {
    val xs get() = x0..x1
    val ys get() = y0..y1

    operator fun contains(p: Pair<Int, Int>)=
        p.first in xs && p.second in ys
}

fun parseInput(input: String) =
    Regex("^target area: x=([+-]?\\d+)..([+-]?\\d+), y=([+-]?\\d+)..([+-]?\\d+)$").let {
        val match = it.find(input)!!
        val (x0, x1, y0, y1) = match.destructured
        Rect(x0.toInt(), x1.toInt(), y0.toInt(), y1.toInt())
    }

fun sqrt(n: Int) = sqrt(n.toDouble())

fun Double.ceilToInt() = ceil(this).toInt()

fun simulate(rect: Rect): Pair<Int, Int> {
    val minX = ((-1 + sqrt(1 + 8 * rect.x0)) / 2).ceilToInt()
    val maxX = rect.x1
    val minY = rect.y0
    val maxY = -rect.y0
    var maxHeight = 0
    val velocities = mutableSetOf<Pair<Int, Int>>()
    for (dx in minX..maxX) {
        for (dy in minY..maxY) {
            var i = 0
            var x = 0
            var y = 0
            var height = 0
            while (x <= rect.x1 && y >= rect.y0) {
                if (Pair(x, y) in rect) {
                    maxHeight = maxOf(height, maxHeight)
                    velocities.add(Pair(dx, dy))
                }
                x += maxOf(dx - i, 0)
                y += dy - i
                height = maxOf(y, height)
                i++
            }
        }
    }
    return Pair(maxHeight, velocities.size)
}

fun main() {
    fun part1(input: List<String>): Int =
        parseInput(input[0]).let { rect ->
            simulate(rect).first
        }

    fun part2(input: List<String>): Int =
        parseInput(input[0]).let { rect ->
            simulate(rect).second
        }

    val testInput = readInput("day17/Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("day17/Day17")
    println(part1(input))
    println(part2(input))
}
