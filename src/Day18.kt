import java.lang.invoke.MethodHandles
import java.util.HashSet
import kotlin.math.max
import kotlin.math.min

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Don't underestimate the JVM. I was overthinking the runtime of this one, thinking it would explode out.
 * Part 2 soln isn't optimized but still returns in around 1.5 secs
 */

/*
Part 1 thinking:
n^2 approach: for-each cube, look at each other cube and see any overlaps
n approach: put cubes into a matrix, keeps links to nearby slots
then scan through and count up remaining slots
Kind of like a 3d game of life iteration?

Part 2 thinking:
Now this is more like the "island" finding problem in 3d
https://leetcode.com/problems/number-of-islands/

Kind of similar to how I solved part 1 but also need to sprawl out, keep track of seen, and detect when
a bubble is fully encased
 */
fun main() {
    day.println()

    data class Cube(val x: Int, val y: Int, val z: Int) {
        fun around(): List<Cube> {
            return buildList {
                for (diff in arrayOf(-1, 1)) {
                    add(Cube(x + diff, y, z))
                }
                for (diff in arrayOf(-1, 1)) {
                    add(Cube(x, y + diff, z))
                }
                for (diff in arrayOf(-1, 1)) {
                    add(Cube(x, y, z + diff))
                }
            }
        }

        /**
         * A cube is an inner bubble if it's empty and it's fully surrounded by cubes OR inner bubbles.
         */
        fun isInnerBubble(matrix: HashSet<Cube>, limits: Pair<Cube, Cube>, seen: MutableSet<Cube>): Boolean {
            if (this in matrix ||
                this.x !in limits.first.x..limits.second.x
                || this.y !in limits.first.y..limits.second.y
                || this.z !in limits.first.z..limits.second.z
            ) {
                return false
            }

            val unseens = around().toSet() - seen
            return unseens.all { unseen ->
                seen.add(unseen)
                unseen in matrix || unseen.isInnerBubble(matrix, limits, seen)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val cubes = input.map { s -> s.split(",").map { side -> side.toInt() } }.map { Cube(it[0], it[1], it[2]) }
        val matrix = cubes.toHashSet()
        return cubes.sumOf { cube ->
            6 - (cube.around().map {
                matrix.contains(it)
            }.count { it })
        }
    }


    fun part2(input: List<String>): Int {
        val cubes = input.map { s -> s.split(",").map { side -> side.toInt() } }.map { Cube(it[0], it[1], it[2]) }
        val limits = cubes.fold(cubes[0] to cubes[0]) {
            (lo,hi), cube ->
            Cube(min(lo.x, cube.x), min(lo.y, cube.y), min(lo.z, cube.z)) to Cube(max(hi.x, cube.x), max(hi.y, cube.y), max(hi.z, cube.z))
        }
        val matrix = cubes.toHashSet()
        return cubes.sumOf { cube ->
            6 - (cube.around().map {
                if (matrix.contains(it)) {
                    true
                } else {
                    it.isInnerBubble(matrix, limits, mutableSetOf(cube))
                }
            }.count { it })
        }
    }

    checkEq(10, part1(readInput("${day}_ex0")))
    checkEq(64, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    checkEq(58, part2(readInput("${day}_ex")))
    println("Part 2 ex passed")
    println("Part 2:")
    solve { part2(input) }
}


