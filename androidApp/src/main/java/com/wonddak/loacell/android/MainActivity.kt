package com.wonddak.loacell.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import com.wonddak.loacell.android.util.LoginHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
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
                    val items by db.getRoomInfo().collectAsState(initial = emptyList())

                    val scope = rememberCoroutineScope()
                    Column {
                        OutlinedButton(onClick = { 
                            scope.launch { 
                                db.addRoomInfo("test")
                            }
                        }) {
                            Text(text = "ADD")
                        }
                        items.forEach { roominfo ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${roominfo.id}-${roominfo.title}")
                                OutlinedButton(onClick = {
                                    db.addRaidInfo(roominfo.id,"testRaid")
                                }) {
                                    Text(text = "ADD")
                                }
                                OutlinedButton(onClick = {
                                    db.deleteRoomInfo(roominfo.id)
                                }) {
                                    Text(text = "Delete")
                                }
                            }
                            val raidInfo by db.getRaidInfoFromRoomId(roominfo.id).collectAsState(initial = emptyList())
                            raidInfo.forEach {
                                Text(text = "\t${it.id}-${it.title}-${it.type?.name}(${it.type?.maxPerson})")
                            }
                            Divider()
                        }
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