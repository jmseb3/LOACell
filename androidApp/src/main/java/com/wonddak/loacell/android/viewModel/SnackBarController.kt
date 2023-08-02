package com.wonddak.loacell.android.viewModel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wonddak.loacell.android.model.SnackBarItem

class SnackBarController {
    var snackBarMessage: SnackBarItem? by mutableStateOf(null)
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
        snackBarMessage = null
    }
}