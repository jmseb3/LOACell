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
import com.wonddak.loacell.android.ui.raid.RaidView
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private val loaCellViewModel: LoaCellViewModel by viewModels()
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
                    val roomList by db.roomInfoQueriesHelper.getALl()
                        .collectAsState(initial = emptyList())

                    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
                    Column(Modifier.fillMaxSize()) {
                        if (selectedRoomId == 0L) {
                            RaidView(
                                roomList = roomList,
                                addRoomAction = {
                                    db.roomInfoQueriesHelper.addRoomInfo("test", "123")
                                },
                                showRoomInfo = { roomId ->
                                    loaCellViewModel.showRoomInfo(roomId)
                                }
                            )
                        }
                        if (selectedRoomId != 0L) {
                            Column() {
                                Text(text = "SelectRoomId : $selectedRoomId")
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