@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import platform.AuthenticationServices.ASAuthorizationAppleIDButton
import platform.AuthenticationServices.ASAuthorizationAppleIDButtonStyle
import platform.AuthenticationServices.ASAuthorizationAppleIDButtonTypeSignIn
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIAction
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.logo_apple
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
    if (iconOnly) {
        IconButton(onClick = onClick, modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RectangleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_apple),
                    contentDescription = "Apple 계정 연동",
                    modifier = Modifier.size(44.dp),
                )
            }
        }
        return
    }

    UIKitView(
        factory = {
            ASAuthorizationAppleIDButton(
                authorizationButtonType = ASAuthorizationAppleIDButtonTypeSignIn,
                authorizationButtonStyle = ASAuthorizationAppleIDButtonStyle.ASAuthorizationAppleIDButtonStyleBlack,
            ).apply {
                addAction(
                    action = UIAction.actionWithHandler { onClick() },
                    forControlEvents = UIControlEventTouchUpInside,
                )
            }
        },
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { contentDescription = "Apple로 로그인" },
    )
}
