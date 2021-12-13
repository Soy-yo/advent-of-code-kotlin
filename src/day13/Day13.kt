package day13

import readInput

typealias Point = Pair<Int, Int>

fun splitInput(input: List<String>): Pair<List<String>, List<String>> =
    Pair(input.takeWhile { it.isNotBlank() }, input.takeLastWhile { it.isNotBlank() })

fun getSymmetryLine(name: String, value: Int) =
    when (name) {
        "x" -> Point(value, 0)
        "y" -> Point(0, value)
        else -> throw IllegalArgumentException()
    }

fun symmetric(of: Point, by: Point): Point {
    val (x0, y0) = of
    val (x, y) = by
    if (x == 0) {
        return Point(x0, 2 * y - y0)
    }
    if (y == 0) {
        return Point(2 * x - x0, y0)
    }
    throw IllegalArgumentException()
}

infix fun Point.isOver(line: Point): Boolean {
    val (x0, y0) = this
    val (x, y) = line
    if (x == 0) {
        return y0 > y
    }
    if (y == 0) {
        return x0 > x
    }
    throw IllegalArgumentException()
}

fun main() {
    fun part1(input: List<String>): Int =
        splitInput(input).let { (pointStrings, foldStrings) ->
            val (coordinate, value) = foldStrings.first().split(' ').last().split("=")
            val points = pointStrings.map {
                it.split(',')
                    .map(String::toInt)
                    .let { (x, y) -> Point(x, y) }
            }.toSet()
            val sym = getSymmetryLine(coordinate, value.toInt())
            points.fold(0) { acc, p ->
                if (symmetric(p, sym) in points) acc + 1 else acc + 2
            } / 2
        }

    fun part2(input: List<String>): String {
        splitInput(input).let { (pointStrings, foldStrings) ->
            val folds = foldStrings.map {
                it.split(' ').last().split("=").let { (z, v) ->
                    getSymmetryLine(z, v.toInt())
                }
            }
            val points = pointStrings.map {
                it.split(',')
                    .map(String::toInt)
                    .let { (x, y) -> Point(x, y) }
            }.toSet()
            val result = folds.fold(points) { acc, fold ->
                buildSet {
                    for (p in acc) {
                        if (p isOver fold) {
                            add(symmetric(p, fold))
                        } else {
                            add(p)
                        }
                    }
                }
            }
            val maxX = result.maxOf(Point::first)
            val maxY = result.maxOf(Point::second)
            return buildString {
                for (y in 0..maxY) {
                    for (x in 0..maxX) {
                        if (Point(x, y) in result) {
                            append('#')
                        } else {
                            append('.')
                        }
                    }
                    if (y < maxY) {
                        appendLine()
                    }
                }
            }
        }
    }

    val part2Result = """
        #####
        #...#
        #...#
        #...#
        #####
    """.trimIndent()

    val testInput = readInput("day13/Day13_test")
    check(part1(testInput) == 17)
    check(part2(testInput) == part2Result)

    val input = readInput("day13/Day13")
    println(part1(input))
    println(part2(input))
}
