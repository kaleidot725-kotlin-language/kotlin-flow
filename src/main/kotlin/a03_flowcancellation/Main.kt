package a03_flowcancellation

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

fun foo(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        println("Emitting $i")
        emit(i)
    }
}

@InternalCoroutinesApi
fun main() = runBlocking<Unit> {
    withTimeoutOrNull(250) { // Timeout after 250ms
        foo().collect { value -> println(value) }
    }
    println("Done")
}