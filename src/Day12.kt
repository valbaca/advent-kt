import java.lang.invoke.MethodHandles
import java.util.*
import kotlin.Comparator
import kotlin.math.abs

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: I've still got Dijkstra's algo memorized. Got part 1 done on the first try.
 * PriorityQueue and a custom comparator made this solution sing.
 * Kotlin's static typing with type inference and syntax really does bridge the best of the
 * two worlds that generally have tradeoffs:
 * - Execution speed of Java
 * - Nearly as nice to write as Python
 * - BUT static typing means the IDE can help more than it would in Python
 * - AND static typing means you can expect it to be *reasonable* at execution
 *
 * I'm still stumbling around the functional parts of Kotlin.
 *
 * Part 2 runs faster than part 1! Two ArrayLists lookups is faster than set-contains.
 */

/*
1. parse into int array:
S = 0
a = 1 ... z = n-1
E = n
Store S and E

2.
Seen: Set of nodes that should not be started again
Visited: Set of nodes that have been started and finished
Queue: PriorityQueue (by steps) of spots to visit
while (queue not empty) {
  curr = queue.pop
  if (curr == END) return curr.steps
  if (curr in visited) continue;
  for (spot in curr.reachableAround) {
    if (spot in visited) continue;
    if (spot not in seen) {
      seen.add(spot)
      queue.add(spot w/ steps = curr.steps+1)
    }
  }
  visited.add(curr)
}
 */
fun main() {
    day.println()
    fun part1(input: List<String>): Int {
        return parse(input).shortest()
    }

    fun part2(input: List<String>): Int {
        return parse(input).shortestAny()
    }

    checkEq(31, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }


    checkEq(29, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}


data class Trail(val steps: Int, val cord: Cord)


data class Topo( // topography
    val start: Cord, val end: Cord, val mx: List<List<Int>>
) {
    operator fun get(cord: Cord): Int {
        return mx[cord.first][cord.second]
    }

    private fun around(cord: Cord): List<Cord> {
        val v = this[cord]
        return buildList {
            for ((rd, cd) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                val r = cord.first + rd
                val c = cord.second + cd
                if (r in mx.indices && c in mx[r].indices && this@Topo[r to c] <= v + 1) {
                    add(r to c)
                }
            }
        }
    }

    private fun buildTrailComparator(): Comparator<Trail> = object : Comparator<Trail> {
        override fun compare(o1: Trail?, o2: Trail?): Int {
            checkNotNull(o1)
            checkNotNull(o2)
            val compareSteps = o1.steps.compareTo(o2.steps)
            if (compareSteps != 0) return compareSteps
            return o1.cord.dist(end).compareTo(o1.cord.dist(end))
        }
    }

    fun shortest(): Int {
        val pq = PriorityQueue(buildTrailComparator())
        pq.add(Trail(0, start))

        val visited = mutableSetOf<Cord>()
        val seen = mutableSetOf<Cord>()
        while (pq.isNotEmpty()) {
            val curr = pq.poll()!!
            if (curr.cord == end) return curr.steps
            if (curr.cord in visited) continue
            for (spot in around(curr.cord)) {
                if (spot in seen) continue
                pq.add(Trail(curr.steps + 1, spot))
                seen.add(spot)
            }
            visited.add(curr.cord)
        }
        throw IllegalStateException()
    }

    fun shortestAny(): Int {

        val pq = PriorityQueue(buildTrailComparator())
        pq.add(Trail(0, start))
        pq.addAll(buildList {
            mx.forEachIndexed { r, row ->
                row.forEachIndexed { c, v ->
                    if (v <= 1) {
                        add(Trail(0, r to c))
                    }
                }
            }
        })

        val costs = mx.map { row ->
            row.map { Int.MAX_VALUE }.toMutableList()
        }.toMutableList()
        while (pq.isNotEmpty()) {
            val curr = pq.poll()!!
            if (costs[curr.cord] > curr.steps) {
                costs[curr.cord] = curr.steps
            } else {
                continue // can assume already 'visited' b/c of the PQ
            }
            for (spot in around(curr.cord)) {
                if (costs[spot] <= curr.steps + 1) continue
                pq.add(Trail(curr.steps + 1, spot))
            }
        }
        return costs[end]
    }


}

fun Char.toTopoValue(): Int {
    return when (this) {
        'S' -> 0
        'E' -> 'z' - 'a' + 2
        else -> this - 'a' + 1
    }
}

private fun parse(input: List<String>): Topo {
    var start: Cord? = null
    var end: Cord? = null
    val mx = input.mapIndexed { row, line ->
        line.toCharArray().mapIndexed { col, ch ->
            val topo = ch.toTopoValue()
            if (ch == 'S') {
                start = row to col
            } else if (ch == 'E') {
                end = row to col
            }
            topo
        }
    }.toList()
    return Topo(start!!, end!!, mx)
}

fun Cord.dist(other: Cord): Int {
    return abs(this.first - other.first) + abs(this.second - other.second)
}

operator fun MutableList<MutableList<Int>>.get(cord: Cord): Int {
    return this[cord.first][cord.second]
}

operator fun MutableList<MutableList<Int>>.set(cord: Cord, v: Int) {
    this[cord.first][cord.second] = v
}