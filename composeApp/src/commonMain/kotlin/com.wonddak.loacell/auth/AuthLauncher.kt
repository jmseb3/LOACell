package com.wonddak.loacell.auth

import androidx.compose.runtime.Composable

class AuthLauncher(
    private val googleLogin: () -> Unit,
    private val appleLogin: () -> Unit,
    private val anonymousLogin: () -> Unit
) {

    fun launchGoogleLogin() {
        googleLogin()
    }

    fun launchAppleLogin() {
        appleLogin()
    }

    fun launchAnonymousLogin() {
        anonymousLogin()
    }
}

@Composable
expect fun rememberAuthLauncher(
    loginHelper: LoginHelper,
    onSuccess: (result: FBAuthResult) -> Unit
): AuthLauncher