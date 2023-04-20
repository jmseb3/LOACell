package com.wonddak.loacell.android

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginView(
    googleLoginAction: () -> Unit = {},
    anonymousLoginAction: () -> Unit = {}
) {
    Column() {

        OutlinedButton(onClick = { googleLoginAction() }) {
            Text(text = "Google")
        }
        OutlinedButton(onClick = { anonymousLoginAction() }) {
            Text(text = "로그인 하지 않기")
        }

    }

}

@Composable
@Preview(
    showSystemUi = true,
    showBackground = true
)
fun LoginViewPreview() {
    LoginView()
}