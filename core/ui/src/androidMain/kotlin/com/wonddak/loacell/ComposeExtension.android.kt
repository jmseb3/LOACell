package com.wonddak.loacell

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat.finishAffinity

@Composable actual fun SetBackAction(enabled: Boolean, action: () -> Unit) { BackHandler(enabled, action) }
private var backPressedTime = 0L
@Composable actual fun SetTwiceClose() {
    val activity = LocalActivity.current
    BackHandler {
        if (System.currentTimeMillis() > backPressedTime + 2_000L) { backPressedTime = System.currentTimeMillis(); Toast.makeText(activity, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show() } else finishAffinity(activity)
    }
}
