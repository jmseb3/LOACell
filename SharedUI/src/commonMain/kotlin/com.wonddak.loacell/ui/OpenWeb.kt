package com.wonddak.loacell.ui

import androidx.compose.runtime.Composable


@Composable
expect fun rememberWebLauncher(): OpenWebLauncher

expect class OpenWebLauncher(
    onLaunch: (url: String) -> Unit,
) {
    fun launchWeb(url: String)
}