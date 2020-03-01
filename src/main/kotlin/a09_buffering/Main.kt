package a09_buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun foo(): Flow<Int> = flow {
    for (i in 1..3) {
        println("foo $i")
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking {
    var time = measureTimeMillis {
        foo().collect { value ->
            delay(300)
            println("main $value")
        }
    }
    println("No Buffer Collected in $time ms")

    time = measureTimeMillis {
        foo().buffer().collect { value ->
            delay(300)
            println("main $value")
        }
    }
    println("Buffer Collected in $time ms")
}