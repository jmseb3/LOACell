package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseUser
import com.wonddak.loacell.android.util.LoginHelper

@Composable
fun LoginInfoView(
    user : FirebaseUser?,
    loginHelper: LoginHelper
) {
    val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loginHelper.registerAnonymousToGoogle(result)
    }
    AnimatedVisibility(visible = user!= null) {
        Column() {
            Text(text = user?.email.toString())
            Text(text = user?.uid.toString())
            if (user?.isAnonymous == true) {
                OutlinedButton(onClick = {
                    loginHelper.requestGoogleLogin {
                        anonymousToGoogleLoginLauncher.launch(it)
                    }
                }) {
                    Text(text = "Google과 연동")
                }
            }
            OutlinedButton(onClick = { loginHelper.signOut() }) {
                Text(text = "LogOut")
            }
        }
    }
}