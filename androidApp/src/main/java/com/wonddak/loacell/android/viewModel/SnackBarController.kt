package com.wonddak.loacell.android.viewModel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.android.model.SnackBarItem

open class SnackBarController(

) : ViewModel() {
    var snackBarMessage: SnackBarItem by mutableStateOf(SnackBarItem())
        private set

    private fun showSnackBar(snackBarItem: SnackBarItem) {
        snackBarMessage = snackBarItem
    }


    fun showSnackBar(
        message: String,
        label: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = {resetSnackBar()},
    ) {
        showSnackBar(SnackBarItem(true, message, label, duration, action))
    }

    fun resetSnackBar() {
        snackBarMessage = SnackBarItem()
    }
}