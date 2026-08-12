package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val buttonContainerColor = Color.Black
    val buttonContentColor = Color.White
    val buttonBorderColor = Color.Black

    if (iconOnly) {
        IconButton(onClick = onClick, modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(buttonContainerColor)
                    .border(1.dp, buttonBorderColor, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_google),
                    contentDescription = "Google 계정 연동",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .widthIn(min = 180.dp, max = 180.dp)
                .heightIn(min = 48.dp),
            border = BorderStroke(1.dp, buttonBorderColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = buttonContainerColor,
                contentColor = buttonContentColor,
            ),
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Google로 로그인")
        }
    }
}
