import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


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
