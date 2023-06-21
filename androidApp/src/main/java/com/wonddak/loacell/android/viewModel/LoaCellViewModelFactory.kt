package com.wonddak.loacell.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.Config

class LoaCellViewModelFactory(
    private val db: AppDataBase,
    private val config :Config
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoaCellViewModel::class.java)) {
            LoaCellViewModel(db,config) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}