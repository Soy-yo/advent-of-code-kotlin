package day12

import readInput

class UndirectedGraph<T>(vararg edges: Pair<T, T>) {

    private val edges = buildMap<T, MutableList<T>> {
        for ((left, right) in edges) {
            if (!contains(left)) {
                put(left, mutableListOf())
            }
            getValue(left).add(right)
            if (!contains(right)) {
                put(right, mutableListOf())
            }
            getValue(right).add(left)
        }
    }

    fun neighbors(node: T) = edges.getValue(node).toList()

}

enum class PathMethod {
    REPEAT_UPPERCASE,
    AT_MOST_REPEAT_ONE_LOWERCASE
}

fun String.isLowerCase() = all(Char::isLowerCase)

fun pathsBetween(
    graph: UndirectedGraph<String>,
    from: String,
    to: String,
    method: PathMethod = PathMethod.REPEAT_UPPERCASE
): Int {
    val paths = mutableSetOf<List<String>>()
    fun dfs(
        start: String,
        seen: Set<String> = setOf(),
        path: MutableList<String> = mutableListOf(),
        repeated: Boolean = false
    ): Int {
        var count = 0
        val lowercase = start.isLowerCase()
        path.add(start)
        for (node in graph.neighbors(start)) {
            if (node in seen) {
                continue
            }
            if (node == to) {
                if (method == PathMethod.REPEAT_UPPERCASE) {
                    count++
                } else {
                    if (path !in paths) {
                        count++
                        paths.add(path)
                    }
                }
                continue
            }
            if (lowercase) {
                if (method == PathMethod.AT_MOST_REPEAT_ONE_LOWERCASE && !repeated && start != from) {
                    count += dfs(node, seen, path.toMutableList(), true)
                }
                count += dfs(node, seen + arrayOf(start), path.toMutableList(), repeated)
            } else {
                count += dfs(node, seen, path.toMutableList(), repeated)
            }
        }
        return count
    }

    return dfs(from)
}

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            line.split('-').let { (a, b) -> a to b }
        }.let {
            UndirectedGraph(*it.toTypedArray())
        }.let {
            pathsBetween(it, "start", "end")
        }

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.split('-').let { (a, b) -> a to b }
        }.let {
            UndirectedGraph(*it.toTypedArray())
        }.let {
            pathsBetween(it, "start", "end", PathMethod.AT_MOST_REPEAT_ONE_LOWERCASE)
        }

    val testInput = readInput("day12/Day12_test")
    check(part1(testInput) == 226)
    check(part2(testInput) == 3509)

    val input = readInput("day12/Day12")
    println(part1(input))
    println(part2(input))
}
