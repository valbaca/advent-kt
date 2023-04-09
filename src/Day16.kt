import com.google.common.collect.ArrayTable
import com.google.common.collect.HashBasedTable
import java.lang.invoke.MethodHandles
import java.util.*
import kotlin.collections.ArrayDeque

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")


val start = "AA".hashCode()

/**
 * TIL: Guava's Table is easy to work with and was a huge save for part 1
 *
 * Looks like most people used Floyd–Warshall algorithm to solve part 2
 * I stubbornly stuck with my approach and just let it run for a very long time (~30mins)
 *
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
 */
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
        val valveMap = input.map { it.toValve() }.associateBy { it.id }

        data class State2(
            val pos: ID,
            val eleph: ID,
            val visited: Set<ID>,
            val elephVisited: Set<ID>,
            val opened: Set<ID>, // visited since last change, clear on valve open
            val minutes: Int = 26, // minutes left
            val futureFlow: Int = 0
        )

//        val pq = PriorityQueue<State2> { s1, s2 ->
//            0 - s1.futureFlow.compareTo(s2.futureFlow)
//        }
        val pq = ArrayDeque<State2>()
        val initialOpened = valveMap.filter { (k,v) -> v.rate == 0 }.map { (k,v) -> k }.toSet()
//        println(zeroOpened)
        pq.add(State2(start, start, setOf(start), setOf(start), initialOpened))
        var mx = Int.MIN_VALUE

        // Create a table of Space -> Time -> MaxFlow
        // Need this to reduce the problem space from exploding out
        // If you return to a place you've been, later and with lower flow: can just stop going forward
        // This is like Dynamic Programming if you squint at it
        val table = HashBasedTable.create<Pair<ID, ID>, Int, Int>()
        for (k in valveMap.keys) {
            for (k2 in valveMap.keys) {
                for (min in 0..26) {
                    table.put(k to k2 , min, Int.MIN_VALUE)
                }
            }
        }
//        val table = ArrayTable.create(initTable)

        while (pq.isNotEmpty()) {
            val s = pq.removeLast()
            if (s.minutes == 0) {
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
                    println("mx=$mx pq=${pq.size}")
                }
                continue
            }
            val lastBestFlow = table.get(s.pos to s.eleph, s.minutes)!!
            if (lastBestFlow < s.futureFlow) {
                for (m in s.minutes downTo 0) {
                    table.put(s.pos to s.eleph, m, s.futureFlow)
                }
            } else {
                continue // bail, we've been here before and done better
            }

            val options =
                valveMap[s.pos]!!.leadsTo.filter { dest -> dest !in s.visited }
                    .map { dest ->
                        State2(dest, s.eleph, s.visited.plus(dest), s.elephVisited, s.opened, s.minutes-1, s.futureFlow)
                    }.toMutableList()
            if (s.pos !in s.opened) {
                // open valve
                val futureFlow = s.futureFlow + ((s.minutes-1) * valveMap[s.pos]!!.rate)
                options += State2(s.pos, s.eleph, setOf(s.pos), s.elephVisited, s.opened.plus(s.pos),  s.minutes-1, futureFlow)
            }

            val xOptions = mutableListOf<State2>()
            xOptions.addAll(options)
            for (so in options) {
                val eOptions =
                    valveMap[so.eleph]!!.leadsTo.filter { dest -> dest != so.pos && dest !in so.elephVisited }
                        .map { dest ->
                            State2(so.pos, dest, so.visited, so.elephVisited.plus(dest), so.opened, so.minutes, so.futureFlow)
                        }.toMutableList()
                if (so.eleph !in so.opened) {
                    // open valve
                    val futureFlow = so.futureFlow + (so.minutes * valveMap[so.eleph]!!.rate)
                    eOptions += State2(so.pos, so.eleph, so.visited, setOf(so.eleph), so.opened.plus(so.eleph), so.minutes, futureFlow)
                }
                xOptions.addAll(eOptions)
            }

            val eOptions =
                valveMap[s.eleph]!!.leadsTo .filter { dest -> dest != s.pos && dest !in s.elephVisited }
                    .map { dest ->
                        State2(s.pos, dest, s.visited, s.elephVisited.plus(dest), s.opened, s.minutes-1, s.futureFlow)
                    }.toMutableList()
            if (s.eleph !in s.opened) {
                // open valve
                val futureFlow = s.futureFlow + ((s.minutes-1) * valveMap[s.eleph]!!.rate)
                eOptions += State2(s.pos, s.eleph, s.visited, setOf(s.eleph), s.opened.plus(s.eleph), s.minutes-1, futureFlow)
            }
            xOptions.addAll(eOptions)

            if (xOptions.isEmpty()) { // no more moves
                if (mx < s.futureFlow) {
                    mx = s.futureFlow
                    println("mx=$mx pq=${pq.size}")
                }
            } else {
                pq.addAll(xOptions)
            }
        }
        return mx
    }

    val exInput = readInput("${day}_ex")
    checkEq(1651, part1(exInput))
    println("Part 1 Ex:")
    solve { part1(exInput) }

    val input = readInput(day)
//
    println("Part 1:")
    solve { part1(input) }

    checkEq(1707, part2(exInput))
    println("Part 2:")
    solve { part2(input) }
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

// Part 2:
// [redacted answer] (took 1890528ms)
// TOOK 30 MINS...not proud but glad to be done with this one.