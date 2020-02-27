package a05_intermediateflowoperators

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking



fun main() = runBlocking {

    // Map を利用した例
    suspend fun performRequest(request: Int): String {
        return "response $request"
    }

    (1..3).asFlow().map { request -> performRequest(request) }.collect { response -> println(response) }

    // Transform を利用した例
    (1..3).asFlow().transform { request ->
        emit("Making request $request")
        emit(performRequest(request))
    }.collect { response ->
        println(response)
    }

    // Take を利用した例
    val takeFlow = flow {
        try {
            emit(1)
            emit(2)
            emit(3)
        } catch (e: Exception) {
            println(e.message)
        } finally {
            println("Finally in numbers")
        }
    }

    takeFlow.take(2).collect { value -> println(value) }
}