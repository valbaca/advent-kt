import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

enum class Throw {
    Rock, Paper, Scissors;

    fun score() = when (this) {
        Rock -> 1
        Paper -> 2
        Scissors -> 3
    }
}

fun String.toThrow(): Throw = when (this) {
    "A", "X" -> Throw.Rock
    "B", "Y" -> Throw.Paper
    "C", "Z" -> Throw.Scissors
    else -> TODO()
}

enum class Result {
    Draw, Win, Loss;

    fun score() = when (this) {
        Draw -> 3
        Win -> 6
        Loss -> 0
    }
}

fun String.toResult(): Result = when (this) {
    "X" -> Result.Loss
    "Y" -> Result.Draw
    "Z" -> Result.Win
    else -> TODO()
}


fun play(you: Throw, them: Throw): Result {
    return when (you) {
        them -> Result.Draw
        Throw.Rock -> if (them == Throw.Paper) Result.Loss else Result.Win
        Throw.Paper -> if (them == Throw.Scissors) Result.Loss else Result.Win
        Throw.Scissors -> if (them == Throw.Rock) Result.Loss else Result.Win
    }
}

fun main() {
    day.println()

    fun part1(input: List<String>): Int {
        return input.sumOf { s ->
            val (them, you) = s.split(" ").map { it.toThrow() }
            play(you, them).score() + you.score()
        }
    }

    fun part2(input: List<String>): Int {
        // reverse the "play" function
        val whatPlay: MutableMap<Result, MutableMap<Throw, Throw>> = mutableMapOf();
        for (you in Throw.values()) {
            for (them in Throw.values()) {
                val result = play(you, them);
                val resultMap = whatPlay.getOrPut(result) { mutableMapOf() }
                resultMap[them] = you
            }
        }

        return input.sumOf { s ->
            val (themStr, resultStr) = s.split(" ")
            val them = themStr.toThrow()
            val result = resultStr.toResult()
            val you = whatPlay[result]!![them]!!
            result.score() + you.score()
        }
    }

    checkEq(15, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}
