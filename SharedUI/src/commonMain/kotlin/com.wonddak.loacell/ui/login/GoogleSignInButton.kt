package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import loacell.sharedui.generated.resources.Res
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
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .heightIn(min = 48.dp),
            border = BorderStroke(1.dp, Color(0xFF747775)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF1F1F1F),
            ),
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Google로 로그인")
        }
    }
}
