package year2022

import checkEq
import println
import readInput
import solve
import java.lang.invoke.MethodHandles

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: DeepRecursiveFunction is great but has a major limitation: you cannot invoke any other function within it!
 * Ended up not even needing to worry about a deep call stack for part 2, so just used a normal function.
 *
 * This could certainly be cleaned up in to more succinct code, it's really repetitive and sprawling but takes less than
 * 20ms
 */

enum class Day21Op(val value: Char) {
    ADD('+'),
    SUB('-'),
    MUL('*'),
    DIV('/');

    companion object {
        infix fun from(value: Char): Day21Op = Day21Op.values().firstOrNull { it.value == value }!!
    }
}

fun main() {
    day.println()
    data class Monkey(var value: Long? = null, val str: String) {

    }

    fun makeYell(tree: Map<String, Monkey>): DeepRecursiveFunction<Monkey, Long> =
        DeepRecursiveFunction { monkey: Monkey ->
            val value = monkey.value
            if (value != null) {
                value
            } else {
                val splits = monkey.str.split('+', '-', '*', '/').map { it.trim() }
                check(splits.size == 2)
                val (a, b) = splits.map { tree[it]!! }.map { callRecursive(it) }
                val v = when {
                    monkey.str.contains('+') -> a + b
                    monkey.str.contains('-') -> a - b
                    monkey.str.contains('*') -> a * b
                    monkey.str.contains('/') -> a / b
                    else -> throw IllegalStateException()
                }
                monkey.value = v
                v
            }
        }


    fun part1(input: List<String>): Long {
        val tree: Map<String, Monkey> = input.associate { line ->
            val (id, str) = line.split(": ")
            id to Monkey(str.toLongOrNull(), str)
        }

        val yell = makeYell(tree)

        return yell(tree["root"]!!)
    }


    data class Equation(val left: String, val op: Day21Op, val right: String)
    data class Chimp(val id: String, var value: Long?, val eq: Equation?)

    fun part2(input: List<String>): Long {
        val tree: Map<String, Chimp> = input.associate { line ->
            val (id, str) = line.split(": ")
            val value = str.toLongOrNull()
            val eq = if (value != null) null else {
                val splits = str.split(' ').map { it.trim() }.filter { it.isNotEmpty() }
                Equation(splits[0], ((Day21Op from splits[1].first())), splits[2])
            }
            id to Chimp(id, value, eq)
        }

        val hasHumn = DeepRecursiveFunction { chimp: Chimp ->
            if (chimp.id == "humn") {
                true
            } else if (chimp.value != null) {
                false
            } else {
                callRecursive(tree[chimp.eq!!.left]!!) || callRecursive(tree[chimp.eq.right]!!)
            }

        }
        val root = tree["root"]!!
        val rootEq = root.eq!!
        val left = tree[rootEq.left]!!
        val right = tree[rootEq.right]!!
        check(hasHumn(left) xor hasHumn(right))
        val (valueNode, otherNode) = if (hasHumn(left)) {
            right to left
        } else {
            left to right
        }

        val getValue = DeepRecursiveFunction<Chimp, Long> { chimp: Chimp ->
            val value = chimp.value
            if (value != null) {
                value
            } else {
                val (a, b) = callRecursive(tree[chimp.eq!!.left]!!) to callRecursive(tree[chimp.eq.right]!!)
                val v = when(chimp.eq.op) {
                    Day21Op.ADD -> a + b
                    Day21Op.SUB -> a - b
                    Day21Op.MUL -> a * b
                    Day21Op.DIV -> a / b
                }
                chimp.value = v
                v
            }
        }

        var value = getValue(valueNode)

        fun inverse(chimp: Chimp): Long {
            if (chimp.id == "humn") {
                return value
            }
            val left = tree[chimp.eq!!.left]!!
            val right = tree[chimp.eq.right]!!
            //            check(hasHumn(left) &  hasHumn(right))
            val isHumnLeft = hasHumn(left)
            val const = if (isHumnLeft) getValue(right) else getValue(left)

            if (isHumnLeft) {
                // value = humn OP const => value INVOP = human
                value = when(chimp.eq.op) {
                    Day21Op.ADD -> value - const
                    Day21Op.SUB -> value + const
                    Day21Op.MUL -> value / const
                    Day21Op.DIV -> value * const
                }
            } else {
                value = when(chimp.eq.op) {
                    // value = const OP humn
                    Day21Op.ADD -> value - const
                    Day21Op.SUB -> -1 * (value - const)
                    Day21Op.MUL -> value / const
                    Day21Op.DIV -> (1/value) * const
                }
            }
            return if (isHumnLeft) inverse(left) else inverse(right)
        }

        return inverse(otherNode)
    }

    checkEq(152L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }
    println("Part 2 ex:")
    checkEq(301L, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}
