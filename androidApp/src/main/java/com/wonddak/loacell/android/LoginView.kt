package com.wonddak.loacell.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginView(
    googleLoginAction: () -> Unit = {},
    anonymousLoginAction: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        val modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
        LoginButton(
            modifier,
            { googleLoginAction() }
        ) {
            Row(
                modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = "SIGN IN WITH GOOGLE")
            }
        }
        LoginButton(
            modifier,
            { anonymousLoginAction() }
        ) {
            Text(text = "로그인 하지 않기")
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun LoginViewPreview() {
    LoginView()
}

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    action: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Button(
        onClick = { action() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        modifier = modifier,
        shape = RoundedCornerShape(5.dp)
    ) {
        content()
    }

}
