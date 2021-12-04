package day04

import readInput

data class Position(val row: Int, val col: Int)

class BingoBoard(board: List<List<Int>>) {

    private val numbers = buildMap {
        for ((i, row) in board.withIndex()) {
            for ((j, number) in row.withIndex()) {
                put(number, Position(i, j))
            }
        }
    }.toMutableMap()

    private val rowCounts = IntArray(board.size) { board[0].size }

    private val colCounts = IntArray(board[0].size) { board.size }

    var value = numbers.keys.sum()
        private set

    var completed = false
        private set

    fun mark(number: Int): Boolean {
        if (completed || number !in numbers.keys) {
            return false
        }
        val (row, col) = numbers.remove(number)!!
        value -= number
        rowCounts[row]--
        colCounts[col]--
        completed = rowCounts[row] == 0 || colCounts[col] == 0
        return completed
    }

}

fun readBoards(input: List<String>): List<BingoBoard> {
    var board = mutableListOf<List<Int>>()
    val bingoBoards = mutableListOf<BingoBoard>()
    for (line in input) {
        if (line.isBlank() && board.isNotEmpty()) {
            bingoBoards.add(BingoBoard(board))
            board = mutableListOf()
        } else {
            board.add(line.trim().split(Regex("\\s+")).map(String::toInt))
        }
    }
    // For the remaining board
    if (board.isNotEmpty()) {
        bingoBoards.add(BingoBoard(board))
    }
    return bingoBoards
}

fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input[0].split(',').map(String::toInt)
        val bingoBoards = readBoards(input.drop(2))
        for (number in numbers) {
            for (bingoBoard in bingoBoards) {
                if (bingoBoard.mark(number)) {
                    return number * bingoBoard.value
                }
            }
        }
        return 0
    }

    fun part2(input: List<String>): Int {
        val numbers = input[0].split(',').map(String::toInt)
        var bingoBoards = readBoards(input.drop(2))
        var remaining = bingoBoards.size
        for (number in numbers) {
            val remainingBoards = mutableListOf<BingoBoard>()
            for (bingoBoard in bingoBoards) {
                if (bingoBoard.mark(number)) {
                    remaining--
                    if (remaining == 0) {
                        return number * bingoBoard.value
                    }
                } else {
                    remainingBoards.add(bingoBoard)
                }
            }
            bingoBoards = remainingBoards
        }
        return 0
    }

    val testInput = readInput("day04/Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("day04/Day04")
    println(part1(input))
    println(part2(input))
}
