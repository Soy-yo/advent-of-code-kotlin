package day09

import day03.plus
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

class DisjointSet(n: Int): Iterable<Int> {

    private val parent = IntArray(n) { -1 }

    operator fun get(k: Int): Int =
        if (parent[k] < 0) {
            k
        } else {
            parent[k] = get(parent[k])
            parent[k]
        }

    fun merge(a: Int, b: Int) {
        val x = get(a)
        val y = get(b)
        if (x == y) {
            return
        }
        val (i, j) = if (x <= y) x to y else y to x
        parent[i] += parent[j]
        parent[j] = i
    }

    fun sizeOf(k: Int) = -parent[get(k)]

    override fun iterator(): Iterator<Int> = object : Iterator<Int> {

        private var index = 0

        override fun hasNext(): Boolean = index < parent.size

        override fun next(): Int {
            val result = index
            index++
            while (hasNext() && parent[index] >= 0) {
                index++
            }
            return result
        }
    }

}

operator fun List<Int>.plus(other: List<Int>) = zip(other) { a, b -> a + b }

fun List<List<Int>>.sum() = reduce(List<Int>::plus)

fun Iterable<Int>.product() = fold(1) { acc, x -> acc * x }

fun main() {
    fun part1(input: List<String>): Int {
        val typedInput = input.map { line ->
            line.chunked(1).map(String::toInt)
        }
        return typedInput.mapIndexed { i, row ->
            row.mapIndexed { j, height ->
                adjacentPositions(i, j, input.size, row.size).all { (p, q) ->
                    height < typedInput[p][q]
                }.let {
                    if (it) height + 1 else 0
                }
            }
        }.sum().sum()
    }

    fun part2(input: List<String>): Int {
        val colSize = input[0].length
        val ds = DisjointSet(input.size * colSize)
        input.map { line ->
            line.chunked(1).map(String::toInt)
        }.withIndex().zipWithNext { (i, above), (_, below) ->
            if (i == 0) {
                above.withIndex().zipWithNext { (j, left), (_, right) ->
                    if (left != 9 && right != 9) {
                        ds.merge(j, j + 1)
                    }
                }
            }
            below.withIndex().zipWithNext { (j, left), (_, right) ->
                if (above[j] != 9 && left != 9) {
                    ds.merge(i * colSize + j, (i + 1) * colSize + j)
                }
                if (left != 9 && right != 9) {
                    ds.merge((i + 1) * colSize + j, (i + 1) * colSize + j + 1)
                }
                if (above[j + 1] != 9 && right != 9) {
                    ds.merge(i * colSize + j + 1, (i + 1) * colSize + j + 1)
                }
            }
        }
        return ds.map {
            ds.sizeOf(it)
        }.sortedDescending()
            .take(3)
            .product()
    }

    val testInput = readInput("day09/Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("day09/Day09")
    println(part1(input))
    println(part2(input))
}
