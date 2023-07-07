package com.wonddak.loacell

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect class CommonFlow<T>(flow: Flow<T>) : Flow<T>

expect open class CommonStateFlow<T>(flow: StateFlow<T>) : StateFlow<T>

expect open class CommonMutableStateFlow<T>(flow: MutableStateFlow<T>) : MutableStateFlow<T>

fun <T> Flow<T>.toCommonFlow() = CommonFlow(this)

fun <T> StateFlow<T>.toCommonStateFlow() = CommonStateFlow(this)

fun <T> MutableStateFlow<T>.toCommonMutableStateFlow() = CommonMutableStateFlow(this)