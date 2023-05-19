package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun LoginInfoView(
    loaCellViewModel: LoaCellViewModel
) {
    val context = LocalContext.current
    val loginHelper = LoginHelper(context)
    val user by LoaCellApp.user.collectAsState(null)
    AnimatedVisibility(visible = user != null) {
        Column() {
            Text(text = user?.email.toString())
            Text(text = user?.uid.toString())
            if (user?.isAnonymous == true) {
                val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    loginHelper.registerAnonymousToGoogle(result)
                }
                OutlinedButton(onClick = {
                    loginHelper.requestGoogleLogin {
                        anonymousToGoogleLoginLauncher.launch(it)
                    }
                }) {
                    Text(text = "Google과 연동")
                }
            }
            OutlinedButton(
                onClick = {
                    loginHelper.signOut()
                    loaCellViewModel.clearAllStatus()
                }
            ) {
                Text(text = "LogOut")
            }
        }
    }
}