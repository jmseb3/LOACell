package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.loacell.auth.LoginHelper
import kotlinx.coroutines.launch
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.button_google
import loacell.sharedui.generated.resources.logo_google
import org.jetbrains.compose.resources.painterResource

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
    }

    if (iconOnly) {
        IconButton(onClick = onClick, modifier = modifier) {
            Image(
                painter = painterResource(Res.drawable.logo_google),
                contentDescription = "Google login",
                modifier = Modifier.size(40.dp),
            )
        }
    } else {
        Image(
            painter = painterResource(Res.drawable.button_google),
            contentDescription = "Google login",
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .aspectRatio(352f / 63f)
                .clickable(onClick = onClick),
        )
    }
}
