import java.lang.invoke.MethodHandles
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: to admit defeat
 *
 * I did not solve this one. I ended up "borrowing" the approach outlined here:
 * https://github.com/ClouddJR/advent-of-code-2022/blob/main/src/main/kotlin/com/clouddjr/advent2022/Day19.kt#L63
 *
 *
 */

val ORE = 0
val CLAY = 1
val OBSIDIAN = 2
val GEODE = 3

typealias BotId = Int
typealias Res = List<Int>

fun main() {
    day.println()

    fun rate(cost: Int, have: Int, bots: Int): Int {
        val remaining = (cost - have).coerceAtLeast(0)
        return ceil(remaining / bots.toFloat()).toInt()
    }

    data class Blueprint(
        val id: ID, val robotCosts: List<Res>
    ) {
        val maxOre = robotCosts.maxOfOrNull { it[ORE] }!!
        val maxClay = robotCosts.maxOfOrNull { it[CLAY] }!!
        val maxObsidian = robotCosts.maxOfOrNull { it[OBSIDIAN] }!!
    }

    data class State(
        val bp: Blueprint, val mins: Int, val res: Res = listOf(1, 0, 0, 0), val bots: List<Int> = listOf(1, 0, 0, 0)
    ) : Comparable<State> {
        override fun compareTo(other: State): Int = 0 - res[GEODE].compareTo(other.res[GEODE])

        fun canBuild(bot: BotId): Boolean =
            bp.robotCosts[bot].mapIndexed { i, r -> i to r }.all { (costType, botCost) ->
                botCost == 0 || bots[costType] > 0
            }

        fun shouldBuild(bot: BotId): Boolean = when (bot) {
            ORE -> bp.maxOre > bots[bot]
            CLAY -> bp.maxClay > bots[bot]
            OBSIDIAN -> bp.maxObsidian > bots[bot]
            else -> true
        }

        fun minsToBuild(bot: BotId): Int = bp.robotCosts[bot].mapIndexed { idx, cost ->
            rate(cost, res[idx], bots[idx])
        }.max() + 1

        fun nextOrNull(bot: BotId): State? {
            if (!shouldBuild(bot) || !canBuild(bot)) {
                return null // can't...or won't?
            }
            val m = minsToBuild(bot)
            if (m >= mins) {
                return null // Not enough time remaining to build
            }
            return copy(mins = mins - m,
                res = res.mapIndexed { id, amt -> amt + (m * bots[id]) - bp.robotCosts[bot][id] },
                bots = bots.mapIndexed { id, n -> if (id == bot) n + 1 else n })
        }

        fun nextStates(): List<State> = bots.indices.mapNotNull { nextOrNull(it) }
        fun bestPossible() = res[GEODE] + (0 until mins - 1).sumOf { it + bots[GEODE] }
    }

    fun Blueprint.maxGeodes(mins: Int): Int {
        val pq = PriorityQueue<State>().apply { add(State(this@maxGeodes, mins)) }
        var mx = 0
        while (pq.isNotEmpty()) {
            val s: State = pq.poll()
            if (s.bestPossible() > mx) {
                pq.addAll(s.nextStates())
            }
            mx = max(mx, s.res[GEODE] + s.bots[GEODE] * (s.mins - 1))
        }
        return mx
    }

    fun Blueprint.determineQuality(mins: Int = 24): Int {
        return maxGeodes(mins) * id
    }

    fun String.parseToBlueprint(): Blueprint {
        // [0 ore, 1 clay, 2 obsidian, 3 geode]
        val ints = this.ints()
        return Blueprint(
            id = ints[0],
            robotCosts = listOf(
                listOf(ints[1], 0, 0, 0), // ore robot
                listOf(ints[2], 0, 0, 0), // clay robot
                listOf(ints[3], ints[4], 0, 0), // obs robot
                listOf(ints[5], 0, ints[6], 0), // geode robot
            )
        )
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.parseToBlueprint() }
            .sumOf { it.determineQuality() }
    }

    fun part2(input: List<String>): Int {
        return input
            .take(3)
            .map { it.parseToBlueprint() }
            .map { it.maxGeodes(32) }
            .reduce(Int::times)
    }

    println("ex0")
    checkEq(9, part1(readInput("${day}_ex0")))
    println("ex")
    checkEq(33, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    println("Part 1 ex0")
    checkEq(56, part2(readInput("${day}_ex0")))

    println("Part 2:")
    solve { part2(input) }
}