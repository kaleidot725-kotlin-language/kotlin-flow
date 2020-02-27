package a04_flowbuilder

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    flowOf(1, 2, 3).collect { value -> println(value) }
    (1..3).asFlow().collect { value -> println(value) }
}