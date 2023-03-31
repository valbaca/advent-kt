import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()
    fun elfSums(input: List<String>): List<Int> {
        val groups = input.partitionBy { it.isEmpty() }
        val ints = groups.filter { it != listOf("") }.map {
            it.map(String::toInt)
        }
        return ints.map { list -> list.sum() }
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
