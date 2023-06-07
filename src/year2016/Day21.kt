package year2016

import arrow.fx.coroutines.timeInMillis
import checkEq
import ints
import kotlinx.coroutines.*
import println
import readInput
import rotate
import solve
import java.lang.invoke.MethodHandles
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TIL:
 * Don't trust s.split("").toList() as you get empty strings on both ends.
 * Instead, simply use s.toList()
 *
 * Brute force ftw. This is exactly why passwords should be long.
 *
 * This blog *FINALLY* explained how to run CPU-intensive coroutines in parallel threads
 * https://silica.io/understanding-kotlin-coroutines/5/ 
 */
private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

fun main() {
    day.println()
    fun exec(s: String, ins: String): String {
        val isplit = ins.split(" ")
        return when {
            ins.startsWith("swap position") -> {
                val (x,y) = ins.ints()
                s.replaceRange(x.rangeTo(x), s[y].toString())
                    .replaceRange(y.rangeTo(y), s[x].toString())
            }
            ins.startsWith("swap letter") -> {
                val x = isplit[2]
                val xi = s.indexOf(x)
                val y = isplit[5]
                val yi = s.indexOf(y)
                s.replaceRange(xi.rangeTo(xi), y)
                    .replaceRange(yi.rangeTo(yi), x)

            }
            ins.startsWith("rotate based") -> {
                val x = isplit[6]
                val xi = s.indexOf(x)
                val rotate =  xi + if (xi >= 4) 2 else 1
                s.rotate(rotate)

            }
            ins.startsWith("rotate") -> {
                var steps = isplit[2].toInt()
                if (isplit[1] == "left") {
                    steps = -steps
                }
                s.rotate(steps)
            }
            ins.startsWith("reverse") -> {
                val x = isplit[2].toInt()
                val y = isplit[4].toInt()
                s.substring(0, x) + s.substring(x, y+1).reversed() + s.substring(y+1)
            }
            ins.startsWith("move") -> {
                val x = isplit[2].toInt()
                val y = isplit[5].toInt()
                val slist = s.toMutableList()
                val sub = slist.removeAt(x)
                slist.add(y, sub)
                slist.joinToString("")
            }
            else -> error("Bad ins ${ins}, s=${s}")
        }
    }

    fun part1(start: String, input: List<String>): String {
        return input.fold(start) { s, ins ->
//            println("s=${s} ins=${ins}")
            exec(s, ins)
        }
    }

    fun part2(input: List<String>): Int {
        val tgt = "fbgdceah"
        val found = AtomicBoolean(false)
        val threads = 8
        runBlocking(Dispatchers.Default) {
            val jobs = List(threads) {
                async {
//                    println("Starting ${it} @ ${timeInMillis()} on ${Thread.currentThread().name}")
                    val hash = tgt.toMutableList()
                    while (!found.get()) {
                        hash.shuffle()
                        val hashish = hash.joinToString("")
                        val out_pass = part1(hashish, input)
                        if (out_pass == tgt) {
                            found.set(true)
//                            println("${it} FOUND! => $hashish")
//                            println("Ending ${it} @ ${timeInMillis()}")
                            return@async hashish
                        }
                    }
//                    println("Ending ${it} @ ${timeInMillis()}")
                    return@async null
                }
            }
            jobs.awaitAll()
            jobs.mapNotNull { it.await() }[0].println()
        }
        return 0
    }

    checkEq("decab", part1("abcde", readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1("abcdefgh", input) }
    println("Part 2:")
    solve { part2(input) }
}
