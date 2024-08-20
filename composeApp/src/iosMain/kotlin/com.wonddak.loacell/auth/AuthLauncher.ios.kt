package com.wonddak.loacell.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
                    loginHelper.requestGoogleLogin(onSuccess)
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