package year2022

import checkEq
import println
import readInput
import solve
import java.lang.invoke.MethodHandles
import kotlin.math.abs

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: the .also{println} trick was really useful for this one!
 */
fun main() {
    day.println()

    data class State(
        var clock: Int = 1, var x: Int = 1, val hist: MutableMap<Int, Int> = mutableMapOf(1 to 1)
    ) {
        operator fun get(time: Int): Int {
            var t = time
            var v = this.hist[t]
            while (v == null && t > 0) {
                v = this.hist[--t] // reverse probing
            }
            if (t <= 0 || v == null) {
                throw IllegalStateException("No time found for $time")
            }
            return v
        }

        fun save() {
            hist[clock] = x
        }

        fun score(): Int {
            return listOf(20, 60, 100, 140, 180, 220).sumOf { it * this[it] }
        }

        fun exec(cmd: String): State {
            when (cmd) {
                "noop" -> clock += 1
                else -> {
                    val arg = cmd.removePrefix("addx ").toInt()
                    clock += 2
                    x += arg
                    save()
                }
            }
            return this
        }
    }

    fun exec(input: List<String>): State {
        return input.fold(State()) { state, cmd -> state.exec(cmd) }
    }

    fun part1(input: List<String>): Int {
        return exec(input).score()
    }

    fun part2(input: List<String>): List<String> {
        val state = exec(input)
        val screen = mutableListOf<String>()
        for (cycleStart in (1..240 step 40)) {
            screen.add(buildString {
                for (cycle in (cycleStart until (cycleStart + 40))) {
                    val x = state[cycle] + 1
                    val pos = ((cycle - 1) % 40) + 1
                    if (abs(pos - x) <= 1) {
                        append('#')
                    } else {
                        append('.')
                    }
                }
            })
        }
        return screen
    }

    checkEq(-1, exec(readInput("${day}_ex0")).x)
    checkEq(13140, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    val expectedCrt = """##..##..##..##..##..##..##..##..##..##..
###...###...###...###...###...###...###.
####....####....####....####....####....
#####.....#####.....#####.....#####.....
######......######......######......####
#######.......#######.......#######.....""".lines()
    checkEq(expectedCrt, part2(readInput("${day}_ex")))
    solve { part2(input).joinToString(separator = "\n") }
}
