package year2022

import Cord
import SparseGrid
import checkEq
import println
import progressFromTo
import readInput
import solve
import toPair
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Today's ended up being pretty ugly.
 * DeepRecursiveFunction is really great! Takes the manual work out of converting
 * a function call into using a Stack.
 */
fun main() {
    day.println()
    fun part1(input: List<String>): Int {
        val cave = Cave(parse(input))
        while (true) {
            val sandCount = cave.addSand()
            if (sandCount != null) {
                return sandCount
            }
        }
    }

    fun part2(input: List<String>): Int {
        val cave = Cave(parse(input))
        val maxY = cave.grid.grid.values.map { col ->
            col.lastKey()
        }.max() + 2

        println("maxY = $maxY")
        cave.floor = maxY
        while (true) {
            val sandCount = cave.addSand()
            if (sandCount != null) {
                return sandCount
            }
        }
    }

    checkEq(24, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(93, part2(readInput("${day}_ex")))

    println("Part 2:")
    solve { part2(input) }
}

data class Cave(
    val grid: SparseGrid<Mats> = SparseGrid(),
    val start: Cord = 500 to 0,
    var sandCount: Int = 0,
    var floor: Int? = null
) {
    fun addSand(at: Cord = start): Int? {
        val recur = DeepRecursiveFunction<Cord, Int?> { at ->
            if (floor == null) {
                if (grid.grid[at.first].isNullOrEmpty() ||
                    grid.grid[at.first]!!.lastKey() <= at.second
                ) {
                    return@DeepRecursiveFunction sandCount
                }
            } else if (at.second + 1 == floor!!) {
                grid[at.first - 1 to floor!!] = Mats.Rock
                grid[at.first to floor!!] = Mats.Rock
                grid[at.first + 1 to floor!!] = Mats.Rock
            }
            val down = at.first to (at.second + 1)
            val mDown = grid[down]
            return@DeepRecursiveFunction if (mDown == null) {
                callRecursive(down)
            } else {
                // down is blocked => go down left
                val downLeft = (at.first - 1) to (at.second + 1)
                val mDownLeft = grid[downLeft]
                if (mDownLeft == null) {
                    callRecursive(downLeft)
                } else {
                    // down left blocked => go down right
                    val downRight = (at.first + 1) to (at.second + 1)
                    val mDownRight = grid[downRight]
                    if (mDownRight == null) {
                        callRecursive(downRight)
                    } else {
                        // all blocked => settle
                        grid[at] = Mats.Sand
                        sandCount++
                        if (at == start) {
                            sandCount
                        } else {
                            null
                        }
                    }
                }
            }
        }
        return recur(at)
    }
}

/*
 * x = dist right (col)
 * y = dist down (row)
 * 0...x
 * .
 * y
 *
 *
 */

enum class Mats {
    Rock,
    Sand
}

private fun parse(input: List<String>): SparseGrid<Mats> {
    val cave = SparseGrid<Mats>()
    input.map { lines ->
        val points =
            lines.split("->").map { it.trim() }.map { it.split(",") }.map { pairs -> pairs.map { s -> s.trim() } }
                .map { pairs -> pairs.map { s -> s.toInt() }.toPair() }
        val rockLines = points.windowed(2).map { it.toPair() }
        for ((fromCord, toCord) in rockLines) {
            val (xs, ys) = progressFromTo(fromCord, toCord)
            cave.putAlong(xs, ys, Mats.Rock)
        }
    }
    return cave
}

