package a19_callback_flow

import a19_callback_flow.model.Counter
import a19_callback_flow.model.OnChangedListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

fun main() = runBlocking<Unit> {
    generalCallbackSample()
}

private fun generalCallbackSample() {
    val counter = Counter()
    val listener = object : OnChangedListener {
        override fun onChanged(count: Int) {
            println("count:$count")
        }
    }

    counter.addOnChangedListener(listener)
    repeat(10) { counter.increment() }
    repeat(10) { counter.decrement() }
    counter.removeOnChangedListener(listener)
}

private suspend fun callbackFlowSample() {
    val counter: Counter = Counter()
    val flow: Flow<Int> = createCallbackFlow(counter)

    flow.onEach { println("count:$it") }.launchIn(GlobalScope)

    delay(100)

    repeat(10) { counter.increment() }

    repeat(10) { counter.decrement() }

    delay(100)
}

private fun createCallbackFlow(counter: Counter): Flow<Int> = callbackFlow {
    val listener = object : OnChangedListener {
        override fun onChanged(count: Int) {
            offer(count)
        }
    }

    counter.addOnChangedListener(listener)
    awaitClose { counter.removeOnChangedListener(listener) }
}