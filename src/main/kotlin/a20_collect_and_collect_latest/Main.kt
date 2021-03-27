package a20_collect_and_collect_latest

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

fun main(): Unit = runBlocking {
    println("TEST - COLLECT")
    testCollect()

    println("TEST - COLLECT LATEST")
    testCollectLatest()
}

private fun testCollect() = runBlocking {
    val anotherScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val sharedFlow = MutableSharedFlow<Long>(replay = 5)

    anotherScope.launch {
        sharedFlow.collect {
            delay(3000)
            println("observe $it")
        }
    }

    this.launch {
        for (i in 0L..10L) {
            delay(1000)

            sharedFlow.emit(i)
            println("emit $i")
        }

        delay(1000 * 30)
    }
}

private fun testCollectLatest() = runBlocking {
    val anotherScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val sharedFlow = MutableSharedFlow<Long>(replay = 5)

    anotherScope.launch {
        sharedFlow.collectLatest {
            delay(3000)
            println("observe $it")
        }
    }

    this.launch {
        for (i in 0L..10L) {
            delay(1000)

            sharedFlow.emit(i)
            println("emit $i")
        }

        delay(1000 * 30)
    }
}