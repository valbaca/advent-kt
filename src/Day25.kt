import java.lang.invoke.MethodHandles
import kotlin.math.abs
import kotlin.math.pow

/**
 * TIL: one single toInt threw off the whole calculation. I again had to reach to ClouddJR's solution (which he did in 12 LINES?!?)
 * to do some sanity checking. But really thinking about how I knew my solution worked for small numbers gave the real
 * hint that it was something around rounding.
 *
 * I should really spend time going through all of his solutions and learn a thing or 25.
 */
private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()

    fun ipow(base: Long, power: Long): Long {
        return base.toDouble().pow(power.toDouble()).toLong()
    }

    val snafuValues = listOf(-2L, -1L, 0L, 1L, 2L)


    fun decimalDigitToSnafu(d: Long): Char = when(d) {
        -2L -> '='
        -1L -> '-'
        0L -> '0'
        1L -> '1'
        2L -> '2'
        else -> error("")
    }

    fun snafuToIntDigit(ch:Char): Long = when(ch) {
        '=' -> -2
        '-' -> -1
        '0' -> 0
        '1' -> 1
        '2' -> 2
        else -> error("")
    }

    fun snafuToDecimal(s: String): Long {
        return s.reversed().toCharArray().foldIndexed(0) { idx, acc, ch ->
            acc + (5.0.pow(idx.toDouble()).toLong() * snafuToIntDigit(ch))
        }
    }

    fun decimalToSnafu(n: Long): String {
        if (n < 1L) return "0"
        var digits = 0L // current digits place (the # of digits is one more)
        var leftDigit = 1L
        // using 1 or 2 following by all 2s to find the upper bound
        while (true) {
            if (n > snafuToDecimal("$leftDigit" + "2".repeat(digits.toInt()))) {
                if (leftDigit == 1L) {
                    leftDigit++
                } else {
                    leftDigit = 1L
                    digits++
                }
            } else {
                break
            }
        }

        var rem = n - (leftDigit * ipow(5L, digits))
        digits--
        val snafus = mutableListOf<Long>(leftDigit)
        while (digits >= 0L) {
            val snafuMap = snafuValues.map { snaf ->
                snaf to rem - (snaf * ipow(5L, digits))
            }
            val snafRem = snafuMap.minByOrNull { (snaf, newRem) -> abs(newRem) }!!
            snafus.add(snafRem.first)
            rem = snafRem.second
            digits--
        }
        checkEq(0L, rem)
        return snafus.map {decimalDigitToSnafu(it)}.joinToString("")
    }

    fun snafuTest() {
        for (i in 0..10) {
            val snaf = decimalToSnafu(i.toLong())
            val dec = snafuToDecimal(snaf)
            println("$i => $snaf -> $dec")
        }
        for (i in listOf(15, 20, 2022, 12345, 314159265)){
            val snaf = decimalToSnafu(i.toLong())
            val dec = snafuToDecimal(snaf)
            println("$i => $snaf -> $dec")

        }
        val lines = readInput("${day}_ex_snafu").map { it.trim() }
        for (input in lines) {
            val dec = snafuToDecimal(input)
            checkEq(input, decimalToSnafu(dec))
            println("$input -> $dec -> ${decimalToSnafu(dec)}")
        }
    }
//    snafuTest()

    fun part1(input: List<String>): Long {
        val sum = input.map { it.trim() }.sumOf { snafuToDecimal(it) }
        println("Decimal: $sum")
        println("Snafu: ${decimalToSnafu(sum)}")
        return sum
    }

    checkEq(4890L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
}