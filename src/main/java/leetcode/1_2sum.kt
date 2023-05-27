package leetcode

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

fun twoSum(nums: IntArray, target: Int): IntArray {
    val map = HashMap<Int, Int>()
    for ((index, i) in nums.withIndex()) {
        if (map.containsKey(target - i)) return intArrayOf(index, map[target - i]!!)
        map[i] = index
    }
    return intArrayOf()
}

fun main() {
    val tests: List<Case1> = Gson().fromJson(File("./src/main/java/leetcode/json/test_1.json").readText())
    tests.forEach { case -> assert(twoSum(case.input.nums, case.input.target) == case.output) }
}

private data class Input1(
    val nums: IntArray,
    val target: Int
)

private data class Case1(
    val contributor: String,
    val input: Input1,
    val output: IntArray
)

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
