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
    foo()
        .catch { e ->
            println(e)
            emit(10000)
        }
        .collect { value -> println(value) }

    foo2()
        .catch { e -> println("Caught $e") } // does not catch downstream exceptions
        .collect { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
}
