package year2022

import checkEq
import println
import readInput
import solve
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Kotlin made this one EASY: map toSet -> reduce intersection
 */
fun main() {
    day.println()
    fun getItemScore(ch: Char): Int {
        if (ch in 'a'..'z') {
            return ch.code - ('a'.code) + 1
        } else if (ch in 'A'..'Z') {
            return ch.code - ('A'.code) + 27
        }
        throw IllegalArgumentException()
    }

    fun getRucksackError(line: String): Char {
        val n = line.length / 2
        val intersect = line.subSequence(0, n).toSet().intersect(
            line.subSequence(n, line.length).toSet()
        )
        check(intersect.count() == 1)
        return intersect.toList().first()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val ch = getRucksackError(it)
            getItemScore(ch)
        }
    }

    fun getBadge(chunk: List<String>): Char {
        val badge = chunk.map { it.toSet() }.reduce {
            acc, cur -> acc.intersect(cur)
        }
        check(badge.count() == 1)
        return badge.toList().first()
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .sumOf { chunk ->
                val badge = getBadge(chunk)
                getItemScore(badge)
            }
    }

    checkEq(157, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(70, part2(readInput("${day}_ex")))

    println("Part 2:")
    solve { part2(input) }
}
