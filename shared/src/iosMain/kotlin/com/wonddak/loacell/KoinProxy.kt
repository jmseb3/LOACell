package com.wonddak.loacell

import androidx.annotation.MainThread
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.di.commonModule
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(commonModule())
    }
}

class ModuleProvider :KoinComponent {
    val database : AppDataBase by inject()
    val loginHelper : LoginHelper by inject()
    val config : Config by inject()

    @MainThread
    suspend fun getSheetSpace() {
        config.sheetSpace.first()
    }
}