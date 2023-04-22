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
private enum class LR {L, R;}
private enum class NSEW{N,S,E,W;

    fun rotateLeft(): NSEW = when(this) {
        N -> W
        S -> E
        E -> N
        W -> S
    }

    fun rotateRight(): NSEW = when(this) {
        N -> E
        S -> W
        E -> S
        W -> N
    }
}

private typealias Inst = Either<LR, Int> // either "L"|"R" or Instructions
private typealias Grid = Table<Row, Col, Char>

private data class Pos(var r: Row, var c: Col, var face: NSEW) {
    fun follow(inst: Either<LR, Int>, table: Table<Row, Col, Char>) {
        when (inst) {
            is Either.Left -> {
                when(inst.leftOrNull()!!) {
                    LR.L -> face = face.rotateLeft()
                    LR.R -> face = face.rotateRight()
                }
            }
            is Either.Right -> {
                repeat(inst.getOrNull()!!) {
                    moveToNext(table)
                }
            }
        }
    }

    private fun moveToNext(table: Table<Row, Col, Char>) {
        var (nxtR, nxtC) = when(face) {
            N -> r -1 to c
            S -> r+1 to c
            E -> r to c+1
            W -> r to c-1
        }
        if (table[nxtR, nxtC] == null) {
            when(face) {
                N -> {
                    nxtR = table.rowKeySet().sortedDescending().first {
                        r -> table[r, c] != null
                    }
                }
                S -> {
                    nxtR = table.rowKeySet().sorted().first {
                            r -> table[r, c] != null
                    }
                }
                E -> {
                    nxtC = table.columnKeySet().sorted().first {
                        c -> table[r, c] != null
                    }
                }
                W -> {
                    nxtC = table.columnKeySet().sortedDescending().first {
                            c -> table[r, c] != null
                    }
                }
            }
        }
        if (table[nxtR, nxtC] == '.') {
            r = nxtR
            c = nxtC
        }
    }

    fun score(): Int =
        (r+1) * 1000 + 4 * (c+1) + when(face) {
            N -> 3
            S -> 1
            E -> 0
            W -> 2
        }

    companion object {
        fun firstPos(grid: Grid): Pos {
            for (r in grid.rowKeySet().sorted()) {
                for (c in grid.columnKeySet().sorted()) {
                    if (grid[r,c] == '.')
                        return Pos(r, c, E)
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
        TODO()
    }

    checkEq(6032, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}
