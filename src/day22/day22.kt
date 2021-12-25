package day22

import readInput

data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x, $y, $z)"
}

data class SideLength(val x: Int, val y: Int, val z: Int)

val IntRange.size get() = last - first + 1

fun IntRange.overlaps(other: IntRange) =
    first in other || last in other || other.first in this || other.last in this

operator fun IntRange.contains(other: IntRange) = first <= other.first && other.last <= last

fun IntRange.rangeIntersection(other: IntRange): IntRange =
    when {
        first in other -> if (last in other) this else first..other.last
        last in other -> other.first..last
        other in this -> other
        else -> IntRange.EMPTY
    }

fun IntRange.rangeMinus(other: IntRange): List<IntRange> =
    when {
        this in other -> emptyList()
        other.last < first || last < other.first -> listOf(this)
        other.last in this && other.first !in this -> listOf(other.last+1..last)
        other.first in this && other.last !in this -> listOf(first until other.first)
        else -> listOf(first until other.first, other.last+1..last)
    }

class Cube(val origin: Point, val length: SideLength) {

    val xs get() = origin.x until origin.x + length.x
    val ys get() = origin.y until origin.y + length.y
    val zs get() = origin.z until origin.z + length.z

    constructor(origin: Point, xLength: Int, yLength: Int, zLength: Int) :
            this(origin, SideLength(xLength, yLength, zLength))

    constructor(xs: IntRange, ys: IntRange, zs: IntRange) :
            this(Point(xs.first, ys.first, zs.first), xs.size, ys.size, zs.size)

    fun volume() = xs.size.toLong() * ys.size.toLong() * zs.size.toLong()

    fun intersects(c: Cube): Boolean =
        xs.overlaps(c.xs) && ys.overlaps(c.ys) && zs.overlaps(c.zs)

    operator fun plus(c: Cube): List<Cube> =
        when {
            !intersects(c) -> listOf(this, c)
            c in this -> listOf(this)
            else -> brokenBy(c) + listOf(c)
        }

    operator fun minus(c: Cube): List<Cube> =
        when {
            !intersects(c) -> listOf(this)
            this in c -> emptyList()
            else -> brokenBy(c)
        }

    operator fun contains(p: Point) = p.x in xs && p.y in ys && p.z in zs

    operator fun contains(c: Cube) = c.xs in xs && c.ys in ys && c.zs in zs

    override fun toString() = "[Cube$origin -> $length]"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as Cube

        return origin == other.origin && length == other.length
    }

    override fun hashCode(): Int {
        return 31 * origin.hashCode() + length.hashCode()
    }

    private fun brokenBy(c: Cube): List<Cube> {
        fun informedList(minus: List<IntRange>, int: IntRange): List<Pair<IntRange, Char>> {
            val m = minus.zip(Array(minus.size) { 'm' })
            return if (int === IntRange.EMPTY) {
                m
            } else {
                m + listOf(int to 'i')
            }
        }

        val minusXs = xs.rangeMinus(c.xs)
        val minusYs = ys.rangeMinus(c.ys)
        val minusZs = zs.rangeMinus(c.zs)

        val intXs = xs.rangeIntersection(c.xs)
        val intYs = ys.rangeIntersection(c.ys)
        val intZs = zs.rangeIntersection(c.zs)

        val allXs = informedList(minusXs, intXs)
        val allYs = informedList(minusYs, intYs)
        val allZs = informedList(minusZs, intZs)

        return buildList {
            for ((x, tx) in allXs) {
                for ((y, ty) in allYs) {
                    for ((z, tz) in allZs) {
                        if (tx == 'i' && ty == 'i' && tz == 'i') {
                            continue
                        }
                        add(Cube(x, y, z))
                    }
                }
            }
        }.filter { it.volume() > 0 }
    }

}

fun parseInput(input: List<String>) =
    input.map { line ->
        val (xs, ys, zs) = line.split(' ').last()
            .split(Regex(",?[xyz]="))
            .drop(1)
            .map {
                it.split("..")
                    .map(String::toInt).let { (a, b) ->
                        a..b
                    }
            }
        Cube(xs, ys, zs) to line.startsWith("on")
    }

fun turnedOn(cubes: List<Pair<Cube, Boolean>>): Long {
    val ignore = mutableListOf<Cube>()
    var volume = 0L
    for ((cube, on) in cubes.reversed()) {
        if (on) {
            var miniCubes = listOf(cube)
            val hidden = ignore.any { cube in it }
            if (!hidden) {
                for (c in ignore) {
                    if (!cube.intersects(c)) {
                        continue
                    }
                    miniCubes = miniCubes.filter { it !in c }.flatMap { it - c }
                }
                volume += miniCubes.sumOf { it.volume() }
                ignore.removeAll { it in cube }
                ignore.add(cube)
            }
        } else {
            ignore.removeAll { it in cube }
            ignore.add(cube)
        }
    }
    return volume
}

fun main() {
    fun part1(input: List<String>): Long {
        val cubes = parseInput(input)
        val region = Cube(-50..50, -50..50, -50..50)
        return turnedOn(cubes.filter { (c, _) -> c in region })
    }

    fun part2(input: List<String>): Long {
        val cubes = parseInput(input)
        return turnedOn(cubes)
    }

    val testInput = readInput("day22/Day22_test")
    check(part1(testInput) == 474140L)
    check(part2(testInput) == 2758514936282235)

    val input = readInput("day22/Day22")
    println(part1(input))
    println(part2(input))
}
