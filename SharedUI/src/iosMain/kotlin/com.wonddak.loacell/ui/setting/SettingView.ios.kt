package com.wonddak.loacell.ui.setting

import platform.Foundation.NSBundle
import androidx.compose.runtime.Composable


@Composable
actual fun getAppVersion(): String {
    val name = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString").toString()
    val code = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion").toString()
    return "$name($code)"
}
