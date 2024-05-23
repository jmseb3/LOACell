package com.wonddak.loacell.di

import com.wonddak.loacell.Config
import com.wonddak.loacell.DataStoreProvider
import com.wonddak.loacell.auth.LoginHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val platformModule = module {
    singleOf(::LoginHelper)
    singleOf(::DataStoreProvider)
    singleOf(::Config)
}

fun commonModule() = listOf(platformModule)

