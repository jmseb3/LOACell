package com.wonddak.loacell.di

import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.storage.SynergyReferenceHelper
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.DataStoreProvider
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.viewmodel.dsl.viewModel
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

val viewmodelModule = module {
    viewModel<AuthViewModel>{
        AuthViewModel(get())
    }
    viewModel<StoreViewModel> {
        StoreViewModel()
    }
}

fun commonModule() = listOf(platformModule, viewmodelModule, storageModule)

