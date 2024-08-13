package com.wonddak.loacell.di

import com.wonddak.loacell.database.di.dataBaseModule
import com.wonddak.loacell.Config
import com.wonddak.loacell.DataStoreProvider
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.storage.SynergyReferenceHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val platformModule = module {
    singleOf(::LoginHelper)
    singleOf(::DataStoreProvider)
    singleOf(::Config)
}

val storageModule = module {
    singleOf(::SynergyReferenceHelper)
}

fun commonModule() = listOf(platformModule,dataBaseModule, storageModule)

