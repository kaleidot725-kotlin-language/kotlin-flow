package a06_terminalflowoperators

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    var count = 0
    var result = 0
    val sum = (1..5).asFlow().reduce { a, b ->
        result = a + b
        count++
        println("count $count a $a b $b result $result")

        result
    }
    println("sum $sum")

//    val b = (1..5).asFlow().fold(10) { a, b ->
//        result = a + b
//        count++
//        println("count $count a $a b $b result $result")
//        result
//    }
//    println("sum $b")
}