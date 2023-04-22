import com.google.common.collect.HashBasedTable
import org.eclipse.collections.impl.bag.mutable.HashBag
import java.lang.invoke.MethodHandles
import java.util.*
import kotlin.math.max
import kotlin.math.min

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/*
NSWE
 */

fun main() {
    day.println()


    fun HashBasedTable<Int, Int, Char>.allClear(vararg pos: Pair<Int, Int>): Boolean =
        pos.all { (r, c) -> get(r, c) != '#' }


    fun HashBasedTable<Int, Int, Char>.potential(elf: Pair<Int, Int>, roundNum: Int): Pair<Int, Int>? {
        // NSWE
        val (er, ec) = elf
        if // already clear
                (allClear(
                er - 1 to ec, er - 1 to ec - 1, er - 1 to ec + 1, // N, NW, NE
                er + 1 to ec, er + 1 to ec - 1, er + 1 to ec + 1, // S, SW, SE
                er to ec - 1, // W
                er to ec + 1, // E
            )
        ) return null

        val pots = buildList {
            // North
            add(allClear(er - 1 to ec, er - 1 to ec - 1, er - 1 to ec + 1) to (er - 1 to ec))
            // South
            add(allClear(er + 1 to ec, er + 1 to ec - 1, er + 1 to ec + 1) to (er + 1 to ec))
            // West
            add(allClear(er to ec - 1, er - 1 to ec - 1, er + 1 to ec - 1) to (er to ec - 1))
            // East
            add(allClear(er to ec + 1, er - 1 to ec + 1, er + 1 to ec + 1) to (er to ec + 1))
        }.toMutableList()

        Collections.rotate(pots, -(roundNum))
        return pots.firstNotNullOfOrNull { (clear, pot) ->
            if (clear) pot else null
        }
    }

    fun HashBasedTable<Int, Int, Char>.round(roundNum: Int): Boolean {
        val elves = buildSet<Pair<Int, Int>> {
            rowKeySet().forEach { r ->
                columnKeySet().forEach { c ->
                    if (this@round.get(r, c) == '#') {
                        this.add(r to c)
                    }
                }
            }
        }
        val elfToPotential = elves.map { it to potential(it, roundNum) }
        val potentials = HashBag<Pair<Int, Int>>().apply {
            elfToPotential.map { (_, pot) ->
                add(pot)
            }
        }
        var moved = false
        for ((elf, pot) in elfToPotential) {
            if (pot != null && potentials.occurrencesOf(pot) == 1) {
                // move elf to pot
                put(elf.first, elf.second, '.')
                put(pot.first, pot.second, '#')
                moved = true
            }
        }
        return moved
    }

    data class Bounds(
        var minR: Int = Int.MAX_VALUE, var maxR: Int = Int.MIN_VALUE,
        var minC: Int = Int.MAX_VALUE, var maxC: Int = Int.MIN_VALUE
    ) {
        fun update(r: Int, c: Int): Bounds {
            minR = min(minR, r)
            maxR = max(maxR, r)
            minC = min(minC, c)
            maxC = max(maxC, c)
            return this
        }
    }

    fun HashBasedTable<Int, Int, Char>.bounds(): Bounds {
        return rowKeySet().sorted().fold(Bounds()) { bounds, r ->
            columnKeySet().sorted().fold(bounds) { b, c ->
                if (get(r, c) == '#') {
                    b.update(r, c)
                } else {
                    b
                }
            }
        }
    }

    fun HashBasedTable<Int, Int, Char>.groundTiles(): Int {
        val bounds = bounds()
        return (bounds.minR..bounds.maxR).sumOf { r ->
            (bounds.minC..bounds.maxC).count { c ->
                get(r, c) != '#' // '.' or null
            }
        }
    }

    fun HashBasedTable<Int, Int, Char>.print(): String {
        val bounds = bounds()
        return (bounds.minR..bounds.maxR).map { r ->
            (bounds.minC..bounds.maxC).map { c ->
                if (get(r, c) == '#') '#' else '.' // '.' or null
            }.joinToString("")
        }.joinToString("\n")
    }

    fun parseTable(input: List<String>) =
        HashBasedTable.create<Int, Int, Char>().apply {
            input.mapIndexed { r, line ->
                line.mapIndexed { c, ch ->
                    put(r, c, ch)
                }
            }
        }


    fun part1(input: List<String>): Int {
        var table = parseTable(input)
//        println(table.print())
        repeat(10) {
            table.round(it)
//            println("After round ${it + 1}")
//            println(table.print())
        }
        return table.groundTiles()
    }

    fun part2(input: List<String>): Int {
        var table = parseTable(input)
        for (roundNum in 0..Int.MAX_VALUE) {
            if(!table.round(roundNum)) {
                return roundNum+1
            }
        }
        throw IllegalStateException()
    }

    checkEq(25, part1(readInput("${day}_ex0")))
    checkEq(110, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    checkEq(20, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}
