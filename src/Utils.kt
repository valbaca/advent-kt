import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis


/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("input", "$name.txt").readLines()

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
 * [0, 0, 1, -1, -2, 3] {it < 0} => [[0, 0, 1], [-1, -2], [3]]
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
            Pair(accList, f(elem))
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