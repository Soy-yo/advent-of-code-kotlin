package day19

import readInput
import kotlin.math.absoluteValue

enum class Rotation(private val f: (Triple<Axis, Axis, Axis>) -> Triple<Axis, Axis, Axis>) {
    // Shitty names...
    IDENTITY({ (x, y, z) -> Triple(x, y, z) }),
    SYM_FIRST({ (x, y, z) -> Triple(x, -y, -z) }),
    SWAP_2_3({ (x, y, z) -> Triple(x, z, -y) }),
    SWAP_2_3_INV({ (x, y, z) -> Triple(x, -z, y) }),

    SWAP_1_2({ (x, y, z) -> Triple(y, x, -z) }),
    SWAP_1_2_INV({ (x, y, z) -> Triple(y, -x, z) }),
    ROLL_LEFT({ (x, y, z) -> Triple(y, z, x) }),
    ROLL_LEFT_INV({ (x, y, z) -> Triple(y, -z, -x) }),

    ROLL_RIGHT({ (x, y, z) -> Triple(z, x, y) }),
    ROLL_RIGHT_INV({ (x, y, z) -> Triple(z, -x, -y) }),
    SWAP_1_3({ (x, y, z) -> Triple(z, y, -x) }),
    SWAP_1_3_INV({ (x, y, z) -> Triple(z, -y, x) }),

    SYM_SECOND({ (x, y, z) -> Triple(-x, y, -z) }),
    SYM_THIRD({ (x, y, z) -> Triple(-x, -y, z) }),
    SWAP_3_2({ (x, y, z) -> Triple(-x, z, y) }),
    SWAP_3_2_INV({ (x, y, z) -> Triple(-x, -z, -y) }),

    SWAP_2_1({ (x, y, z) -> Triple(-y, x, z) }),
    SWAP_2_1_INV({ (x, y, z) -> Triple(-y, -x, -z) }),
    LEFT_ROLL({ (x, y, z) -> Triple(-y, z, -x) }),
    LEFT_ROLL_INV({ (x, y, z) -> Triple(-y, -z, x) }),

    RIGHT_ROLL({ (x, y, z) -> Triple(-z, x, -y) }),
    RIGHT_ROLL_INV({ (x, y, z) -> Triple(-z, -x, y) }),
    SWAP_3_1({ (x, y, z) -> Triple(-z, y, x) }),
    SWAP_3_1_INV({ (x, y, z) -> Triple(-z, -y, -x) });

    operator fun invoke(axes: Triple<Axis, Axis, Axis>) = f(axes)
}

data class Point(val x: Int, val y: Int, val z: Int) {

    operator fun plus(p: Point) = Point(x + p.x, y + p.y, z + p.z)
    operator fun minus(p: Point) = Point(x - p.x, y - p.y, z - p.z)
    operator fun unaryMinus() = Point(-x, -y, -z)

    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }
    operator fun get(c: Char) = when (c) {
        'x' -> x
        'y' -> y
        'z' -> z
        else -> throw IndexOutOfBoundsException()
    }
    operator fun get(s: String) = when (s) {
        "x" -> x
        "y" -> y
        "z" -> z
        else -> throw IndexOutOfBoundsException()
    }

    fun distanceTo(p: Point) = (x - p.x).absoluteValue + (y - p.y).absoluteValue + (z - p.z).absoluteValue

    override fun toString(): String = "($x, $y, $z)"
}

data class Axis(val coordinate: Char, val direction: Int) {
    operator fun unaryMinus() = Axis(coordinate, -direction)
}

val STANDARD_AXES = Triple(
    Axis('x', 1),
    Axis('y', 1),
    Axis('z', 1)
)

class PointSystem(
    points: Iterable<Point>,
    val origin: Point = Point(0, 0, 0),
    private val axes: Triple<Axis, Axis, Axis> = STANDARD_AXES
) {

    private val absolutePoints = points
    val points get() = absolutePoints.map { translate(it) }

    fun withCenter(center: Point) = PointSystem(points, translate(center))

    fun rotatedBy(rotation: Rotation) = PointSystem(absolutePoints, origin, rotation(axes))

    fun distancesTo(p: Point) = translate(p).let { tp -> points.map { it.distanceTo(tp) } }

    private fun translate(point: Point) = inAxes(point - origin)

    private fun inAxes(point: Point) =
        Point(
            point[axes.first.coordinate] * axes.first.direction,
            point[axes.second.coordinate] * axes.second.direction,
            point[axes.third.coordinate] * axes.third.direction
        )

}

fun hasMatchingDistances(distances1: List<Int>, distances2: List<Int>, n: Int = 12): Boolean {
    var j = 0
    var count = 0
    for (d in distances1) {
        while (j < distances2.size && distances2[j] < d) {
            j++
        }
        if (j == distances2.size) {
            break
        }
        if (d == distances2[j]) {
            count++
            j++
        }
    }
    return count >= n
}

fun getMatchingPoints(system1: PointSystem, system2: PointSystem): List<Pair<Int, Int>> = buildList {
    for ((i, point) in system1.points.withIndex()) {
        val j = system2.points.indexOf(point)
        if (j >= 0) {
            add(i to j)
        }
    }
}

fun parseInput(input: List<String>): List<PointSystem> = buildList {
    with(input.iterator()) {
        while (hasNext()) {
            next()
            val points = buildList {
                while (hasNext()) {
                    val line = next()
                    if (line.isBlank()) {
                        break
                    }
                    val (x, y, z) = line.split(',').map(String::toInt)
                    add(Point(x, y, z))
                }
            }
            add(PointSystem(points))
        }
    }
}

fun reduceSystems(initialSystems: List<PointSystem>, onlyCenters: Boolean = false): PointSystem {
    val systems = initialSystems.toMutableList()
    val centerSystems = systems.map {
        PointSystem(listOf(Point(0, 0, 0)))
    }.toMutableList()

    fun loop(): Boolean {
        for ((i, system) in systems.withIndex()) {
            for ((j, otherSystem) in systems.withIndex().drop(i + 1)) {
                for (point in system.points.dropLast(11)) {
                    val distances = system.distancesTo(point).sorted()
                    for (otherPoint in otherSystem.points) {
                        val otherDistances = otherSystem.distancesTo(otherPoint).sorted()
                        if (!hasMatchingDistances(distances, otherDistances)) {
                            continue
                        }
                        val s1 = system.withCenter(point)
                        for (rotation in Rotation.values()) {
                            val s2 = otherSystem.withCenter(otherPoint).rotatedBy(rotation)
                            val matches = getMatchingPoints(s1, s2)
                            if (matches.size >= 12) {
                                val newPoints = s2.points.map { it + point }.toMutableList()
                                if (onlyCenters) {
                                    val newCenters = centerSystems[j]
                                        .withCenter(otherPoint)
                                        .rotatedBy(rotation)
                                        .points.map { it + point }
                                        .toMutableList()
                                    centerSystems[i] = PointSystem(centerSystems[i].points.union(newCenters).toList())
                                    centerSystems.removeAt(j)
                                }
                                systems[i] = PointSystem((system.points.union(newPoints)).toList())
                                systems.removeAt(j)
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    @Suppress("ControlFlowWithEmptyBody")
    while (loop()) {}

    if (onlyCenters) {
        return centerSystems[0]
    }

    return systems[0]
}

fun main() {
    fun part1(input: List<String>): Int = reduceSystems(parseInput(input)).points.size

    fun part2(input: List<String>): Int {
        val system = reduceSystems(parseInput(input), onlyCenters = true)
        return system.points.flatMap { system.distancesTo(it) }.maxOrNull()!!
    }

    val testInput = readInput("day19/Day19_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("day19/Day19")
    println(part1(input))
    println(part2(input))
}
