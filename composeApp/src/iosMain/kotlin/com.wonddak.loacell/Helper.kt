package com.wonddak.loacell

import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.util.FileUtil
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun initKoin() {
    startKoin{
        // Load modules
        modules(module {
            singleOf(::FileUtil)
        })
        modules(commonModule())
    }
}