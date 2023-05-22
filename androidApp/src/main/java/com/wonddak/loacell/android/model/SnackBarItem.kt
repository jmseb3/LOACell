package com.wonddak.loacell.android.model

import androidx.compose.material3.SnackbarDuration

data class SnackBarItem(
    val show: Boolean = false,
    val message: String = "",
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val performAction: () -> Unit = {}
)