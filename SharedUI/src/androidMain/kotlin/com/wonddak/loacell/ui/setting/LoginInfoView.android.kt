package com.wonddak.loacell.ui.setting

import androidx.compose.runtime.Composable
import com.wonddak.loacell.auth.LoginHelper
import kotlinx.coroutines.CoroutineScope

internal actual val useLinkApple: Boolean = false

@Composable
actual fun AppleLoginView(
    loginHelper: LoginHelper
) {

}

@Composable
actual fun AppleLoginBtn(
    loginHelper: LoginHelper,
    onSuccess: () -> Unit,
    onFail: (msg: String) -> Unit
) {

}

actual fun revokeApple(
    loginHelper: LoginHelper,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
) {

}