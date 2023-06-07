package year2022

import checkEq
import year2022.Blizzard.*
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import println
import readInput
import solve
import java.lang.invoke.MethodHandles
import java.util.*

/**
 * TIL: Not elegant or succinct in any way...but gives the answer.
 *
 * Like many AoC problems, picking the right compareTo function and good pruning is key for the broad search problems
 */
private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

private enum class Blizzard { U, D, L, R }

private fun toBlizzard(ch: Char): Blizzard = when (ch) {
    '^' -> U
    'v' -> D
    '>' -> R
    '<' -> L
    else -> error("not a blizzard char")
}

fun main() {
    day.println()


    data class Element(val wall: Boolean, val blzs: List<Blizzard>) {
        fun conj(b: Blizzard): Element {
            if (wall) error("")
            return copy(blzs = blzs.toMutableList().apply { add(b) }.toMutableList())
        }
    }

    val WALL = Element(true, emptyList())
    val GROUND = Element(false, emptyList())
    fun toElement(ch: Char): Element {
        return when (ch) {
            '#' -> WALL
            '.' -> Element(false, emptyList())
            else -> Element(false, listOf(toBlizzard(ch)))
        }
    }

    fun parse(input: List<String>): Table<Int, Int, Element> {
        return HashBasedTable.create<Int, Int, Element>().apply {
            input.mapIndexed { r, row -> row.mapIndexed { c, ch -> put(r, c, toElement(ch)) } }
        }
    }

    fun moveBlizzard(blz: Blizzard, r: Int, c: Int, table: Table<Int, Int, Element>): Pair<Int, Int> {
        var (rb, cb) = when (blz) {
            U -> r - 1 to c
            D -> r + 1 to c
            L -> r to c - 1
            R -> r to c + 1
        }
        return if (table[rb, cb]!!.wall) {
            // wrap
            when (blz) {
                U -> table.rowKeySet().max() - 1 to c
                D -> 1 to c
                L -> r to table.columnKeySet().max() - 1
                R -> r to 1
            }
        } else {
            rb to cb
        }
    }

    fun nextTable(table: Table<Int, Int, Element>): Table<Int, Int, Element> {
        val nxt = HashBasedTable.create<Int, Int, Element>().apply {
            table.rowKeySet().forEach { r ->
                table.columnKeySet().forEach { c ->
                    if (table.get(r, c)!!.wall) {
                        put(r, c, WALL)
                    } else {
                        put(r, c, GROUND)
                    }
                }
            }
        }

        table.rowKeySet().forEach { r ->
            table.columnKeySet().forEach { c ->
                val prev = table.get(r, c)!!
                for (blz in prev.blzs) {
                    val (rb, cb) = moveBlizzard(blz, r, c, table)
                    nxt.put(
                        rb, cb, nxt.get(rb, cb)!!.conj(blz)
                    )
                }
            }
        }
        return nxt
    }

    data class Path(val pos: Pair<Int, Int>, val steps: Int = 0, val trip: Int = 0) : Comparable<Path> {
        override fun compareTo(other: Path): Int {
            return -(pos.first + pos.second).compareTo(other.pos.first + other.pos.second)
        }
    }

    fun findEnd(table: Table<Int, Int, Element>): Pair<Int, Int> =
        table.rowKeySet().max() to (table.columnKeySet().max() - 1)

    fun part1(input: List<String>): Int {
        var minSteps: Int = Int.MAX_VALUE
        val tables = mutableListOf(parse(input))
        repeat(100) {
            tables.add(nextTable(tables.last()))
        }
        val start = Path(0 to 1)
        val end = findEnd(tables[0])
        val pq = PriorityQueue<Path>().apply { add(start) }
        val seen = mutableSetOf(start)
        while (pq.isNotEmpty()) {
            val p = pq.poll()
            seen.add(p)
            if (p.pos == end) {
                if (minSteps > p.steps) {
                    minSteps = p.steps
//                    println("new min found $minSteps")
                }
                continue
            }
            if (p.steps > minSteps) {
                continue
            }
            while (tables.size <= (p.steps + 1)) {
                tables.add(nextTable(tables.last()))
            }
            val t = tables[p.steps]
            val elemAtPos = t[p.pos.first, p.pos.second]!!
            if (elemAtPos.wall || elemAtPos.blzs.isNotEmpty()) {
                continue // destroy
            }
            val posbs = arrayListOf(0 to 0, -1 to 0, 1 to 0, 0 to -1, 0 to 1).map { (rd, cd) ->
                p.pos.first + rd to p.pos.second + cd
            }
            val nextT = tables[p.steps + 1]
            for (posb in posbs) {
                val elemAtNextPosb = nextT[posb.first, posb.second]
                if (elemAtNextPosb != null && !elemAtNextPosb.wall && elemAtNextPosb.blzs.isEmpty()) {
                    val newPath = Path(posb, p.steps + 1)
                    if (newPath !in seen) {
                        pq.add(newPath)
                    }
                }
            }
//            println(pq.size)
        }
        return minSteps
    }

    fun part2(input: List<String>): Int {
        var minSteps: Int = Int.MAX_VALUE
        val tables = mutableListOf(parse(input))
        repeat(1000) {
            tables.add(nextTable(tables.last()))
        }
        val start = Path(0 to 1)
        val end = findEnd(tables[0])
        val pq = PriorityQueue<Path> { a, b ->
            if (a.trip != b.trip) {
                -(a.trip.compareTo(b.trip))
            } else {
                when (a.trip) {
                    0, 2 -> -(a.pos.first + a.pos.second).compareTo(b.pos.first + b.pos.second)
                    else -> (a.pos.first + a.pos.second).compareTo(b.pos.first + b.pos.second)
                }
            }
        }.apply { add(start) }
        val seen = mutableSetOf(start)
        while (pq.isNotEmpty()) {
            val p = pq.poll()
            seen.add(p)
            if (p.pos == end && p.trip == 2) {
                if (minSteps > p.steps) {
                    minSteps = p.steps
//                    println("new min found $minSteps")
                }
                continue
            }
            if (p.steps > minSteps) {
                continue
            }
            while (tables.size <= (p.steps + 1)) {
                tables.add(nextTable(tables.last()))
            }
            val t = tables[p.steps]
            val elemAtPos = t[p.pos.first, p.pos.second]!!
            if (elemAtPos.wall || elemAtPos.blzs.isNotEmpty()) {
                continue // destroy
            }
            val posbs = arrayListOf(0 to 0, -1 to 0, 1 to 0, 0 to -1, 0 to 1).map { (rd, cd) ->
                p.pos.first + rd to p.pos.second + cd
            }
            val nextT = tables[p.steps + 1]
            for (posb in posbs) {
                val elemAtNextPosb = nextT[posb.first, posb.second]
                if (elemAtNextPosb != null && !elemAtNextPosb.wall && elemAtNextPosb.blzs.isEmpty()) {
                    val trip = when {
                        p.pos == end && p.trip == 0 -> 1
                        p.pos == start.pos && p.trip == 1 -> 2
                        else -> p.trip
                    }
                    val newPath = Path(posb, p.steps + 1, trip)
                    if (newPath !in seen) {
                        pq.add(newPath)
                    }
                }
            }
//            println(pq.size)
        }
        return minSteps
    }

    checkEq(18, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}
