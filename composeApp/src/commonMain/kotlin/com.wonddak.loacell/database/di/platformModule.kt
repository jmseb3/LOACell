package com.wonddak.loacell.database.di

import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataBaseModule = module {
    singleOf(::AppDataBase)
    singleOf(::DriverFactory)
}
