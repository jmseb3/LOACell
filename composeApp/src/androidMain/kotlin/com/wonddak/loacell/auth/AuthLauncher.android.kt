package com.wonddak.loacell.auth

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.wonddak.loacell.AppContext
import kotlinx.coroutines.launch

@Composable
actual fun rememberAuthLauncher(
    loginHelper: LoginHelper,
    onSuccess: (result: FBAuthResult) -> Unit
): AuthLauncher {
    val scope = rememberCoroutineScope()
    val authLauncher: AuthLauncher = remember {
        AuthLauncher(
            googleLogin = {
                scope.launch {
                    loginHelper.requestGoogleLogin(AppContext.get() as Activity,onSuccess)
                }
            },
            appleLogin = {

            },
            anonymousLogin = {
                scope.launch {
                    loginHelper.requestAnonymousLogin()
                }
            }
        )
    }
    return authLauncher
}