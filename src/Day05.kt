import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()
    fun transpose(lines: List<String>): List<String> {
        val nchars = lines.map { it.length }.max()
        val mx = lines.map { line -> line.toCharArray().toList() }

        val tx = mutableListOf<String>()

        for (i in 0 until nchars) {
            val s = buildString {
                for (j in lines.size - 1 downTo 0) {
                    val ch = mx.getOrNull(j)?.getOrNull(i)
                    if (ch != null) {
                        append(ch)
                    }
                }
            }
            tx.add(s)
        }
        return tx
    }


    fun doMove(move: String, stax: MutableList<ArrayDeque<Char>>, reverse: Boolean = false) {
        val (count, from, to) = move.split(" ").mapNotNull { it.toIntOrNull() }
        if (reverse) {
            // part 2
            val temp = ArrayDeque<Char>() // temp buffer so they flip back
            repeat(count) {
                temp.addLast(stax[from].removeLast())
            }
            repeat(count) {
                stax[to].addLast(temp.removeLast())
            }
        } else {
            // part 1
            repeat(count) {
                stax[to].addLast(stax[from].removeLast())
            }
        }
    }

    fun crates(input: List<String>, reverse: Boolean = false): String {
        val partitions = input.partitionBy { it.isEmpty() }
        val (init, moves) = partitions.filter { it != listOf("") }
        val txInit = transpose(init)
        val staxInit = txInit.filter { it.first() in '0'..'9' }
        val stax = mutableListOf<ArrayDeque<Char>>(ArrayDeque()) // empty list at 0 index so from-to is easy
        for (s in staxInit) {
            stax.add(s.substring(1).filter { it != ' ' }.toCollection(ArrayDeque()))
        }
        moves.forEach { move -> doMove(move, stax, reverse) }
        return stax.mapNotNull { it.lastOrNull() }.joinToString("")
    }

    fun part1(input: List<String>): String {
        return crates(input)
    }

    fun part2(input: List<String>): String {
        return crates(input, true)
    }

    checkEq("CMZ", part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq("MCD", part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}
