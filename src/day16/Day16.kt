package day16

import readInput

const val LITERAL_TYPE = 4

enum class Operator(private val f: (List<Packet>) -> Long) {
    ADD({ packets -> packets.sumOf { it() } }),
    PRODUCT({ packets -> packets.productOf { it() } }),
    MIN({ packets -> packets.minOf { it() } }),
    MAX({ packets -> packets.maxOf { it() } }),
    CTE({ packets -> packets[0]() }),
    GT({ (left, right) -> if (left() > right()) 1L else 0L }),
    LT({ (left, right) -> if (left() < right()) 1L else 0L }),
    EQ({ (left, right) -> if (left() == right()) 1L else 0L });

    operator fun invoke(packets: List<Packet>) = f(packets)
}

sealed class Packet(val version: Int) {
    abstract operator fun invoke(): Long
}

class LiteralPacket(val value: Long, version: Int) : Packet(version) {
    override operator fun invoke() = value
}

class OperatorPacket(val packets: List<Packet>, val operator: Operator, version: Int) : Packet(version) {
    override operator fun invoke() = operator(packets)
}

class PacketParser(private val packet: String) {

    private var index = 0

    fun parse(): Packet = parsePacket()

    private fun getString(from: Int, to: Int) = packet.substring(from, to)

    private fun parseInt(from: Int, to: Int) = getString(from, to).toInt(2)

    private fun parsePacket(): Packet {
        val (version, type) = parseHeader()
        return when (type) {
            LITERAL_TYPE -> LiteralPacket(parseLiteral(), version)
            else -> OperatorPacket(parseOperator(), Operator.values()[type], version)
        }
    }

    private fun parseHeader(): Pair<Int, Int> {
        val version = parseInt(index, index + 3)
        val type = parseInt(index + 3, index + 6)
        index += 6
        return Pair(version, type)
    }

    private fun parseLiteral(): Long = buildString {
        var last = false
        while (!last) {
            last = packet[index] == '0'
            append(getString(index + 1, index + 5))
            index += 5
        }
    }.toLong(2)

    private fun parseOperator(): List<Packet> {
        val lengthId = packet[index]
        index++
        if (lengthId == '0') {
            val length = parseInt(index, index + 15)
            index += 15
            val finalIndex = index + length
            return buildList {
                while (index != finalIndex) {
                    add(parsePacket())
                }
            }
        } else {
            val number = parseInt(index, index + 11)
            index += 11
            return buildList {
                repeat(number) {
                    add(parsePacket())
                }
            }
        }
    }

}

fun String.hexToBin(): String = buildString {
    for (c in this@hexToBin) {
        append(c.toString().toInt(16).toString(2).padStart(4, '0'))
    }
}

fun <T> Iterable<T>.productOf(transform: (T) -> Long): Long = fold(1L) { acc, x -> acc * transform(x) }

fun sumVersions(packet: Packet): Int =
    if (packet is OperatorPacket) {
        packet.version + packet.packets.sumOf { sumVersions(it) }
    } else {
        packet.version
    }


fun main() {
    fun part1(input: List<String>): Int =
        sumVersions(PacketParser(input[0].hexToBin()).parse())

    fun part2(input: List<String>): Long =
        PacketParser(input[0].hexToBin()).parse()()

    val input = readInput("day16/Day16")
    println(part1(input))
    println(part2(input))
}
