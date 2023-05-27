package leetcode

import com.google.gson.Gson
import java.io.File


/**
 * Example:
 * var li = ListNode(5)
 * var v = li.`val`
 * Definition for singly-linked list.
 */
class ListNode(var `val`: Int) {
    var next: ListNode? = null
}


fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
    val head = ListNode(-1)
    var curr = head
    var carry = 0
    var l1Head = l1
    var l2Head = l2

    while (l1Head != null || l2Head != null) {
        var currDigit = (l1Head?.`val` ?: 0) + (l2Head?.`val` ?: 0) + carry
        if (currDigit >= 10) {
            carry = 1
            currDigit -= 10
        } else {
            carry = 0
        }
        curr.next = ListNode(currDigit)
        curr = curr.next!!

        l1Head = l1Head?.next
        l2Head = l2Head?.next
    }
    if (carry != 0) {
        curr.next = ListNode(1)
    }
    printList(head.next)
    return head.next
}

private data class Input2(
    val l1: IntArray,
    val l2: IntArray,
)

private data class Case2(
    val contributor: String,
    val input: Input2,
    val output: IntArray
)

private fun buildList(nums: IntArray): ListNode {
    if (nums.isEmpty()) return ListNode(-1)
    val head: ListNode? = ListNode(nums[0])
    var curr = head
    for (i in 1 until nums.size) {
        curr?.next = ListNode(nums[i])
        curr = curr?.next
    }
//    printList(head)
    return head!!
}

private fun printList(head: ListNode?) {
    var curr = head
    while (curr != null) {
        print("" + curr.`val` + "->")
        curr = curr.next
    }
    println()
}
fun main() {
    val tests: List<Case2> = Gson().fromJson(File("./src/main/java/leetcode/json/test_2.json").readText())
    tests.forEach { case ->
        addTwoNumbers(
            buildList(case.input.l1),
            buildList(case.input.l2)
        )?.let {
            assert(
                it == buildList(case.output)
            )
        }
    }
}

