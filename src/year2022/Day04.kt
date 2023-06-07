package year2022

import checkEq
import println
import readInput
import solve
import spread2
import toPair
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Unfortunately Kotlin doesn't have a good spread operator, so kind of made my own
 */
fun main() {
    day.println()

    fun IntRange.eitherContains(other: IntRange): Boolean =
        first <= other.first && last >= other.last || (other.first <= first && other.last >= last)

    fun IntRange.overlaps(other: IntRange): Boolean = other.first in this || first in other

    fun parseRange(s: String): IntRange = s.split("-").map { it.toInt() }.spread2(::IntRange)

    fun parse(line: String) = line.split(",").map(::parseRange).toPair()

    fun part1(input: List<String>) = input.map(::parse).count { (a, b) -> a.eitherContains(b) }

    fun part2(input: List<String>) = input.map(::parse).count { (a, b) -> a.overlaps(b) }

    checkEq(2, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(4, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}
