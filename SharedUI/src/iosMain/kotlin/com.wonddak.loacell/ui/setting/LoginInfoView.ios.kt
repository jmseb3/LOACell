package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.hellogin.apple.AppleLoginButton
import com.wonddak.hellogin.apple.AppleLoginHelper
import com.wonddak.hellogin.apple.AppleResult
import com.wonddak.hellogin.core.ButtonTheme
import com.wonddak.hellogin.core.ButtonType
import com.wonddak.hellogin.core.Error
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.auth.registerAnonymousToApple
import com.wonddak.loacell.auth.registerAppleToken
import com.wonddak.loacell.auth.revokeAppleUser
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal actual val useLinkApple: Boolean = true

@Composable
actual fun AppleLoginView(
    loginHelper: LoginHelper
) {
    val appleLoginHandler = rememberSaveable {
        object : TokenResultHandler<AppleResult> {
            override fun onSuccess(token: AppleResult) {
                Napier.d(tag = "auth") { "success with $token" }
                loginHelper.registerAppleToken(
                    token.nonce,
                    token.cred
                )
            }

            override fun onFail(error: Error?) {
                Napier.d(tag = "auth") { "fail with $error" }
            }
        }
    }
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        AppleLoginButton(
            tokenResultHandler = appleLoginHandler,
            modifier = Modifier.fillMaxWidth(0.8f),
            mode = ButtonTheme.Dark
        )
    }
}

@Composable
actual fun AppleLoginBtn(
    loginHelper: LoginHelper,
    onSuccess: () -> Unit,
    onFail: (msg: String) -> Unit
) {
    val appleLoginHandler = remember {
        object : TokenResultHandler<AppleResult> {
            override fun onSuccess(token: AppleResult) {
                Napier.d(tag = "auth") { "success with $token" }
                loginHelper.registerAnonymousToApple(
                    nonce = token.nonce,
                    credential = token.cred,
                    onSuccess = onSuccess,
                    onFail = onFail
                )
            }

            override fun onFail(error: Error?) {
                Napier.d(tag = "auth") { "fail with $error" }
                onFail(error.toString())
            }
        }
    }
    AppleLoginButton(
        tokenResultHandler = appleLoginHandler,
        type = ButtonType.IconOnly
    )
}

actual fun revokeApple(
    loginHelper: LoginHelper,
    scope: CoroutineScope,
    onSuccess: () -> Unit
) {
    val appleLoginHandler = object : TokenResultHandler<AppleResult> {
        override fun onSuccess(token: AppleResult) {
            Napier.d(tag = "auth") { "success with $token" }
            loginHelper.revokeAppleUser(
                credential = token.cred,
                onSuccess = onSuccess,
            )
        }

        override fun onFail(error: Error?) {
            Napier.d(tag = "auth") { "fail with $error" }
        }
    }
    scope.launch {
        AppleLoginHelper.requestLogin(appleLoginHandler)
    }
}