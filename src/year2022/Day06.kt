package year2022

import checkEq
import org.eclipse.collections.impl.bag.mutable.HashBag
import println
import readInput
import solve
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Eclipse Collection's Bag is like a HashMap<T, Int> that keeps track of how many. Most importantly, it has a fast
 * sizeDistinct which gives the total count of distinct items. Perfect for this.
 *
 * Also, I'm thrilled that it just worked perfectly with Kotlin. Just added to build.gradle and boom!
 */
fun main() {
    day.println()

    fun start(input: String, n: Int): Int {
        val chars = input.toCharArray()
        val bag = HashBag<Char>()
        var i = 0
        while (bag.sizeDistinct() != n) {
            chars.getOrNull(i-n)?.let { bag.remove(it) }
            bag.add(chars[i++])
        }
        return i
    }

    fun part1(input: List<String>) = input.map { start(it, 4) }

    fun part2(input: List<String>) = input.map { start(it, 14) }

    checkEq(listOf(7, 5, 6, 10, 11), part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(listOf(19, 23, 23, 29, 26), part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}
