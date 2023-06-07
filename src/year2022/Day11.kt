package year2022

import checkEq
import partitionBy
import println
import readInput
import solve
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

val PART1: Function1<Long, Long> = {it / 3}
var RELIEF: Function1<Long, Long> = PART1

/**
 * TIL: modulo...I should've known. I did look for a hint but I did understand why it worked.
 * Using a global var function...not the most elegant but it works!
 */
fun main() {
    day.println()
    fun part1(input: List<String>): Long {
        val monkeyInputs = input.partitionBy { it == "" }.filterNot { it == listOf("") }
        val monkeys = monkeyInputs.map { parseToMonkey(it) }
        RELIEF = PART1
        repeat(20) {
            game(monkeys)
        }
        return monkeys
            .map { it.inspected }
            .sortedDescending()
            .take(2)
            .reduce { a, e -> a * e }
    }

    fun part2(input: List<String>): Long {
        val monkeyInputs = input.partitionBy { it == "" }.filterNot { it == listOf("") }
        val monkeys = monkeyInputs.map { parseToMonkey(it) }
        val modulo = monkeys.map { it.divisor }.reduce {a,e -> a * e}
        RELIEF = {it % modulo}
        repeat(10_000) {
            game(monkeys)
        }
        return monkeys
            .map { it.inspected }
            .sortedDescending()
            .take(2)
            .reduce { a, e -> a * e }
    }

    checkEq(10605L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2:")
    checkEq(2713310158L, part2(readInput("${day}_ex")))
    solve { part2(input) }
}

typealias Worry = Long

data class Monkey(
    val id: Int,
    val divisor: Long,
    val toTrue: Int,
    val toFalse: Int,
    val items: ArrayDeque<Worry> = ArrayDeque(),
    var inspected: Long = 0,
    val operationStr: String,
    val operation: (Long) -> Long
) {
    fun inspect() = buildList {
        while (items.isNotEmpty()) {
            val item = items.removeFirst()
            val itemAfterInspection = operation(item)
            val itemAfterRelief = RELIEF(itemAfterInspection)
            val toMonkey = if (itemAfterRelief % divisor == 0L) toTrue else toFalse
            add(itemAfterRelief to toMonkey)
            inspected++
        }
    }
}

fun parseToMonkey(input: List<String>): Monkey {
    val id = input[0].removePrefix("Monkey ").removeSuffix(":").toInt()
    val items =
        input[1].trim().removePrefix("Starting items: ").split(", ").map { it.toLong() }.toCollection(ArrayDeque())
    val operationStr = input[2].trim().removePrefix("Operation: new = old ").split(" ")
    val (op, arg) = operationStr[0] to operationStr[1]
    val operation: Function1<Long, Long> = when (op) {
        "+" -> if (arg == "old") {
            { it + it }
        } else {
            { it + arg.toLong() }
        }

        "*" -> if (arg == "old") {
            { it * it }
        } else {
            { it * arg.toLong() }
        }

        else -> throw IllegalArgumentException("Given $op $arg")
    }
    val divisor = input[3].trim().removePrefix("Test: divisible by ").toLong()
    val toTrue = input[4].trim().removePrefix("If true: throw to monkey ").toInt()
    val toFalse = input[5].trim().removePrefix("If false: throw to monkey ").toInt()
    return Monkey(
        id,
        divisor,
        toTrue,
        toFalse,
        items,
        operationStr = operationStr.joinToString(" "),
        operation = operation
    )
}

fun game(monkeys: List<Monkey>): List<Monkey> {
    for (m in monkeys.indices) {
        val thrown = monkeys[m].inspect()
        for (itemToMonkey in thrown) {
            val (item, toMonkey) = itemToMonkey
            monkeys[toMonkey].items.addLast(item)
        }
    }
    return monkeys
}