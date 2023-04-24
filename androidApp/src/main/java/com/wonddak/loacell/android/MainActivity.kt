package com.wonddak.loacell.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wonddak.loacell.android.util.LoginHelper

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val user by LoaCellApp.user.collectAsState()
                    val googleLoginLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult()
                    ) { result ->
                        loginHelper.registerGoogleToken(result)
                    }

                    val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult()
                    ) { result ->
                        loginHelper.registerAnonymousToGoogle(result)
                    }

                    Column {
                        LoginView(
                            googleLoginAction = {
                                loginHelper.requestGoogleLogin { intent ->
                                    googleLoginLauncher.launch(intent)
                                }
                            },
                            anonymousLoginAction = {
                                loginHelper.requestAnonymousLogin()
                            }
                        )

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
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loginHelper.updateUserInfo()
    }
}