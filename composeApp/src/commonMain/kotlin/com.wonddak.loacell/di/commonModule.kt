package com.wonddak.loacell.di

import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.DataStoreProvider
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.SplashViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::FBApi)
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
    viewModel<SplashViewModel> {
        SplashViewModel(get(), get())
    }
    viewModel<AuthViewModel> {
        AuthViewModel(get())
    }
    viewModel<StoreViewModel> {
        StoreViewModel()
    }
    viewModel<RaidViewModel> {
        RaidViewModel()
    }
}

fun commonModule() = listOf(networkModule, platformModule, viewmodelModule, storageModule)

