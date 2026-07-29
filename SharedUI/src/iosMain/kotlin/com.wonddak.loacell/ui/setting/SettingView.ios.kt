package com.wonddak.loacell.ui.setting

import platform.Foundation.NSBundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
actual fun getAppVersion(): String {
    return remember {
        val info = NSBundle.mainBundle.infoDictionary
        val name = info?.get("CFBundleShortVersionString") as? String ?: ""
        val code = info?.get("CFBundleVersion") as? String ?: ""
        "$name($code)"
    }
}
