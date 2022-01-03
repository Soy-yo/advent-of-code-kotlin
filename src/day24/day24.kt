package day24

import readInput

fun String.isInt() = toIntOrNull() !== null

class Parser(memory: Map<String, Long> = mapOf("w" to 0L, "x" to 0L, "y" to 0L, "z" to 0L)) {

    private val memory = memory.toMutableMap()

    fun parse(program: List<String>, input: String = ""): Map<String, Long> {
        val tokenizedInput = ArrayDeque(input.split(Regex("\\s+")))
        for (line in program) {
            val (instr, args) = line.split(' ', limit = 2)
            when (instr) {
                "inp" -> inp(tokenizedInput, args)
                else -> {
                    val (a, b) = args.split(" ")
                    when (instr) {
                        "add" -> add(a, b)
                        "mul" -> mul(a, b)
                        "div" -> div(a, b)
                        "mod" -> mod(a, b)
                        "eql" -> eql(a, b)
                        else -> throw IllegalArgumentException("instruction: $line")
                    }
                }
            }
        }
        return memory
    }

    private fun inp(input: ArrayDeque<String>, a: String) {
        memory[a] = input.removeFirst().toLong()
    }

    private fun add(a: String, b: Int) {
        memory[a] = memory.getValue(a) + b
    }

    private fun add(a: String, b: String) {
        if (b.isInt()) {
            add(a, b.toInt())
        } else {
            memory[a] = memory.getValue(a) + memory.getValue(b)
        }
    }

    private fun mul(a: String, b: Int) {
        memory[a] = memory.getValue(a) * b
    }

    private fun mul(a: String, b: String) {
        if (b.isInt()) {
            mul(a, b.toInt())
        } else {
            memory[a] = memory.getValue(a) * memory.getValue(b)
        }
    }

    private fun div(a: String, b: Int) {
        if (b == 0) {
            throw IllegalArgumentException("a / 0")
        }
        memory[a] = memory.getValue(a) / b
    }

    private fun div(a: String, b: String) {
        if (b.isInt()) {
            div(a, b.toInt())
        } else {
            val bv = memory.getValue(b)
            if (bv == 0L) {
                throw IllegalArgumentException("a / 0")
            }
            memory[a] = memory.getValue(a) / bv
        }
    }

    private fun mod(a: String, b: Int) {
        val av = memory.getValue(a)
        if (av < 0 || b <= 0) {
            throw IllegalArgumentException("$av % $b")
        }
        memory[a] = av % b
    }

    private fun mod(a: String, b: String) {
        if (b.isInt()) {
            mod(a, b.toInt())
        } else {
            val av = memory.getValue(a)
            val bv = memory.getValue(b)
            if (av < 0 || bv <= 0) {
                throw IllegalArgumentException("$av % $bv")
            }
            memory[a] = av % bv
        }
    }

    private fun eql(a: String, b: Int) {
        memory[a] = if (memory.getValue(a) == b.toLong()) 1 else 0
    }

    private fun eql(a: String, b: String) {
        if (b.isInt()) {
            eql(a, b.toInt())
        } else {
            memory[a] = if (memory.getValue(a) == memory.getValue(b)) 1 else 0
        }
    }

}

fun main() {
    fun part1(input: List<String>) {
        for (i1 in 1..9) {
            for (i2 in 1..9) {
                for (i3 in 1..9) {
                    for (i4 in 1..9) {
                        val x = "$i1 $i2 9 9 7 9 9 9 2 9 6 $i3 1 $i4"
                        val z = Parser().parse(input, x)["z"]!!
                        if (z == 0L) {
                            println(x.replace(" ", ""))
                        }
                    }
                }
            }
        }
        println("--------------------")
    }

    fun part2(input: List<String>) {
        for (i1 in 1..9) {
            for (i2 in 1..9) {
                for (i3 in 1..9) {
                    for (i4 in 1..9) {
                        for (i5 in 1..9) {
                            val x = "8 1 1 $i1 1 $i2 7 9 $i3 $i4 $i5 8 1 1"
                            val z = Parser().parse(input, x)["z"]!!
                            if (z == 0L) {
                                println(x.replace(" ", ""))
                            }
                        }
                    }
                }
            }
        }
        println("--------------------")
    }

    val input = readInput("day24/Day24")
    part1(input)
    part2(input)
}
