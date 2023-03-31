import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: Kotlin doesn't have a partitionBy/groupBy that returns a list :(
 *
 * Breaking into some variables makes it easier to read and debug...just not too too many.
 */
fun main() {
    day.println()
    fun elfSums(input: List<String>): List<Int> {
        val groups = input.partitionBy { it.isEmpty() }
        val ints = groups.filter { it != listOf("") }.map {
            it.map(String::toInt)
        }
        return ints.map { it.sum() }
    }

    fun part1(input: List<String>): Int {
        val elfSums = elfSums(input)
        return elfSums.max()
    }

    fun part2(input: List<String>): Int {
        return elfSums(input).sortedDescending().take(3).sum()
    }

    checkEq(24000, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}
