package com.wonddak.loacell.android.di

import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {

}

val viewModule = module {
    viewModelOf(::LoaCellViewModel)
}