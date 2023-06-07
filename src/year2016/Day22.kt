package year2016

import ints
import println
import readInput
import solve
import java.lang.invoke.MethodHandles
import kotlin.math.*

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

data class Node(
    val x: Int,
    val y: Int,
    val size: Int,
    val used: Int,
    val avail: Int,
)

fun main() {
    day.println()


    fun part1(input: List<String>): Int {
        // Filesystem              Size  Used  Avail  Use%    x0 y1  s2  u3  a4  %5
        // /dev/grid/node-x0-y0     89T   65T    24T   73% => [0, 0, 89, 65, 24, 73],
        val nodes = input.drop(2).map { line -> line.ints() }.map { ints ->
            Node(x = ints[0], y = ints[1], size = ints[2], used = ints[3], avail = ints[4])
        }
        var sum = 0
        for (a in nodes) {
            if (a.used == 0) continue
            sum += nodes.count { it.avail >= a.used && !(it.x == a.x && it.y == a.y) }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

//    checkEq(0, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}