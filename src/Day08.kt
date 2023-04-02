import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")
typealias Loc = Pair<Int, Int>

/**
 * TIL: List has an `indices` field that gives a range over the indexes!
 * Kotlin doesn't have normal index for-loop like for(int i = 0; i < arr.length; i++) so the direct equivalent is:
 * for (i in arr.indices) // or to go reverse: arr.indices.reversed()
 *
 * But instead, foldIndexed is probably more "idiomatic"
 *
 * I wrote `takeUntil` because `takeWhile` wasn't fitting what I needed. It's still pretty impressive how convenient
 * it is to update the built-in collections and iterators with what you need.
 *
 * Got stuck on using 0 instead of MIN_VALUE for a while. Again, getting sleep solves so many problems.
 */
fun main() {
    day.println()
    fun MutableSet<Loc>.update(maxSeen: Int, r: Int, c: Int, value: Int): Int {
        if (value > maxSeen) {
            this.add(Loc(r, c))
            return value
        }
        return maxSeen
    }

    fun part1(input: List<String>): Int {
        val mx = input.toIntMatrix()
        val visible = mutableSetOf<Loc>()
        for (r in mx.indices) {
            val row = mx[r]
            // left to right
            row.indices.fold(Int.MIN_VALUE) { max, c ->
                visible.update(max, r, c, row[c])
            }
            // right to left
            row.indices.reversed().fold(Int.MIN_VALUE) { max, c ->
                visible.update(max, r, c, row[c])
            }
        }
        val colIndices = mx[0].indices
        for (c in colIndices) {
            val col = mx.map { it[c] }.toList()
            // up to down
            mx.indices.fold(Int.MIN_VALUE) {max, r ->
                visible.update(max, r, c, col[r])
            }
            // down to up
            mx.indices.reversed().fold(Int.MIN_VALUE) {max, r ->
                visible.update(max, r, c, col[r])
            }
        }
        return visible.size
    }


    fun scenic(mx: List<List<Int>>, r: Int, c: Int, value: Int): Int {
        val up = (r - 1 downTo 0).map { mx[it][c] }.takeUntil { it >= value }.count()
        val left = (c - 1 downTo 0).map { mx[r][it] }.takeUntil { it >= value }.count()
        val right = (c + 1 until mx.size).map { mx[r][it] }.takeUntil { it >= value }.count()
        val down = (r + 1 until mx[0].size).map { mx[it][c] }.takeUntil { it >= value }.count()
        return up * left * right * down
    }

    fun part2(input: List<String>): Int {
        val mx = input.toIntMatrix()
        return mx.flatMapIndexed { r, row ->
            row.mapIndexed { c, value ->
                scenic(mx, r, c, value)
            }
        }.max()
    }

    checkEq(21, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(8, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}

