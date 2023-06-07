package year2022

import checkEq
import println
import readInput
import solve
import java.lang.invoke.MethodHandles
import java.util.LinkedList

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: always spot check expectations
 * The `check` on part 1 let me know what was going "wrong"
 */

val KEY = 811589153L

fun main() {
    day.println()

    data class Data(val originalIndex: Int, val value: Long)

    class Buffer(val buf: LinkedList<Data>) {
        fun mix(xs: List<Data>) {
            for (xi in xs.indices) {
                val x = xs[xi]
                val oldBufIndex= buf.indexOf(x)
                buf.removeAt(oldBufIndex)
                val newIndex = (oldBufIndex + x.value).mod(buf.size)
                buf.add(newIndex, x)
            }
        }

        operator fun get(idx: Long) = buf[idx.mod(buf.size) ]
    }

    fun part1(input: List<String>): Long {
        val origs = input.map { it.toLong() }.mapIndexed {i, v -> Data(i, v)}
        check(origs.size == origs.toSet().size)
        val buf = Buffer(buf = LinkedList(origs))
        buf.mix(origs)
        val zeroIdx = buf.buf.indexOfFirst { it.value == 0L }
        return listOf(1000L, 2000L, 3000L)
            .map { buf[zeroIdx+it].value }
            .also { println(it) }
            .sum()
    }

    fun part2(input: List<String>): Long {
        val origs = input.map { it.toLong() * KEY }.mapIndexed {i, v -> Data(i, v)}
        check(origs.size == origs.toSet().size)
        val buf = Buffer(buf = LinkedList(origs))
        repeat(10) {
            buf.mix(origs)
        }
        val zeroIdx = buf.buf.indexOfFirst { it.value == 0L }
        return listOf(1000L, 2000L, 3000L)
            .map { buf[zeroIdx+it].value }
            .also { println(it) }
            .sum()
    }

    checkEq(3L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    println("Part 2:")
    checkEq(1623178306L, part2(readInput("${day}_ex")))
    solve { part2(input) }
}
