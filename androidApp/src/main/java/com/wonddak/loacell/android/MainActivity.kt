package com.wonddak.loacell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wonddak.loacell.android.ui.raid.RaidView
import com.wonddak.loacell.android.ui.raid.RoomView
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.const.RaidType

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private val loaCellViewModel: LoaCellViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
        setContent {
            MainContent() {

                val roomList by db.roomInfoQueriesHelper.getALl()
                    .collectAsState(initial = emptyList())

                val selectedRoomId by loaCellViewModel.roomId.collectAsState()

                BackHandler(selectedRoomId != 0L) {
                    loaCellViewModel.hideRoomInfo()
                }
                Column(Modifier.fillMaxSize()) {
                    AnimatedVisibility(selectedRoomId == 0L) {
                        Column() {
                            OutlinedButton(onClick = { db.roomInfoQueriesHelper.addRoomInfo("test","test Room") }) {
                                Text(text = "ADD")
                            }
                            RoomView(
                                roomList = roomList,
                                showRoomInfo = { roomId ->
                                    loaCellViewModel.showRoomInfo(roomId)
                                }
                            )
                        }
                    }
                    AnimatedVisibility(selectedRoomId != 0L) {
                        Column() {
                            val selectedRoom =  db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
                            val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(selectedRoomId).collectAsState(initial = emptyList())
                            OutlinedButton(onClick = { loaCellViewModel.hideRoomInfo() }) {
                                Text(text = "BACK")
                            }
                            OutlinedButton(onClick = { db.raidInfoQueriesHelper.addRaidInfo(selectedRoomId,"test",RaidType.VALTAN) }) {
                                Text(text = "ADD")
                            }
                            RaidView(
                                roomInfo = selectedRoom,
                                raidInfoList = raidInfoList
                            )
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

@Composable
fun MainContent(
    content: @Composable () -> Unit
) {
    MyApplicationTheme() {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                content()
            }
        }
    }
}
