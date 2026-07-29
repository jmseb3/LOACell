package com.wonddak.loacell.ui.setting

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.pm.PackageInfoCompat
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getAppVersion(): String {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            val pm = context.packageManager
            val pi: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                pm.getPackageInfo(context.packageName, 0)
            }
            "${pi.versionName.orEmpty()}(${PackageInfoCompat.getLongVersionCode(pi)})"
        }.getOrDefault("")
    }
}
