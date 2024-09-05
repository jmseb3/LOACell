package com.wonddak.loacell.ui.setting

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.wonddak.loacell.AppContext

actual fun getAppVersion(): String {
    val context = AppContext.get()
    val pm = context.packageManager
    val pi: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        pm.getPackageInfo(context.packageName, 0)
    }
    val name = pi.versionName
    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pi.longVersionCode
    } else {
        pi.versionCode
    }
    return "$name($code)"
}