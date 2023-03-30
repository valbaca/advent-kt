import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    checkEq(0, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
