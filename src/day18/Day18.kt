package day18

import readInput
import java.util.*
import kotlin.collections.ArrayDeque

const val IN = -1
const val OUT = -2

class SnailfishNumber(private val number: LinkedList<Int>) {

    companion object {
        fun fromString(string: String) = LinkedList<Int>().run {
            for (c in string) {
                when (c) {
                    '[' -> addLast(IN)
                    in '0'..'9' -> addLast(Character.getNumericValue(c))
                    ']' -> addLast(OUT)
                }
            }
            SnailfishNumber(this)
        }
    }

    fun magnitude(): Int {
        var magnitude = 0
        var mult = 1
        val isLeft = ArrayDeque<Boolean>()

        fun prepare() {
            if (isLeft.last()) {
                mult /= 3
                isLeft[isLeft.lastIndex] = false
                mult *= 2
            } else {
                mult /= 2
                isLeft.removeLast()
            }
        }

        for (value in number) {
            when (value) {
                IN -> {
                    isLeft.addLast(true)
                    mult *= 3
                }
                OUT -> {
                    if (isLeft.isNotEmpty()) {
                        prepare()
                    }
                }
                else -> {
                    magnitude += value * mult
                    prepare()
                }
            }
        }
        return magnitude
    }

    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        val copy = LinkedList(number)
        copy.addFirst(IN)
        copy.addAll(other.number)
        copy.addLast(OUT)
        return SnailfishNumber(copy).reduced()
    }

    private fun reduced(): SnailfishNumber {
        while (true) {
            if (explode()) {
                continue
            }
            if (split()) {
                continue
            }
            break
        }
        return this
    }

    fun explode(): Boolean {
        val brackets = setOf(IN, OUT)
        with(number.listIterator()) {
            var depth = 0
            while (hasNext()) {
                val index = nextIndex()
                val value = next()
                when (value) {
                    IN -> depth++
                    OUT -> depth--
                    else -> if (depth == 5) {
                        // Find previous number, if any
                        with(number.listIterator(index)) {
                            while (hasPrevious()) {
                                val p = previous()
                                if (p !in brackets) {
                                    set(p + value)
                                    break
                                }
                            }
                        }
                        set(0)
                        val right = next()
                        remove()
                        // Remove brackets
                        next()
                        remove()
                        previous()
                        previous()
                        remove()
                        next()
                        while (hasNext()) {
                            val n = next()
                            if (n !in brackets) {
                                set(n + right)
                                break
                            }
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun split(): Boolean {
        with(number.listIterator()) {
            while (hasNext()) {
                val value = next()
                if (value > 9) {
                    set(IN)
                    add(value / 2)
                    add((value + 1) / 2)
                    add(OUT)
                    return true
                }
            }
        }
        return false
    }

}

fun Iterable<SnailfishNumber>.sum(): SnailfishNumber = reduce { acc, x -> acc + x }

fun main() {
    fun part1(input: List<String>): Int =
        input.map { SnailfishNumber.fromString(it) }
            .sum()
            .magnitude()

    fun part2(input: List<String>): Int =
        input.map { SnailfishNumber.fromString(it) }.let {
            var maxMagnitude = 0
            for (x in it) {
                for (y in it) {
                    if (x === y) {
                        continue
                    }
                    maxMagnitude = maxOf((x + y).magnitude(), maxMagnitude)
                }
            }
            return@let maxMagnitude
        }

    val testInput = readInput("day18/Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("day18/Day18")
    println(part1(input))
    println(part2(input))
}
