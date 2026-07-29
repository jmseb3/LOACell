package com.wonddak.loacell.ui.setting

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.core.content.pm.PackageInfoCompat
import com.wonddak.loacell.LocalActivity

@Composable
actual fun getAppVersion(): String {
    val context = LocalActivity.current
    val pm = context.packageManager
    val pi: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        pm.getPackageInfo(context.packageName, 0)
    }
    val name = pi.versionName
    val code = PackageInfoCompat.getLongVersionCode(pi)
    return "$name($code)"
}
