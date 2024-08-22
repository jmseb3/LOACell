package com.wonddak.loacell

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BlockBackButton() {
    BackHandler {

    }
}

@Composable
actual fun SetBackAction(action: () -> Unit) {
    BackHandler {
        action()
    }
}