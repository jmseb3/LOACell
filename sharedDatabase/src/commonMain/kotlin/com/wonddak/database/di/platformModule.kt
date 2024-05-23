package com.wonddak.database.di

import com.wonddak.database.AppDataBase
import com.wonddak.database.DriverFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataBaseModule = module {
    singleOf(::AppDataBase)
    singleOf(::DriverFactory)
}
