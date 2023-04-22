import NSEW.*
import Pos.Companion.firstPos
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/*
NOTES
read into a table<IntRow, IntCol, Char>, '.' is open, '#' is wall, null is off board

Part 2 looks incredibly tedious. Will come back to that one.
 */

private typealias Row = Int
private typealias Col = Int

private enum class LR { L, R; }
private enum class NSEW {
    N, S, E, W;

    fun rotateLeft(): NSEW = when (this) {
        N -> W
        S -> E
        E -> N
        W -> S
    }

    fun rotateRight(): NSEW = when (this) {
        N -> E
        S -> W
        E -> S
        W -> N
    }
}

private typealias Inst = Either<LR, Int> // either "L"|"R" or Instructions
private typealias Grid = Table<Row, Col, Char>

private var LEN = 0

private data class Pos(var r: Row, var c: Col, var face: NSEW) {
    fun follow(inst: Either<LR, Int>, table: Table<Row, Col, Char>, part2: Boolean = false) {
        when (inst) {
            is Either.Left -> {
                when (inst.leftOrNull()!!) {
                    LR.L -> face = face.rotateLeft()
                    LR.R -> face = face.rotateRight()
                }
            }

            is Either.Right -> {
                repeat(inst.getOrNull()!!) {
                    if (part2) {
                        moveToNextWrapping(table)
                    } else {
                        moveToNext(table)

                    }
                }
            }
        }
    }

    private fun moveToNext(table: Table<Row, Col, Char>) {
        var (nxtR, nxtC) = when (face) {
            N -> r - 1 to c
            S -> r + 1 to c
            E -> r to c + 1
            W -> r to c - 1
        }
        if (table[nxtR, nxtC] == null) {
            when (face) {
                N -> {
                    nxtR = table.rowKeySet().sortedDescending().first { r ->
                        table[r, c] != null
                    }
                }

                S -> {
                    nxtR = table.rowKeySet().sorted().first { r ->
                        table[r, c] != null
                    }
                }

                E -> {
                    nxtC = table.columnKeySet().sorted().first { c ->
                        table[r, c] != null
                    }
                }

                W -> {
                    nxtC = table.columnKeySet().sortedDescending().first { c ->
                        table[r, c] != null
                    }
                }
            }
        }
        if (table[nxtR, nxtC] == '.') {
            r = nxtR
            c = nxtC
        }
    }

    private fun moveToNextWrapping(table: Table<Row, Col, Char>) {
        var (nxtR, nxtC) = when (face) {
            N -> r - 1 to c
            S -> r + 1 to c
            E -> r to c + 1
            W -> r to c - 1
        }
        var nxtFace = face
        if (table[nxtR, nxtC] == null) {
            // adapted from ClouddJR again:
            // https://github.com/ClouddJR/advent-of-code-2022/blob/main/src/main/kotlin/com/clouddjr/advent2022/Day22.kt#L47
            val x = c
            val y = r
            val trip = when (face to x/50 to y/50) {
                N to 1 to 0 -> 0 to (100 + x) to E
                W to 1 to 0 -> 0 to (149 - y) to E
                N to 2 to 0 -> (x - 100) to 199 to N
                E to 2 to 0 -> 99 to (149 - y) to W
                S to 2 to 0 -> 99 to (-50 + x) to W
                E to 1 to 1 -> (50 + y) to 49 to N
                W to 1 to 1 -> (y - 50) to 100 to S
                N to 0 to 2 -> 50 to (x + 50) to E
                W to 0 to 2 -> 50 to (149 - y) to E
                E to 1 to 2 -> 149 to (149 - y) to W
                S to 1 to 2 -> 49 to (100 + x) to W
                E to 0 to 3 -> (y - 100) to 149 to N
                S to 0 to 3 -> (x + 100) to 0 to S
                W to 0 to 3 -> (y - 100) to 0 to S
                else -> error("invalid state")
            }
            nxtC = trip.first.first // c = x
            nxtR = trip.first.second // r = y
            nxtFace = trip.second
        }
        checkNotNull(table[nxtR, nxtC])

        if (table[nxtR, nxtC] == '.') {
            r = nxtR
            c = nxtC
            face = nxtFace
        }
    }

    fun score(): Int = (r + 1) * 1000 + 4 * (c + 1) + when (face) {
        N -> 3
        S -> 1
        E -> 0
        W -> 2
    }

    companion object {
        fun firstPos(grid: Grid): Pos {
            for (r in grid.rowKeySet().sorted()) {
                for (c in grid.columnKeySet().sorted()) {
                    if (grid[r, c] == '.') return Pos(r, c, E)
                }
            }
            throw IllegalArgumentException()
        }
    }
}

fun main() {
    day.println()

    fun parseTable(input: List<String>): Grid =
        HashBasedTable.create<Row, Col, Char>(input.size, input[0].length).apply {
            input.forEachIndexed { r, line ->
                line.forEachIndexed { c, ch ->
                    if (ch == '.' || ch == '#') {
                        put(r, c, ch)
                    }
                }
            }
        }

    fun parseInstructions(s: String): List<Inst> = buildList {
        var rem = s
        while (rem.isNotEmpty()) {
            val inst = if (rem.startsWith("R") || rem.startsWith("L")) {
                val inst = rem[0]
                rem = rem.substring(1)
                LR.valueOf(inst.toString()).left()
            } else {
                val digs = rem.takeWhile { it in '0'..'9' }
                val inst = digs.toInt()
                rem = rem.substring(digs.length)
                inst.right()
            }
            add(inst)
        }
    }


    fun part1(input: List<String>): Int {
        val table = parseTable(input.subList(0, input.size - 2))
        val instructions = parseInstructions(input.last())
        var pos = firstPos(table)
        for (inst in instructions) {
            pos.follow(inst, table)
        }
        return pos.score()
    }

    fun part2(input: List<String>): Int {
        val table = parseTable(input.subList(0, input.size - 2))
        val instructions = parseInstructions(input.last())
        var pos = firstPos(table)
        for (inst in instructions) {
            pos.follow(inst, table, part2 = true)
        }
        return pos.score()
    }

    checkEq(6032, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}