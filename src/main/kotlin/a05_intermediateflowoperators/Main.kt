package a05_intermediateflowoperators

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

suspend fun performRequest(request: Int): String {
    return "response $request"
}

fun main() = runBlocking {
    (1..3).asFlow().map { request -> performRequest(request) }.collect { response -> println(response) }
}
