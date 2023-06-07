package year2022

import checkEq
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import partitionBy
import println
import readInput
import solve
import toPair
import java.lang.invoke.MethodHandles
import kotlin.math.min

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: *KIND OF* "cheated" by noticing the input was a subset of JSON, so could just use a JSON
 * parser: Jackson.
 * Had some off-by one errors b/c they used 1-based index.
 * Other than that, just standard Comparators.
 */
fun main() {
    day.println()
    fun part1(input: List<String>): Int {
        return input.partitionBy { it.isNotEmpty() }.filter { it != listOf("") }
            .map { pair -> pair.map(String::toItem).toPair() }.mapIndexed { i, (left, right) ->
                val cmp = left.compareTo(right)
                if (cmp < 0) {
                    i + 1 // ugh, off by one error
                } else {
                    0
                }
            }.sum()
    }

    fun part2(input: List<String>): Int {
        val two = "[[2]]"
        val six = "[[6]]"
        val sorted = input.filter { it.isNotEmpty() }.plus(listOf(two, six)).map { it.toItem() }.sorted()
        return (sorted.indexOf(two.toItem()) + 1) * (sorted.indexOf(six.toItem()) + 1)
    }


    checkEq(13, part1(readInput("${day}_ex")))
    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }

    checkEq(140, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}

open class Item : Comparable<Item> {
    override fun compareTo(other: Item): Int {
        return when {
            this is Num && other is Num -> value.compareTo(other.value)
            this is Lst && other is Lst -> {
                val shortest = min(lst.size, other.lst.size)
                for (i in 0 until shortest) {
                    val compElems = lst[i].compareTo(other.lst[i])
                    if (compElems == 0) continue // in order
                    return compElems // not in order, break out
                }
                lst.size.compareTo(other.lst.size)
            }
            this is Num && other is Lst -> Lst(listOf(this)).compareTo(other)
            this is Lst && other is Num -> this.compareTo(Lst(listOf(other)))
            else -> throw IllegalArgumentException()
        }
    }
}

data class Num(val value: Int) : Item()
data class Lst(val lst: List<Item>) : Item()


val om = ObjectMapper()

fun JsonNode.toItem(): Item {
    if (this.isInt) {
        return Num(this.intValue())
    }
    if (!this.isArray) throw IllegalArgumentException()
    val lst = buildList {
        for (elem in this@toItem.elements()) {
            add(elem.toItem())
        }
    }
    return Lst(lst)
}

fun String.toItem(): Item {
    return om.readTree(this).toItem()
}
