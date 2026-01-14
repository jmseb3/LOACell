package com.wonddak.loacell

import android.app.Activity
import android.content.Context
import com.wonddak.hellogin.core.HelloginContainerProvider
import java.lang.ref.WeakReference


object AppContext {
    private var value: WeakReference<Context?>? = null
    fun set(context: Context) {
        value = WeakReference(context)
    }

    internal fun get(): Context {
        return value?.get() ?: throw RuntimeException("Context Error")
    }
}

fun initProvider(
    activity: Activity
) {
    HelloginContainerProvider.setContainer(activity)
    AppContext.set(activity)
}
