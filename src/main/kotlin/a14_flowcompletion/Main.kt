package a14_flowcompletion

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun foo(): Flow<Int> = (1..3).asFlow()

fun foo2(): Flow<Int> = flow {
    emit(1)
    throw RuntimeException()
}

fun main(args: Array<String>) = runBlocking {
    println("==================")
    try {
        foo().collect { value -> println(value) }
    } finally {
        println("Done")
    }
    println("==================")

    println("==================")
    foo()
        .onCompletion { println("Done")}
        .collect { println(it) }
    println("==================")

    println("==================")
    foo2()
        .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally") }
        .catch { cause -> println("Caught exception") }
        .collect { value -> println(value) }
    println("==================")

    println("==================")
    try {
        foo()
            .onCompletion { cause -> println("Flow completed with $cause") }
            .collect { value ->
                check(value <= 1) { "Collected $value" }
                println(value)
            }
    } catch (e: Exception) {
        println(e)
    }
    println("==================")
}