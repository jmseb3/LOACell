package com.wonddak.loacell

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

actual open class CommonFlow<T> actual constructor(
    private val flow: Flow<T>
) : Flow<T> by flow {
    fun collect(
        onCollect: (T) -> Unit
    ): DisposableHandle {
        val job = CoroutineScope(Dispatchers.Main).launch {
            flow.collect(onCollect)
        }
        return DisposableHandle { job.cancel() }
    }
}

actual open class CommonStateFlow<T> actual constructor(
    private val flow: StateFlow<T>
) : CommonFlow<T>(flow), StateFlow<T> {

    actual override val replayCache: List<T>
        get() = flow.replayCache

    actual override val value: T
        get() = flow.value

    actual override suspend fun collect(collector: FlowCollector<T>) = flow.collect(collector)
}

actual open class CommonMutableStateFlow<T> actual constructor(
    private val flow: MutableStateFlow<T>
) : CommonStateFlow<T>(flow), MutableStateFlow<T> {
    actual override val subscriptionCount: StateFlow<Int>
        get() = flow.subscriptionCount

    actual override suspend fun emit(value: T) = flow.emit(value)

    @ExperimentalCoroutinesApi
    actual override fun resetReplayCache() = flow.resetReplayCache()

    actual override fun tryEmit(value: T): Boolean = flow.tryEmit(value)

    actual override fun compareAndSet(expect: T, update: T): Boolean  = flow.compareAndSet(expect,update)

    actual override val replayCache: List<T>
        get() = flow.replayCache

    actual override var value: T
        get() = flow.value
        set(value) {
            flow.value = value
        }

    actual override suspend fun collect(collector: FlowCollector<T>): Nothing = flow.collect(collector)
}

class IOSMutableStateFlow<T>(
    initialValue: T
) : CommonMutableStateFlow<T>(MutableStateFlow(initialValue))
