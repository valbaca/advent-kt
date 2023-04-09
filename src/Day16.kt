import com.google.common.collect.ArrayTable
import com.google.common.collect.HashBasedTable
import java.lang.invoke.MethodHandles
import java.util.*

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")


val start = "AA".hashCode()

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
            0 - s1.futureFlow.compareTo(s2.futureFlow)
        }
        val initialOpened = valveMap.filter { (k,v) -> v.rate == 0 }.map { (k,v) -> k }.toSet()
//        println(zeroOpened)
        pq.add(State(start, setOf(start), initialOpened))
        var mx = Int.MIN_VALUE

        // Create a table of Space -> Time -> MaxFlow
        // Need this to reduce the problem space from exploding out
        // If you return to a place you've been, later and with lower flow: can just stop going forward
        // This is like Dynamic Programming if you squint at it
        val initTable = HashBasedTable.create<ID, Int, Int>()
        for (k in valveMap.keys) {
            for (min in 0..30) {
                initTable.put(k, min, Int.MIN_VALUE)
            }
        }
        val table = ArrayTable.create(initTable)

        while (pq.isNotEmpty()) {
            var s = pq.poll()
            if (s.minutes == 0) {
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
//                    println("mx=$mx pq=${pq.size}")
                }
                continue
            }
            val lastBestFlow = table.get(s.pos, s.minutes)!!
            if (lastBestFlow < s.futureFlow) {
                for (m in s.minutes downTo 0) {
                    table.put(s.pos, m, s.futureFlow)
                }
            } else {
                continue // bail, we've been here before and done better
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
            if (options.isEmpty()) { // no more moves
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
//                    println("mx=$mx pq=${pq.size}")
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

typealias ID = Int

data class Valve(val id: ID, val rate: Int, val leadsTo: List<ID>)

fun String.toValve(): Valve {
    // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    val splits = this.split(" ")
    val leadsTo = splits.subList(9, splits.size).map { it.removeSuffix(",") }.toList()
//    println("${splits[1]} ${splits[1].hashCode()}")
//    for (lead in leadsTo) {
//        println("${lead} ${lead.hashCode()}")
//    }
    return Valve(
        splits[1].hashCode(),
        splits[4].removePrefix("rate=").removeSuffix(";").toInt(),
        leadsTo.map { it.hashCode() }
    )
}
