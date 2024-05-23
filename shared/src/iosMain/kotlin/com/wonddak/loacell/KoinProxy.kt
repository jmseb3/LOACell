package com.wonddak.loacell

import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.di.commonModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(commonModule())
    }
}

class ModuleProvider :KoinComponent {
    private val loginHelper : LoginHelper by inject()
    private val config : Config by inject()
}