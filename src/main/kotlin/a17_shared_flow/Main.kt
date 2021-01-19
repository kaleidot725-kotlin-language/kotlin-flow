package a17_shared_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    // Replay テスト
    launch {
        // collect するとそこでコルーチンの処理が止まるので別々のコルーチンで実行してやる
        println("replayTestStart: emitCount 10 replayCount 1")
        replayTest(emitCount = 10, replayCount = 1)
    }
    launch {
        // collect するとそこでコルーチンの処理が止まるので別々のコルーチンで実行してやる
        println("replayTestStart: emitCount 10 replayCount 5")
        replayTest(emitCount = 10, replayCount = 5)
    }
    launch {
        // collect するとそこでコルーチンの処理が止まるので別々のコルーチンで実行してやる
        println("replayTestStart: emitCount 10 replayCount 10")
        replayTest(emitCount = 10, replayCount = 10)
    }
}

/**
 * Replay の動作を確認するテスト
 * emitCount: エミットする値の数
 * replayCount: リプライする値の数
 */
suspend fun replayTest(emitCount: Int, replayCount: Int){
    val mutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = replayCount)
    for (i in 1..emitCount) {
        println("emit:${i}")
        mutableSharedFlow.emit(i)
    }

    val sharedFlow: SharedFlow<Int> = mutableSharedFlow.asSharedFlow()
    sharedFlow.onEach { println("collect:${it}") }.collect()
}

