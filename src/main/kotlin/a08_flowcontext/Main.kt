package a08_flowcontext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun foo(): Flow<Int> = flow {
    println("Started foo flow")
    for (i in 1..3) {
        emit(i)
    }
}

fun foo2() : Flow<Int> = flow {
    kotlinx.coroutines.withContext(Dispatchers.Default) {
        for (i in 1..3) {
            Thread.sleep(100)
            emit(i)
        }
    }
}

fun foo3() : Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100)
            emit(i)
        }
}.flowOn(Dispatchers.Default)

fun main() = runBlocking<Unit> {
    println("main start")

    foo().collect { value -> println("foo1 Collected $value") }

    try {
        foo2().collect { value -> println("foo2 Collected $value") }
    } catch (e: Exception) {
        println(e.message)
    }

    foo3().collect { value -> println("foo3 Collected $value") }

    println("main end")
}