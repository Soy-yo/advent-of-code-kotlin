package day25

import readInput

typealias Position = Pair<Int, Int>

enum class Direction {
    RIGHT,
    DOWN;

    companion object {
        fun fromChar(c: Char) = when (c) {
            '>' -> RIGHT
            'v' -> DOWN
            else -> throw IllegalArgumentException()
        }
    }

    override fun toString() = when (this) {
        RIGHT -> ">"
        DOWN -> "v"
    }

}

data class SeaCucumber(val direction: Direction, var position: Position) {
    fun nextPosition(n: Int, m: Int) = when (direction) {
        Direction.RIGHT -> Position(position.first, (position.second + 1) % m)
        Direction.DOWN -> Position((position.first + 1) % n, position.second)
    }
    fun move(n: Int, m: Int) {
        position = nextPosition(n, m)
    }
}

data class SeaCucumberMap(
    val seaCucumbers: List<SeaCucumber>,
    val map: MutableMap<Position, SeaCucumber>,
    val mapRows: Int,
    val mapCols: Int
)

fun parseInput(input: List<String>): SeaCucumberMap {
    val n = input.size
    val m = input[0].length
    val list = buildList {
        for ((i, line) in input.withIndex()) {
            for ((j, c) in line.withIndex()) {
                if (c != '.') {
                    add(SeaCucumber(Direction.fromChar(c), Position(i, j)))
                }
            }
        }
    }
    val map = list.associateBy { Pair(it.position.first, it.position.second) }.toMutableMap()
    return SeaCucumberMap(list, map, n, m)
}

fun main() {
    fun part1(input: List<String>): Int {
        val (seaCucumbers, map, rows, cols) = parseInput(input)

        var moved = true

        fun move(sc: SeaCucumber) {
            map.remove(sc.position)
            sc.move(rows, cols)
            map[sc.position] = sc
            moved = true
        }

        var count = 0

        while (moved) {
            moved = false

            seaCucumbers.filter { it.direction == Direction.RIGHT && it.nextPosition(rows, cols) !in map }
                .forEach(::move)
            seaCucumbers.filter { it.direction == Direction.DOWN && it.nextPosition(rows, cols) !in map }
                .forEach(::move)

            count++
        }
        return count
    }

    val testInput = readInput("day25/Day25_test")
    check(part1(testInput) == 58)

    val input = readInput("day25/Day25")
    println(part1(input))
}
