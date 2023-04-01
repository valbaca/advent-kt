import java.lang.invoke.MethodHandles
import java.lang.ref.WeakReference
import java.util.*

private val day = MethodHandles.lookup().lookupClass().name.removeSuffix("Kt")

/**
 * TIL: IdentityHashMap is useful if you know you're not creating duplicates. Using that allowed me to ignore paths and
 * names completely and just the memory address as a unique id.
 *
 * Also, using WeakRef for parent pointer is a good habit I picked up from Rust.
 *
 * Btw, I spent WAYYY too long on this because I had `allDirs` outside the part1 definition, so the example run
 * was getting merged into the real run and throwing off my results.
 * Mutable state strikes again.
 */
fun main() {
    day.println()

    fun getDirSizes(input: List<String>): List<Dir> {
        val root = Dir("/", null)
        val allDirs = IdentityHashMap<Dir, Dir>()
        allDirs[root] = root

        /**
         * Either returns the dir of the given name OR creates an empty one (and adds it to `this` and the allDirs list
         */
        fun Dir.mkdir(name: String): Dir {
            val existingDir = this.dirs[name]
            if (existingDir != null) {
                return existingDir
            }
            val dir = Dir(name, WeakReference(this))
            allDirs[dir] = dir
            this.dirs[name] = dir
            return dir
        }

        var pwd = root
        val ip = input.listIterator()
        while (ip.hasNext()) {
            val line = ip.next()
            when {
                line == "$ cd /" -> pwd = root
                line == "$ cd .." -> pwd = pwd.parent!!.get()!!
                line.startsWith("$ cd ") -> pwd = pwd.mkdir(line.removePrefix("$ cd "))
                line == "$ ls" || line.startsWith("dir") -> { /* Can just ignore. Wow */ }
                else -> {
                    val (size, name) = line.split(" ")
                    pwd.addFile(File(name, size.toLong()))
                }
            }
        }
        return allDirs.values.toList()
    }

    fun part1(input: List<String>): Long {
        return getDirSizes(input).map { it.size }.filter { it <= 100_000 }.sumOf { it }
    }

    fun part2(input: List<String>): Long {
        val dirs = getDirSizes(input)
        val root = dirs.maxOfOrNull { it.size }!!
        val tgt = 30000000L - (70000000L - root)
        return dirs.map { it.size }.filter { it >= tgt }.min()
    }

    checkEq(95437L, part1(readInput("${day}_ex")))

    val input = readInput(day)

    println("Part 1:")
    solve { part1(input) }


    checkEq(24933642L, part2(readInput("${day}_ex")))
    println("Part 2:")
    solve { part2(input) }
}

data class File(val name: String, val size: Long)
data class Dir(
    val name: String,
    val parent: WeakReference<Dir>? = null,
    val files: MutableMap<String, File> = mutableMapOf(),
    val dirs: MutableMap<String, Dir> = mutableMapOf(),
    var size: Long = 0L,
) {
    fun addFile(file: File) = files.putIfAbsent(file.name, file) ?: this.increaseSize(file.size)
    private fun increaseSize(n: Long) {
        this.size += n
        this.parent?.get()?.increaseSize(n)
    }
}