package com.wonddak.loacell

import androidx.annotation.MainThread
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.di.commonModule
import com.wonddak.loacell.storage.SynergyReferenceHelper
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
    private val database : AppDataBase by inject()
    private val loginHelper : LoginHelper by inject()
    private val config : Config by inject()
    private val synergyReferenceHelper : SynergyReferenceHelper by inject()

    fun getAppDataBase() :AppDataBase = database
    fun getLoginHelper() : LoginHelper = loginHelper
    fun getConfig() : Config = config

    fun getSynergyReferenceHelper() : SynergyReferenceHelper =synergyReferenceHelper

    @MainThread
    suspend fun getSheetSpace() : Float {
        return config.sheetSpace.first()
    }
}