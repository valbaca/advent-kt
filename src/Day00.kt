import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()
    fun part1(input: List<String>): Int {
        TODO()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    checkEq(0, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    println(part1(input))
    println("Part 2:")
    println(part2(input))
}
