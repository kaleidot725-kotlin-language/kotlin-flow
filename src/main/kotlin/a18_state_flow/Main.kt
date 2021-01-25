package a18_state_flow

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    distinctTest2()
}

/**
 *  StateFlow の初期値のテスト
 */
fun initialValueTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}

fun emitTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}

fun emitTest2() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:2")
        mutableStateFlow.emit(2)

        delay(1000)
    }
}

fun distinctTest() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:0")
        mutableStateFlow.emit(0)

        delay(1000)
    }
}

fun distinctTest2() {
    runBlocking {
        val mutableStateFlow = MutableStateFlow<Int>(value = 0)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)

        mutableStateFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableStateFlow.emit(1)

        delay(1000)
    }
}