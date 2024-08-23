package com.wonddak.loacell.ui

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberWebLauncher(): OpenWebLauncher {
    val context = LocalContext.current

    return OpenWebLauncher {
        val intent = CustomTabsIntent
            .Builder()
            .build()
        intent.launchUrl(context, Uri.parse(it))
    }
}

actual class OpenWebLauncher actual constructor(
    private val onLaunch: (url: String) -> Unit,
) {
    actual fun launchWeb(url: String) {
        onLaunch(url)
    }
}