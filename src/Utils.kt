import org.checkerframework.checker.units.qual.s
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.system.measureTimeMillis


/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("input", "${name.replace(".", "/")}.txt").readLines()

/**
 * Given two objects, checks they're equal; throws IllegalStateException if they're not equal.
 */
fun checkEq(
    expected: Any?, actual: Any?
) {
    kotlin.check(expected == actual) { "Expected $expected Actual $actual" }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16).padStart(32, '0')


/**
 * Given Iterator<T> and a function, returns list where items are partitioned by the result of f(T) where the result differs
 *
 * `[0, 0, 1, -1, -2, 3] {it < 0} => [[0, 0, 1], [-1, -2], [3]]`
 *
 * Named `partitionBy` after Clojure's [partition-by](https://clojuredocs.org/clojure.core/partition-by)
 * Also similar to Rust itertools' [group_by](https://docs.rs/itertools/latest/itertools/structs/struct.GroupBy.html)
 *
 * This could be lazy...but it's probably fine.
 */
fun <T, R> Iterable<T>.partitionBy(f: (t: T) -> R): List<List<T>> {
    val init = Pair<MutableList<MutableList<T>>, R?>(/* accList */ mutableListOf(), /* lastResult */ null)
    return this.fold(init) { (accList, lastResult), elem ->
        if (accList.isEmpty()) {
            accList.add(mutableListOf(elem))
            return@fold Pair(accList, f(elem))
        }
        val currResult = f(elem)
        if (lastResult == currResult) {
            accList.last().add(elem)
            Pair(accList, currResult)
        } else {
            accList.add(mutableListOf(elem))
            Pair(accList, currResult)
        }
    }.first.map { innerList -> innerList.toList() }.toList()
}

/**
 * Runs the block and gives a printable string of the result and elapsed time.
 */
fun <T> runMeasure(block: () -> T): String {
    val result: T
    val elapsed = measureTimeMillis {
        result = block()
    }
    return "$result (took ${elapsed}ms)"
}

/**
 * Runs the block, puts its result in a string (with elapsed time appended) and prints it out.
 */
fun <T> solve(block: () -> T) {
    runMeasure(block).println()
}

//fun <A, B, R> Pair<A, B>.spread(f: (A, B) -> R) = f(first, second)
fun <A, R> List<A>.spread2(f: (A, A) -> R) = f(this[0], this[1])
fun <A> List<A>.toPair() = Pair(this[0], this[1])

fun List<String>.toIntMatrix(): List<List<Int>> = this.map { s ->
    s.map { it.toString().toInt() }.toList()
}.toList()

/**
 * Returns a list containing first elements until the given predicate is true.
 * Includes the element that satisfies the predicate.
 *
 * Ex. [1, 2, 10, 3, 4].takeUntil { it >= 10} => [1, 2, 10]
 *
 * Similar but not quite the same as takeWhileInclusive:
 * https://jivimberg.io/blog/2018/06/02/implementing-takewhileinclusive-in-kotlin/
 */
fun <T> Iterable<T>.takeUntil(pred: (T) -> Boolean): List<T> {
    var stop = false
    return takeWhile {
        val go = !stop
        stop = pred(it)
        go
    }
}

fun Int.isPos() = this > 0
fun Int.isNeg() = this < 0

fun main() {
    val s = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian."
//    s.split(":. ").forEach {println(it)}
    val ints = s.split(':', '.', ' ')
        .mapNotNull { it.toIntOrNull() }
//        .forEach { println(it) }
    val (
        id,
        oreRobotOre
    ) = ints
}


fun main0() {
    // https://blog.jetbrains.com/kotlin/2021/12/tips-and-tricks-for-solving-advent-of-code/
    val xs = listOf("abc", "c", "ad", "bc", "ab", "ca")

    // associate should return K-V Pairs; if duplicates, last association wins
    print("associate: ")
    println(xs.associate { Pair(it.first(), it) }) // {a=ab, c=ca, b=bc}
    print("associate to: ")
    println(xs.associate { it.first() to  it }) // use `to` for easy Pairs

    // TIP: just use associate -> Pair and IntelliJ will suggest the correct function

    // given a list, xs and a block that turns x -> y

    // associateBy just returns the K, X is the Value
    // returns Map<Y, X>
    print("associateBy: ")
    println(xs.associateBy { it.first() }) // equivalent to above

    // associateWith just returns the V, X is the Key
    // returns Map<X, Y>
    print("associateWith: ")
    println(xs.associateWith { it.length })

    // groupBy is Map<K,List<V>> or Map<Y, List<X>>
    print("groupBy: ")
    println(xs.groupBy { it.first() })
    // groupingBy is lazy
    print("groupingBy: ")
    println(xs.groupingBy{ it.first() }.eachCount())


    print("windowed: ")
    println("abcd".windowed(2))

    // forEach map filter reduce fold ... all have Indexed versions
    println("3456".filterIndexed { i, c -> i % c.digitToInt() == 0 })

    xs.map { "map$it" }
        .also {print("print mid-chain: ")}
        .also(::println) // all one line
        .also {print("print mid-chain on separate lines:")}
        .onEach(::println) // separate lines
        .filter { it.length % 2 == 0 }


    println("ArrayDeque: ")
    val dq = ArrayDeque<Int>()
    dq += 0
    dq += 1
    println(dq)
    dq += listOf(7, 8, 9)
    println(dq)

    val lst = listOf(1,2,3)
    val lst2 = lst + 9
    println(lst2)

}
/**
 * Coordinate (Cord for short)
 */
typealias Cord =  Pair<Int, Int>

operator fun Cord.plus(other: Cord) = (first + other.first) to (second + other.second)

/**
 * Using a Map to back an x,y grid of T
 */
data class SparseGrid<T>(val grid: SortedMap<Int, SortedMap<Int, T>> = sortedMapOf()) {
    operator fun get(cord: Cord): T? {
        return grid[cord.first]?.get(cord.second)
    }

    operator fun contains(cord: Cord): Boolean {
        return grid.contains(cord.first) && grid[cord.first]!!.contains(cord.second)
    }

    operator fun set(cord: Cord, value: T) {
        if (cord.first !in grid) {
            grid[cord.first] = sortedMapOf()
        }
        grid[cord.first]!![cord.second] = value
    }

    fun putAlong(xs: IntProgression, ys: IntProgression, value: T) {
        for (x in xs) {
            val xg = grid.getOrPut(x) { sortedMapOf() }
            for (y in ys) {
                xg[y] = value
            }
        }
    }
}

fun Int.progressTo(toValue: Int) = when {
    this <= toValue -> this..toValue
    else -> this downTo toValue
}

fun progressFromTo(from: Cord, to: Cord): Pair<IntProgression, IntProgression> {
    val (fromX, fromY) = from
    val (toX, toY) = to
    return fromX.progressTo(toX) to fromY.progressTo(toY)
}

//fun String.ints(): List<Int> = this.split(':', '.', ' ').mapNotNull { it.toIntOrNull() }
fun String.ints(): List<Int> {
    return buildList {
        var s = StringBuilder()
        for (c in this@ints.toCharArray()) {
            if (c.isDigit()) {
                s.append(c)
            } else if (s.isNotEmpty()) {
                add(s.toString().toInt())
                s = StringBuilder()
            }
        }
        if (s.isNotEmpty()) add(s.toString().toInt())
    }
}

fun String.rotate(distance: Int): String {
    val list = this.toMutableList()
    Collections.rotate(list, distance)
    return list.joinToString("")
}