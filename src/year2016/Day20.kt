package year2016

import println
import readInput
import solve
import java.lang.invoke.MethodHandles
import kotlin.math.*


/**
 * I think I've put off this problem for over six years...It's where I stopped with Go and Java.
 *
 * TIL Kotlin's built-in ranges made this so much easier. Even if it's really just a pair of numbers, having a built-in
 * type just makes this so much easier.
 *
 * Kotlin has unsigned types (UInt/ULong) but I'm not 100% sure they work (or at least I couldn't get them to work as
 * I expected them to)
 * I ended up solving part 2 by just going off the size of the list, rather than what I expected to be able to do which
 * was take the difference of each of the ranges.
 */
private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

@OptIn(ExperimentalUnsignedTypes::class)
fun main() {
    day.println()
    fun parseToRanges(input: List<String>): List<UIntRange> = input.map { it.split("-") }
        .map { (start, end) -> UIntRange(start.toUInt(), end.toUInt()) }
        .sortedBy { it.first }

    fun part1(input: List<String>): String {
        val ranges = parseToRanges(input)
        var x: UInt = ranges[0].last + 1u
        var ri = 0
        while (true) {
            if (x in ranges[ri]) {
                x = ranges[ri].last + 1u
                ri++
            } else if (x > ranges[ri].start) {
                ri++
            } else if (x < ranges[ri].start) {
                return x.toString()
            }
        }
    }

    fun mergeRanges(ranges: List<LongRange>): List<LongRange> {
        if (ranges.size <= 1) return ranges
        return ranges
            .sortedBy { it.first } // with sorted, we know that prev.start <= curr.start
            .fold(mutableListOf(ranges[0])) { acc, curr ->
                val prev = acc.last()
                if (curr.first - 1L > prev.last) {
                    // no overlap
                    acc.add(curr)
                } else  {
                    // overlap => merge
                    val merged = LongRange(
                        min(prev.first, curr.first),
                        max(prev.last, curr.last)
                    )
                    acc[acc.lastIndex] = merged
                }
                acc
            }
    }

    @ExperimentalUnsignedTypes
    fun part2(input: List<String>): String {
        val ranges = input.map { it.split("-") }
            .map { (start, end) -> LongRange(start.toLong(), end.toLong()) }
            .sortedBy { it.first }
        val merged = mergeRanges(ranges)
        return "${merged.size-1}"
    }

//    checkEq(0, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}