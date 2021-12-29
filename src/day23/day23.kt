package day23

import readInput
import java.util.*
import kotlin.math.absoluteValue

typealias Position = Pair<Int, Int>

fun energy(name: Char) =
    when(name) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> throw IllegalArgumentException()
    }

fun destination(name: Char) =
    when(name) {
        'A' -> 3
        'B' -> 5
        'C' -> 7
        'D' -> 9
        else -> throw IllegalArgumentException()
    }

const val COLS = 13

val EXTRA = listOf(
    Amphipod('D', 3, 3),
    Amphipod('C', 3, 5),
    Amphipod('B', 3, 7),
    Amphipod('A', 3, 9),
    Amphipod('D', 4, 3),
    Amphipod('B', 4, 5),
    Amphipod('A', 4, 7),
    Amphipod('C', 4, 9),
)

data class Amphipod(val name: Char, val position: Position) {
    constructor(name: Char, i: Int, j: Int) : this(name, Pair(i, j))
}

class Burrow(
    val amphipods: List<Amphipod>,
    val energy: Int = 0,
    val locked: Set<Amphipod> = emptySet()
) {

    val rows = amphipods.maxOf { it.position.first } + 1

    val map = Array(rows) { i ->
        when (i) {
            0 -> CharArray(COLS) { '#' }
            1 -> CharArray(COLS) { j ->
                amphipods.singleOrNull {
                    it.position == Position(i, j)
                }?.name ?: if (j == 0 || j == COLS - 1) '#' else '.'
            }
            else -> {
                CharArray(COLS) { j ->
                    if (inRoom(Position(i, j))) {
                        amphipods.filter {
                            it.position == Position(i, j)
                        }.takeIf { it.size == 1 }
                            ?.single()?.name ?: '.'
                    } else {
                        '#'
                    }
                }
            }
        }
    }

    fun validMoves(): List<Pair<Amphipod, Position>> = buildList {
        fun MutableList<Pair<Amphipod, Position>>.addSides(amphipod: Amphipod) {
            val j0 = amphipod.position.second
            // Right
            for (j in j0 + 1..map[1].lastIndex) {
                if (!isFree(Position(1, j))) {
                    break
                }
                if (atDoor(Position(1, j))) {
                    continue
                }
                add(amphipod to Position(1, j))
            }
            // Left
            for (j in j0 - 1 downTo 0) {
                if (!isFree(Position(1, j))) {
                    break
                }
                if (atDoor(Position(1, j))) {
                    continue
                }
                add(amphipod to Position(1, j))
            }
        }

        for (amphipod in amphipods) {
            when {
                amphipod.inItsRoom() -> continue
                amphipod.inRoom() -> {
                    val dj = destination(amphipod.name)
                    // Always check if it can move to its room
                    if (amphipod.position.second != dj && amphipod.canMoveToItsRoom()) {
                        for (i in 2..map.lastIndex) {
                            if (isFree(Position(i, dj))) {
                                add(amphipod to Position(i, dj))
                            }
                        }
                        continue
                    }
                    val (i, j) = amphipod.position
                    if (!isFree(Position(i - 1, j))) {
                        continue
                    }
                    addSides(amphipod)
                }
                amphipod.inHallway() -> {
                    // Always check if it can move to its room
                    if (amphipod.canMoveToItsRoom()) {
                        val dj = destination(amphipod.name)
                        for (i in 2..map.lastIndex) {
                            if (isFree(Position(i, dj))) {
                                add(amphipod to Position(i, dj))
                            }
                        }
                        continue
                    }
                    if (amphipod !in locked) {
                        addSides(amphipod)
                    }
                }
            }
        }
    }

    fun distance(from: Position, to: Position): Int =
            (from.first - 1).absoluteValue + (from.second - to.second).absoluteValue + (1 - to.first).absoluteValue

    fun move(amphipod: Amphipod, to: Position): Burrow {
        val newAmphipods = amphipods.map { if (it === amphipod) Amphipod(amphipod.name, to) else it }
        val moveEnergy = energy(amphipod.name) * distance(amphipod.position, to)
        val newLocked = amphipods.filter { it.inHallway() && it !== amphipod }.toSet()
        return Burrow(newAmphipods, energy + moveEnergy, newLocked)
    }

    fun allSet() = amphipods.all { it.inItsRoom() }

    fun isFree(p: Position) =
        p.first in map.indices && p.second in map[p.first].indices && map[p.first][p.second] == '.'

    operator fun get(p: Position): Amphipod? = amphipods.singleOrNull { it.position == p }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as Burrow
        return amphipods == other.amphipods && locked == other.locked
    }

    override fun hashCode(): Int {
        return 31 * amphipods.hashCode() + locked.hashCode()
    }

    override fun toString() = map.joinToString("\n") { it.joinToString("") }

    private fun inHallway(p: Position) = p.first == 1

    private fun inRoom(p: Position) = p.first > 1 && p.second in 3..9 step 2

    private fun atDoor(p: Position) = inHallway(p) && p.second in 3..9 step 2

    private fun amphipodsInRoom(id: Int) = buildList {
        for (i in 2..map.lastIndex) {
            if (map[i][id] != '.') {
                add(map[i][id])
            }
        }
    }

    private fun Amphipod.canMoveToItsRoom(): Boolean {
        val dest = destination(name)
        if (!isFree(Position(2, dest)) || amphipodsInRoom(dest).any { it != name }) {
            return false
        }
        val (i, j) = position
        if ((2 until i).any { !isFree(Position(it, j)) }) {
            return false
        }
        // Check hallway
        if ((j+1..dest).any { !isFree(Position(1, it)) }) {
            return false
        }
        if ((dest until j).any { !isFree(Position(1, it)) }) {
            return false
        }

        return true
    }

    private fun Amphipod.inHallway() = inHallway(position)

    private fun Amphipod.inRoom() = inRoom(position)

    private fun Amphipod.inItsRoom() = inRoom() &&
            position.second == destination(name) &&
            amphipodsInRoom(destination(name)).all { it == name }
}

fun parseInput(input: List<String>): Burrow =
    input.withIndex().drop(2).flatMap { (i, line) ->
        line.withIndex()
            .filter { (_, c) -> c.isLetter() }
            .map { (j, c) -> Amphipod(c, i, j) }
    }.let { Burrow(it) }

fun parseInputWithExtra(input: List<String>): Burrow =
    input.withIndex().drop(2).flatMap { (i, line) ->
        line.withIndex()
            .filter { (_, c) -> c.isLetter() }
            .map { (j, c) -> if (i == 2) Amphipod(c, i, j) else Amphipod(c, i + 2, j) }
    }.let { Burrow(it + EXTRA) }

fun minEnergy(burrow: Burrow): Int = burrow.amphipods.sumOf {
    val energy = energy(it.name)
    val destination = destination(it.name)
    if (destination == it.position.second) {
        0
    } else {
        burrow.distance(it.position, Position(2, destination)) * energy
    }
}

fun simulate(burrow: Burrow): Int {
    var lowestEnergy: Int? = null
    val seen = mutableMapOf<Burrow, Int>()
    seen[burrow] = 0
    val pq = PriorityQueue<Pair<Burrow, Int>>(compareBy { it.second })
    pq.add(burrow to minEnergy(burrow))
    while (pq.isNotEmpty()) {
        val (current, expectedEnergy) = pq.poll()
        if (lowestEnergy !== null && expectedEnergy >= lowestEnergy) {
            continue
        }
        for ((amphipod, destination) in current.validMoves()) {
            val next = current.move(amphipod, destination)
            if (next in seen && seen[next]!! <= next.energy) {
                continue
            }
            seen[next] = next.energy
            if (next.allSet()) {
                if (lowestEnergy == null || next.energy <= lowestEnergy) {
                    lowestEnergy = next.energy
                }
                continue
            }
            val nextExpectedEnergy = current.energy + minEnergy(next)
            if (lowestEnergy !== null && nextExpectedEnergy >= lowestEnergy) {
                continue
            }
            pq.add(next to nextExpectedEnergy)
        }
    }
    return lowestEnergy!!
}

fun main() {
    fun part1(input: List<String>) = simulate(parseInput(input))

    fun part2(input: List<String>) = simulate(parseInputWithExtra(input))

    val testInput = readInput("day23/Day23_test")
    check(part1(testInput) == 12521)
    check(part2(testInput) == 44169)

    val input = readInput("day23/Day23")
    println(part1(input))
    println(part2(input))
}
