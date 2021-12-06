package day05


import readInput
import kotlin.math.absoluteValue

data class Point(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

class Segment(val p1: Point, val p2: Point) {

    val isHorizontal: Boolean
        get() = p1.y == p2.y

    val isVertical: Boolean
        get() = p1.x == p2.x

    val isDiagonal: Boolean
        get() = (p1.x - p2.x).absoluteValue == (p1.y - p2.y).absoluteValue

    fun toList(): List<Point> = when {
        isHorizontal -> {
            val range = if (p1.x > p2.x) {
                p1.x downTo p2.x
            } else {
                p1.x..p2.x
            }
            range.map { x -> Point(x, p1.y) }
        }
        isVertical -> {
            val range = if (p1.y > p2.y) {
                p1.y downTo p2.y
            } else {
                p1.y..p2.y
            }
            range.map { y -> Point(p1.x, y) }
        }
        isDiagonal -> {
            val xRange = if (p1.x > p2.x) {
                p1.x downTo p2.x
            } else {
                p1.x..p2.x
            }
            val yRange = if (p1.y > p2.y) {
                p1.y downTo p2.y
            } else {
                p1.y..p2.y
            }
            xRange.zip(yRange).map { (x, y) -> Point(x, y) }
        }
        else -> {
            emptyList()
        }
    }

    operator fun component1() = p1

    operator fun component2() = p2

    override fun toString() = "[$p1, $p2]"

}

fun main() {
    fun part1(input: List<String>): Int = input.map { line ->
        line.split(" -> ").map {
            it.split(',').map(String::toInt).let { (x, y) -> Point(x, y) }
        }.let { (p1, p2) ->
            Segment(p1, p2)
        }
    }.filter {
        it.isVertical || it.isHorizontal
    }.flatMap(Segment::toList)
        .groupingBy { it }
        .eachCount()
        .count { (_, v) -> v > 1 }

    fun part2(input: List<String>): Int = input.map { line ->
        line.split(" -> ").map {
            it.split(',').map(String::toInt).let { (x, y) -> Point(x, y) }
        }.let { (p1, p2) ->
            Segment(p1, p2)
        }
    }.flatMap(Segment::toList)
        .groupingBy { it }
        .eachCount()
        .count { (_, v) -> v > 1 }

    val testInput = readInput("day05/Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("day05/Day05")
    println(part1(input))
    println(part2(input))
}
