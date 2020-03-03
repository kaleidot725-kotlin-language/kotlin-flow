package a13_exceptiontransparency

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun foo(): Flow<Int> = flow {
    for (i in 1..3) {
        check(i <= 2)
        println("Emitting $i")
        emit(i)
    }
}

fun foo2(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    println("==================")
    foo()
        .catch { e ->
            println(e)
            emit(10000)
        }
        .collect { value -> println(value) }
    println("==================")

    println("==================")
    try {
        foo2()
            .catch { e -> println("Caught $e") } // does not catch downstream exceptions
            .collect { value ->
                check(value <= 1) { "Collected $value" }
                println(value)
            }
    } catch (e: Exception) {
        println(e)
    }
    println("==================")

    println("==================")
    foo2()
        .onEach { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
        .catch { e -> println("Caught $e") }
        .collect()
    println("==================")
}
