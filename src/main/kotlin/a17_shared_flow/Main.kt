package a17_shared_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.util.*

fun main() = runBlocking<Unit> {
//    println("start")
//    defaultTest()

    println("start: emitCount 10 bufferOption BufferOverflow.SUSPEND")
    bufferTest(10, replayCount = 0, extraBufferCapacity = 10, bufferOverflow = BufferOverflow.SUSPEND)

//    println("start: emitCount 10 replayCount 0")
//    replayTest(emitCount = 10, replayCount = 0)
//
//    println("start: emitCount 10 replayCount 1")
//    replayTest(emitCount = 10, replayCount = 1)
//
//    println("start: emitCount 10 replayCount 5")
//    replayTest(emitCount = 10, replayCount = 5)
//
//    println("start: emitCount 10 replayCount 10")
//    replayTest(emitCount = 10, replayCount = 10)
}

/**
 * SharedFlow の基本的な動作のテスト
 */
fun defaultTest() {
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>()

        println("emit:0")
        mutableSharedFlow.emit(0)

        delay(1000)

        mutableSharedFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        println("emit:1")
        mutableSharedFlow.emit(1)

        delay(1000)
    }
}

/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
fun replayTest(emitCount: Int, replayCount: Int){
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount)

        for (i in 1..emitCount) {
            println("emit:${i}")
            mutableSharedFlow.emit(i)
        }

        mutableSharedFlow.onEach {
            println("collect:${it}")
        }.launchIn(GlobalScope)

        // 値のコレクトが完了するまで待つ
        delay(1000)
    }
}

/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
fun bufferTest(emitCount: Int, replayCount: Int, bufferOverflow: BufferOverflow, extraBufferCapacity: Int) {
    runBlocking {
        val mutableSharedFlow = MutableSharedFlow<Int>(replay = replayCount, extraBufferCapacity = extraBufferCapacity, onBufferOverflow = bufferOverflow)

        mutableSharedFlow.onEach {
            delay(100)
            println("slow:collect: ${it}")
        }.launchIn(GlobalScope)

        mutableSharedFlow.onEach {
            println("collect: ${it}")
        }.launchIn(GlobalScope)

        delay(1000)

        for (i in 1..emitCount) {
            println("emit: value ${i}")
            mutableSharedFlow.emit(i)
        }

        // 値のコレクトが完了するまで待つ
        delay(2000)
    }
}

