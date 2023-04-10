import com.google.common.collect.HashBasedTable
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")


/**
 * TIL: Got bit hard by the fact that HashBasedTable will return null if given an Int and not a Long
 * for (x in 0 until WIDTH) has x as an Int!
 *
 * Also got pretty stuck on part 2. I had a rough idea that it would involve crafting a loop but wasn't sure
 * how the repeats would "fit" within each other.
 *
 * Found another Kotlin solution that uses an interesting State. Merged that in with what I already had working (classic
 * programming right?)
 *
 * I feel like the problems are getting out of my ability to solo them, so may not get through the year or could at
 * least take a break
 */

private const val WIDTH = 7

private const val ITERS = 2022

typealias LCord = Pair<Long, Long>
operator fun LCord.plus(other: LCord) = (first + other.first) to (second + other.second)

fun main() {
    day.println()
    fun printChamber(top: Long, chamber: HashBasedTable<Long, Long, Char>) {
        for (y in top downTo 0) {
            for (x in 0 until WIDTH) {
                print(chamber.get(y, x) ?: '.')
            }
            println()
        }
    }

    // return new position (if any), and whether the move was valid/made
    fun move(dir: LCord, rock: List<LCord>, pos: LCord, chamber: HashBasedTable<Long, Long, Char>): Pair<LCord, Boolean> {
        var valid = true
        for (part in rock) {
            val partStartPos = pos + part
            val movedPart = partStartPos + dir
            val (y, x) = movedPart
            if (x !in 0 until WIDTH  || y < 0 || chamber.get(y, x) != null) {
                // out of bounds
                valid = false
                break
            }
        }
        return if (valid) {
            (pos + dir) to true
        } else {
            pos to false
        }
    }

    fun surface(top: Long, chamber: HashBasedTable<Long, Long, Char>): List<Long> {
        return buildList {
            for (x in 0 until WIDTH) {
                var y = top
                while(y > -1 && chamber.get(y, x.toLong()) == null) {
                    y--
                }
                add(y-top)
            }
        }
    }

    fun part1(input: List<String>): Long {
        check(input.size == 1)
        val stream = generateSequence { input[0].toCharArray().toList() }.flatten().iterator()

        val chamber: HashBasedTable<Long, Long, Char> = HashBasedTable.create<Long, Long, Char>(10_000, WIDTH) // Row, Cell/Col, Content
        var top: Long = 0
        val rocks = generateSequence { rocks }.flatten().take(ITERS).iterator()

        for (rock in rocks) {
//            println(rock)
            val insertRow = top + 3L
            var pos = insertRow to 2L // rock pos
            while (true) {
                // push by jet
                val jet = stream.next()
                val dir = if (jet == '<') (0L to -1L) else (0L to 1L)
                val jetResult = move(dir, rock, pos, chamber)
                pos = jetResult.first
                // down
                val downResult = move(-1L to 0, rock, pos, chamber)
                pos = downResult.first
                if (!downResult.second) {
                    // could not move down, settle
                    for (part in rock) {
                        val (y, x) = part + pos
                        if (y+1 > top) {
                            top = y+1
                        }
                        chamber.put(y, x, '#')
                    }
                    break
                }
            }
        }
        return top
    }

    fun part2(rockCount: Long, input: List<String>): Long {
        data class State(val surface: List<Long>, val jetIndex: Int, val rockIndex: Int)
        val cache = mutableMapOf<State, Pair<Long, Long>>()
        val stream = generateSequence { input[0].toCharArray().toList() }.flatten().iterator()

        val chamber: HashBasedTable<Long, Long, Char> = HashBasedTable.create<Long, Long, Char>(10_000, WIDTH) // Row, Cell/Col, Content
        var top: Long = 0
        val infRocks = generateSequence { rocks }.flatten().iterator()

        var rockIndex = 0
        var jetIndex = 0
        for (r in 0 until rockCount) {
//            println(rock)
            val rock = infRocks.next()
            rockIndex = (rockIndex + 1) % rocks.size
            val insertRow = top + 3L
            var pos = insertRow to 2L // rock pos
            while (true) {
                // push by jet
                val jet = stream.next()
                jetIndex = (jetIndex + 1) % input[0].length
                val dir = if (jet == '<') (0L to -1L) else (0L to 1L)
                val jetResult = move(dir, rock, pos, chamber)
                pos = jetResult.first
                // down
                val downResult = move(-1L to 0, rock, pos, chamber)
                pos = downResult.first
                if (!downResult.second) {
                    // could not move down, settle
                    for (part in rock) {
                        val (y, x) = part + pos
                        if (y+1 > top) {
                            top = y+1
                        }
                        chamber.put(y, x, '#')
                    }
                    break
                }
            }
            val state = State(
                surface(top, chamber),
                jetIndex,
                rockIndex
            )
            // c/o https://github.com/ClouddJR/advent-of-code-2022/blob/main/src/main/kotlin/com/clouddjr/advent2022/Day17.kt#L15-L31
            if (state in cache) {
                val (rocksAtLoopStart, heightAtLoopStart) = cache.getValue(state)
                val rocksPerLoop = r - rocksAtLoopStart
                val loopHeight = (top - heightAtLoopStart)
                val remainingLoops = (rockCount - r) / rocksPerLoop
                val remainingRocks = (rockCount - r) % rocksPerLoop
                val remainingHeight = cache.values
                    .first { (rock, _) -> rock == rocksAtLoopStart + remainingRocks - 1 }.second - heightAtLoopStart

                return top + remainingLoops * loopHeight + remainingHeight
            }
            cache[state] = r to top
        }
        TODO()
    }

    checkEq(3068L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2 ex:")
    checkEq(1514285714288L, part2(1000000000000L, readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(1000000000000L, input) }
    // 1552769679300 is too low
}

// Cords in this one are y,x = row,col
// y down to up
// x left to right

val rocks = listOf<List<LCord>>(
    listOf(0L to 0L, 0L to 1L, 0L to 2L, 0L to 3L), // -
    listOf(0L to 1L, 1L to 0L, 1L to 1L, 1L to 2L, 2L to 1L), // +
    listOf(0L to 0L, 0L to 1L, 0L to 2L, 1L to 2L, 2L to 2L), // _|
    listOf(0L to 0L, 1L to 0L, 2L to 0L, 3L to 0L), // |
    listOf(0L to 0L, 0L to 1L, 1L to 0L, 1L to 1L) // #
)