package a09_buffering

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    time = measureTimeMillis {
        foo()
            .conflate() // conflate emissions, don't process each one
            .collect { value ->
                delay(300) // pretend we are processing it for 300 ms
                println("conflate $value")
            }
    }
    println("Conflate Collected in $time ms")

    time = measureTimeMillis {
        foo()
            .collectLatest { value -> // cancel & restart on the latest value
                println("Collecting $value")
                delay(300) // pretend we are processing it for 300 ms
                println("Done $value")
            }
    }
    println("CollectLatest Collected in $time ms")
}