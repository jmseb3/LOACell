package com.wonddak.loacell.ui

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberWebLauncher(): OpenWebLauncher {
    return OpenWebLauncher {
        val safariViewController = SFSafariViewController(uRL = NSURL(string = it))
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            safariViewController,
            animated = true,
            null
        )
    }
}

actual class OpenWebLauncher actual constructor(
    private val onLaunch: (url: String) -> Unit,
) {
    actual fun launchWeb(url: String) {
        onLaunch(url)
    }
}