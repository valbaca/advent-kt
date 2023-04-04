import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: fold to the rescue! Glad I put in the time into the `touching` and `moveTo` functions. Made part 2 super easy.
 *
 * Learned that runningFold simplifies the fold-into-a-list pattern.
 */
fun main() {
    day.println()

    fun part1(input: List<String>): Int {
        var head = Point(0, 0)
        var tail = Point(0, 0)
        val seen = mutableSetOf<Point>()
        for (line in input) {
            val splits = line.split(" ")
            val dir = splits[0]
            val n = splits[1].toInt()
            repeat(n) {
                head = head.move(dir)
                tail = tail.moveTo(head)
                seen += tail
            }
        }
        return seen.size
    }

    fun part2(input: List<String>): Int {
        var rope = buildList<Point> {
            repeat(10) {
                add(Point(0, 0))
            }
        }
        val seen = mutableSetOf<Point>()
        for (line in input) {
            val splits = line.split(" ")
            val dir = splits[0]
            val n = splits[1].toInt()
            repeat(n) {
                // [H, 1, 2, ..., 9]
                // init = [H']
                // then fold in the rest -> [H', 1'] -> [H', 1', 2'] ->...
                rope = rope.subList(1, 10).runningFold(rope[0].move(dir)) { prev, p ->
                    p.moveTo(prev)
                }
                seen += rope.last()
            }
        }
        return seen.size
    }

    checkEq(13, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    solve { part2(input) }
}

fun Int.stepToward(other: Int): Int {
    val c = this.compareTo(other)
    return when {
        c.isPos() -> this - 1
        c.isNeg() -> this + 1
        else -> this
    }
}

data class Point(val x: Int, val y: Int) {
    fun touching(other: Point) = ((x - other.x) in -1..1 && (y - other.y) in -1..1)

    fun moveTo(other: Point): Point {
        if (touching(other)) {
            return this
        }
        return Point(x.stepToward(other.x), y.stepToward(other.y))
    }

    fun move(dir: String): Point = when (dir) {
        "U" -> Point(x, y + 1)
        "D" -> Point(x, y - 1)
        "L" -> Point(x - 1, y)
        "R" -> Point(x + 1, y)
        else -> throw IllegalArgumentException()
    }
}