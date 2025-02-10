package com.wonddak.loacell.di

import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.DataStoreProvider
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.SplashViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::FBApi)
    singleOf(::LostArkApi)
}

val platformModule = module {
    singleOf(::LoginHelper)
    singleOf(::DataStoreProvider)
    singleOf(::Config)
}

val storageModule = module {
    singleOf(::FileHelper)
}

val viewmodelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::RaidViewModel)
}

fun commonModule() = listOf(networkModule, platformModule, viewmodelModule, storageModule)

