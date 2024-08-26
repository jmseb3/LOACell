package com.wonddak.loacell

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SetBackAction(enabled: Boolean, action: () -> Unit) {
    BackHandler(enabled) {
        action()
    }
}