package com.wonddak.loacell.android.di

import com.wonddak.database.AppDataBase
import com.wonddak.database.DriverFactory
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    singleOf(::DriverFactory)
    singleOf(::AppDataBase)
}

val viewModule = module {
    viewModelOf(::LoaCellViewModel)
}