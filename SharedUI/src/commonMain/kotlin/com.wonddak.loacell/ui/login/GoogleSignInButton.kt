package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.button_google
import loacell.sharedui.generated.resources.logo_google
import org.jetbrains.compose.resources.painterResource

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
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
