package day20

import readInput

val MAPPING = mapOf('.' to 0, '#' to 1)

fun <T> String.map(mapping: Map<Char, T>) = map { mapping.getValue(it) }

operator fun Array<IntArray>.get(iSlice: IntRange, jSlice: IntRange): Array<IntArray> {
    val rowIndices = iSlice.filter { it in indices }
    val colIndices = jSlice.filter { it in first().indices }
    return Array(rowIndices.size) { i ->
        IntArray(colIndices.size) { j ->
            get(rowIndices[i])[colIndices[j]]
        }
    }
}

fun parseImage(image: List<String>, pad: Int = 0) =
    Array(image.size + 2 * pad) { i ->
        IntArray(image[0].length + 2 * pad) { j ->
            if (i - pad !in image.indices || j - pad !in image[0].indices) {
                0
            } else {
                MAPPING.getValue(image[i - pad][j - pad])
            }
        }
    }

fun pseudoConv(image: Array<IntArray>, template: List<Int>, kernel: Int = 3, extend: Int? = null): Array<IntArray> {
    if (template.first() == 1 && template.last() != 0) {
        throw IllegalArgumentException()
    }
    val offset = kernel / 2
    val result = Array(image.size) { IntArray(image[0].size) }
    for (i in image.indices) {
        for (j in image[i].indices) {
            if (extend !== null && (
                        i - offset !in image.indices ||
                        i + offset !in image.indices ||
                        j - offset !in image[0].indices ||
                        j + offset !in image[0].indices
            )) {
                result[i][j] = extend
            } else {
                val img = image[i-offset..i+offset, j-offset..j+offset]
                val n = img
                    .joinToString("") { it.joinToString("") }
                    .toInt(2)
                result[i][j] = template[n]
            }
        }
    }
    return result
}

fun convolveN(image: Array<IntArray>, template: List<Int>, n: Int = 1): Int {
    var extend = if (template.first() == 1) 1 else null
    var img = image
    repeat (n) {
        img = pseudoConv(img, template, extend = extend)
        if (extend !== null) {
            extend = 1 - extend!!
        }
    }
    return img.sumOf { it.sum() }
}

fun main() {
    fun part1(input: List<String>): Int {
        val template = input[0].map(MAPPING)
        val image = parseImage(input.drop(2), pad = 5)
        return convolveN(image, template, 2)
    }

    fun part2(input: List<String>): Int {
        val template = input[0].map(MAPPING)
        val image = parseImage(input.drop(2), pad = 55)
        return convolveN(image, template, 50)
    }

    val testInput = readInput("day20/Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("day20/Day20")
    println(part1(input))
    println(part2(input))
}
