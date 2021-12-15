package day15

import readInput
import java.util.*

fun matrixDijsktra(graph: Array<IntArray>, start: Pair<Int, Int>): Array<IntArray> {
    fun neighbors(i: Int, j: Int) = buildList {
        if (i > 0) {
            add(Pair(i - 1, j))
        }
        if (i < graph.size - 1) {
            add(Pair(i + 1, j))
        }
        if (j > 0) {
            add(Pair(i, j - 1))
        }
        if (j < graph[i].size - 1) {
            add(Pair(i, j + 1))
        }
    }

    val distances = Array(graph.size) { i -> IntArray(graph[i].size) { Int.MAX_VALUE } }
    distances[start.first][start.second] = 0
    val remaining = PriorityQueue<Pair<Int, Int>>(compareBy { (i, j) -> distances[i][j] })
    remaining.add(start)
    while (remaining.isNotEmpty()) {
        val (i, j) = remaining.poll()
        for ((p, q) in neighbors(i, j)) {
            val d = distances[i][j] + graph[p][q]
            if (d < distances[p][q]) {
                // Reallocate (p, q) in the priority queue
                remaining.remove(Pair(p, q))
                distances[p][q] = d
                remaining.add(Pair(p, q))
            }
        }
    }
    return distances
}

fun repeatIntMatrix(
    matrix: Array<IntArray>,
    timesDown: Int = 1,
    timesRight: Int = 1,
    transform: ((Array<IntArray>, Pair<Int, Int>) -> Int)? = null
): Array<IntArray> {
    val result = Array(matrix.size * timesDown) { i ->
        IntArray(matrix[i % matrix.size].size * timesRight) { 0 }
    }
    if (transform === null) {
        return result
    }
    for ((i, row) in result.withIndex()) {
        for (j in row.indices) {
            result[i][j] = transform(result, Pair(i, j))
        }
    }
    return result
}

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            line.chunked(1).map(String::toInt).toIntArray()
        }.toTypedArray().let {
            val distances = matrixDijsktra(it, Pair(0, 0))
            distances.last().last()
        }

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.chunked(1).map(String::toInt).toIntArray()
        }.toTypedArray().let {
            val full = repeatIntMatrix(it, 5, 5) { arr, (i, j) ->
                val p = i - it.size
                if (p < 0) {
                    val q = j - it[i % it.size].size
                    if (q < 0) {
                        return@repeatIntMatrix it[i][j]
                    } else {
                        return@repeatIntMatrix arr[i][q] % 9 + 1
                    }
                } else {
                    return@repeatIntMatrix arr[p][j] % 9 + 1
                }
            }
            val distances = matrixDijsktra(full, Pair(0, 0))
            distances.last().last()
        }

    val testInput = readInput("day15/Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("day15/Day15")
    println(part1(input))
    println(part2(input))
}
