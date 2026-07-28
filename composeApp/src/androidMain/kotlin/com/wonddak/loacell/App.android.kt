package com.wonddak.loacell

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.compositionLocalOf
import java.lang.ref.WeakReference

val LocalActivity = compositionLocalOf<ComponentActivity> {
	error("CompositionLocal LocalActivity not present")
}

object AppContext {
    private var value: WeakReference<Context?>? = null
    fun set(context: Context) {
        value = WeakReference(context)
    }

    internal fun get(): Context {
        return value?.get() ?: throw RuntimeException("Context Error")
    }
}
