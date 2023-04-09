import java.lang.invoke.MethodHandles
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: It's easier to merge ranges than I thought.
 * This was the first one (I think?) this year that required Long.
 *
 * I knew my part1 soln was not going to be efficient enough for part2.
 * Thankfully going from Set to merged Ranges was only moderately difficult.
 *
 * I could probably go back and make part1 cleaner and faster but leaving it as-is.
 */
fun main() {
    day.println()


    fun getAllNoBeacons(sbs: List<SensorBeacon>, y: Int, includeBeacons: Boolean = false) = sbs.map {
        it.getNoBeacons(y, includeBeacons)
    }.reduce { acc, set ->
        acc.addAll(set)
        acc
    }

    fun part1(y: Int, input: List<String>): Int {
        val sbs = input.map { it.toSensorBeacon() }
        return getAllNoBeacons(sbs, y)
            .size
    }


    fun mergeRanges(ranges: List<IntRange>): List<IntRange> {
        val sortedRanges = ranges.sortedWith { o1, o2 ->
            checkNotNull(o1)
            checkNotNull(o2)
            o1.first.compareTo(o2.first)
        }

        val merged: MutableList<IntRange> = sortedRanges.fold(mutableListOf()) { acc, range ->
            val lastRange = acc.lastOrNull()
            if (lastRange != null && range.first in lastRange) {
                val last = max(lastRange.last, range.last)
                acc[acc.size - 1] = lastRange.first..last
            } else {
                acc.add(range)
            }
            acc
        }
        return merged
    }

    fun truncateRanges(limit: Int, ranges: List<IntRange>): List<IntRange> {
        return ranges.mapNotNull { range ->
            val first = max(range.first, 0)
            val last = min(range.last, limit)
            if (first > last) {
                null
            } else {
                first..last
            }
        }
    }

    fun part2(limit: Int, input: List<String>): Long {
        val sbs = input.map { it.toSensorBeacon() }
        val range = 0..limit
        val out = mutableListOf<Long>()
        for (y in range) {
            val noBeaconRanges = mergeRanges(
                truncateRanges(limit, sbs.mapNotNull { it.getNoBeaconRange(y) })
            )
            if (noBeaconRanges.size == 1) {
                val nbr = noBeaconRanges[0]
                if (nbr.last - nbr.first == limit) continue
            }
            if (noBeaconRanges.size > 2) {
                throw IllegalStateException()
            }
            for (x in range) {
                val xInAny = noBeaconRanges.any { x in it }
                if (!xInAny) {
                    println("x=$x y=$y")
                    return x * 4_000_000L + y
                }
            }
        }
        return -1L
    }

    checkEq(12, part1(10, listOf("Sensor at x=8, y=7: closest beacon is at x=2, y=10")))
    checkEq(26, part1(10, readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(2_000_000, input) }

    println("Part 2:")
    checkEq(56000011L, part2(20, readInput("${day}_ex")))

    solve { part2(4_000_000, input) }
}


data class SensorBeacon(val sensor: Cord, val beacon: Cord) {
    fun getNoBeacons(y: Int, includeBeacons: Boolean = false): MutableSet<Int> {
        val dist = abs(sensor.first - beacon.first) + abs(sensor.second - beacon.second)
        val ydiff = abs(sensor.second - y)
        if (ydiff > dist) {
            return mutableSetOf()
        }
        val xstart = (sensor.first - dist) + ydiff
        val xend = (sensor.first + dist) - ydiff
        val set = mutableSetOf<Int>()
        for (x in xstart..xend) {
            set.add(x)
        }
        if (!includeBeacons) {
            if (y == beacon.second) {
                set.remove(beacon.first)
            }
        }
        return set
    }

    fun getNoBeaconRange(y: Int): IntRange? {
        val dist = abs(sensor.first - beacon.first) + abs(sensor.second - beacon.second)
        val ydiff = abs(sensor.second - y)
        if (ydiff > dist) {
            return null
        }
        val xstart = (sensor.first - dist) + ydiff
        val xend = (sensor.first + dist) - ydiff
        return xstart..xend
    }
}

private fun String.toSensorBeacon(): SensorBeacon {
    val splits = this.split(" ")
    val sx = splits[2].removePrefix("x=").removeSuffix(",").toInt()
    val sy = splits[3].removePrefix("y=").removeSuffix(":").toInt()
    val bx = splits[8].removePrefix("x=").removeSuffix(",").toInt()
    val by = splits[9].removePrefix("y=").toInt()
    return SensorBeacon(
        sx to sy, bx to by
    )
}