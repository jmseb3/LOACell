package com.wonddak.loacell.ui.login

import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.loacell.auth.LoginHelper
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInButton(
    loginHelper: LoginHelper,
    tokenResultHandler: TokenResultHandler<GoogleResult>,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val onClick: () -> Unit = {
        scope.launch {
            loginHelper.requestGoogleLogin(tokenResultHandler)
        }
        Unit
    }

    if (iconOnly) {
        IconButton(onClick = onClick, modifier = modifier) {
            Text("G")
        }
    } else {
        Button(onClick = onClick, modifier = modifier) {
            Text("Google로 로그인")
        }
    }
}
