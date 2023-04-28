package com.wonddak.loacell.android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
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
                            var show by remember {
                                mutableStateOf(false)
                            }
                            OutlinedButton(onClick = {
                                show = true
                            }) {
                                Text(text = "ADD")
                            }
                            RoomView(
                                roomList = roomList,
                                showRoomInfo = { roomId ->
                                    loaCellViewModel.showRoomInfo(roomId)
                                }
                            )
                            val context = LocalContext.current
                            if (show) {
                                BottomSheetDialog(
                                    onDismissRequest = { show = false },
                                    properties = BottomSheetDialogProperties(dismissWithAnimation = true),
                                ) {
                                    AddRoomSheet() {title, description ->
                                        if (title.isNotEmpty()) {
                                            db.roomInfoQueriesHelper.addRoomInfo(title, description)
                                            show = false
                                        } else {
                                            Toast.makeText(context,"title이 비어있습니다.",Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }
                            }
                        }
                    }
                    AnimatedVisibility(selectedRoomId != 0L) {
                        Column() {
                            val selectedRoom =
                                db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
                            val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(
                                selectedRoomId
                            ).collectAsState(initial = emptyList())
                            OutlinedButton(onClick = { loaCellViewModel.hideRoomInfo() }) {
                                Text(text = "BACK")
                            }
                            OutlinedButton(onClick = {
                                db.raidInfoQueriesHelper.addRaidInfo(
                                    selectedRoomId,
                                    "test",
                                    RaidType.VALTAN
                                )
                            }) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                content()
            }
        }
    }
}
