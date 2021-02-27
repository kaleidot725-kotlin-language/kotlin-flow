package a19_callback_flow

import a19_callback_flow.model.Counter
import a19_callback_flow.model.OnChangedListener
import jdk.nashorn.internal.objects.Global
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

val counter = Counter()
val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

fun main() {
    generalCallbackSample()
    callbackFlowCancelSample()
    callbackFlowCancelSample()
}

/**
 * 通常のコールバックでカウント値を収集する時の動作
 */
private fun generalCallbackSample() {
    // リスナーを作成する
    val listener = object : OnChangedListener {
        override fun onChanged(count: Int) {
            println("count:$count")
        }
    }

    // リスナーを登録する
    counter.addOnChangedListener(listener)

    // カウントする
    repeat(10) { counter.increment() }
    repeat(10) { counter.decrement() }

    // リスナーを解除する
    counter.removeOnChangedListener(listener)
}

/**
 * callbackFlow が起動された時の動作
 */
private fun callbackFlowSample() = runBlocking {
    // 特定の scope で callbackFlow を起動してデータ購読を開始する
    scope.launch {
        val flow: Flow<Int> = createCallbackFlow(counter)
        flow.collect { println("count:$it") }
    }

    // 少し待ってからカウントする
    delay(100)
    repeat(10) { counter.increment() }
    repeat(10) { counter.decrement() }

    // カウントの通知が完了するまで少し待つ
    delay(100)

    // callbackFlow を起動した scope をキャンセルしてデータ購読を終了する
    scope.cancel()
}

/**
 * callbackFlow がキャンセルされた場合の動作
 */
private fun callbackFlowCancelSample() = runBlocking {
    scope.launch {
        val flow: Flow<Int> = createCallbackFlow(counter)
        flow.collect { println("count:$it") }
    }

    delay(1000)

    scope.cancel()
}

/**
 * callbackFlow を生成する
 */
private fun createCallbackFlow(counter: Counter): Flow<Int> = callbackFlow {
    // リスナーを作成する
    val listener = object : OnChangedListener {
        override fun onChanged(count: Int) {
            offer(count)
        }
    }

    // リスナーを登録する
    counter.addOnChangedListener(listener)

    // callbackFlow を起動しているコルーチンがキャンセルされたらリスナーを解除する
    awaitClose {
        counter.removeOnChangedListener(listener)
    }
}