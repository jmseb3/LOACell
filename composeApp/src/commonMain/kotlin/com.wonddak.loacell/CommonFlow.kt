package com.wonddak.loacell

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect class CommonFlow<T>(flow: Flow<T>) : Flow<T>

expect open class CommonStateFlow<T>(flow: StateFlow<T>) : StateFlow<T> {
    override val replayCache: List<T>
    override suspend fun collect(collector: FlowCollector<T>): Nothing
    override val value: T
}

expect open class CommonMutableStateFlow<T>(flow: MutableStateFlow<T>) : MutableStateFlow<T> {
    override val subscriptionCount: StateFlow<Int>
    override suspend fun emit(value: T)

    @ExperimentalCoroutinesApi
    override fun resetReplayCache()
    override fun tryEmit(value: T): Boolean
    override var value: T
    override fun compareAndSet(expect: T, update: T): Boolean
    override val replayCache: List<T>
    override suspend fun collect(collector: FlowCollector<T>): Nothing
}

fun <T> Flow<T>.toCommonFlow() = CommonFlow(this)

fun <T> StateFlow<T>.toCommonStateFlow() = CommonStateFlow(this)

fun <T> MutableStateFlow<T>.toCommonMutableStateFlow() = CommonMutableStateFlow(this)