package flows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun foo(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Start")
        delay(100)
        emit(i)
        println("End")
    }
}

fun main() = runBlocking<Unit> {
    foo().collect { value -> println(value) }
}