import java.lang.invoke.MethodHandles
import java.util.*

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()

    fun part1(input: List<String>): Int {
        val valveMap = input.map { it.toValve() }.associateBy { it.id }

        data class State(
            val pos: ID,
            val visited: Set<ID> = setOf(),
            val opened: Set<ID> = setOf(), // visited since last change, clear on valve open
            val minutes: Int = 30, // minutes left
            val futureFlow: Int = 0
        )

        val pq = PriorityQueue<State> { s1, s2 ->
            0-s1.futureFlow.compareTo(s2.futureFlow)
        }
        val zeroOpened = valveMap.filter { (k,v) -> v.rate == 0 }.map { (k,v) -> k }.toSet()
        println(zeroOpened)
        pq.add(State("AA", setOf("AA"), zeroOpened))
        var mx = Int.MIN_VALUE
        while (pq.isNotEmpty()) {
            var s = pq.poll()
            if (s.minutes == 0) {
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
                    println("mx=$mx pq=${pq.size}")
                }
                continue
            }
            s = State(s.pos, s.visited, s.opened, s.minutes - 1, s.futureFlow)
            val options =
                valveMap[s.pos]!!.leadsTo.filter { dest -> dest !in s.visited }
                    .map { dest ->
                        State(dest, s.visited.plus(dest), s.opened, s.minutes, s.futureFlow)
                    }.toMutableList()
            if (s.pos !in s.opened) {
                // open valve
                val futureFlow = s.futureFlow + (s.minutes * valveMap[s.pos]!!.rate)
                options += State(s.pos, setOf(s.pos), s.opened.plus(s.pos), s.minutes, futureFlow)
            }
            if (options.isEmpty()) { // stuck
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
                    println("mx=$mx pq=${pq.size}")
                }
            } else {
                pq.addAll(options)
            }
        }
        return mx
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val exInput = readInput("${day}_ex")
    checkEq(1651, part1(exInput))
    println("Part 1 Ex:")
    solve { part1(exInput) }

    val input = readInput(day)
//
    println("Part 1:")
    solve { part1(input) }
//    println("Part 2:")
//    solve { part2(input) }
}

typealias ID = String

data class Valve(val id: ID, val rate: Int, val leadsTo: List<ID>)

fun String.toValve(): Valve {
    // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    val splits = this.split(" ")
    val leadsTo = splits.subList(9, splits.size).map { it.removeSuffix(",") }.toList()
    return Valve(
        splits[1],
        splits[4].removePrefix("rate=").removeSuffix(";").toInt(),
        leadsTo
    )
}
