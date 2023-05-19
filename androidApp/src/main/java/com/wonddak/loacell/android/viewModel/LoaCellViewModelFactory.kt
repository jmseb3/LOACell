package com.wonddak.loacell.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoaCellViewModelFactory(private val db: com.wonddak.loacell.AppDataBase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoaCellViewModel::class.java)) {
            LoaCellViewModel(db) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}