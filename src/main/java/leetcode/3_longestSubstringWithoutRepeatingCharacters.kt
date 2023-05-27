package leetcode

import com.google.gson.GsonBuilder
import java.io.File

fun lengthOfLongestSubstring(s: String): Int {
    if (s.length < 2) return s.length
    var turtle = 0
    val map = HashMap<Char, Int>()

    var max = 0

    for (hare in s.indices) {
        turtle = turtle.coerceAtLeast(map[s[hare]]?.plus(1) ?: 0)
        map[s[hare]] = hare
        max = max.coerceAtLeast(hare - turtle + 1)
    }
    return max
}

fun main() {
    val tests: List<Case3> = GsonBuilder().serializeNulls().create().fromJson(
        File("./src/main/java/leetcode/json/test_3.json").readText()
    )
    println(tests)
    tests.forEach { case -> assert(lengthOfLongestSubstring(case.input.s) == case.output) }
}

private data class Input3(
    val s: String
)

private data class Case3(
    val contributor: String,
    val input: Input3,
    val output: Int
)
