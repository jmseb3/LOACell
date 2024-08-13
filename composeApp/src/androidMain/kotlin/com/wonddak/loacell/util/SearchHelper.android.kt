package com.wonddak.loacell.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Context.openName(base:String, name: String) {
    val url = base + name
    val intent = CustomTabsIntent
        .Builder()
        .build()
    intent.launchUrl(this, Uri.parse(url))
}