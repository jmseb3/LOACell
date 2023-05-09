package com.wonddak.loacell.android

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.raid.RaidView
import com.wonddak.loacell.android.ui.raid.RoomView
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.util.NotificationUtil
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private val loaCellViewModel: LoaCellViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))
        startForegroundService(Intent(this,FireStoreUpdateService::class.java))

        setContent {
            MainContent() {

                val roomList by db.roomInfoQueriesHelper.getALl()
                    .collectAsState(initial = emptyList())

                val selectedRoomId by loaCellViewModel.roomId.collectAsState()

                Column(Modifier.fillMaxSize()) {
                    AnimatedVisibility(selectedRoomId.isEmpty()) {
                        Column() {
                            var show by remember {
                                mutableStateOf(false)
                            }
                            OutlinedButton(onClick = {
                                show = true
                            }) {
                                Text(text = "ADD")
                            }
                            OutlinedButton(onClick = {
                                db.roomInfoQueriesHelper.addRoomInfo(
                                    "123",
                                    "456",
                                    "kYdq2AE19rTMI8yDymdB"
                                )
                            }) {
                                Text(text = "ADD_TestRoom")
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
                                            FireStoreHelper.addRoomInfo(
                                                title, description
                                            ) { id ->
                                                db.roomInfoQueriesHelper.addRoomInfo(title, description, id)
                                            }
                                            show = false
                                        } else {
                                            Toast.makeText(context,"title이 비어있습니다.",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AnimatedVisibility(selectedRoomId.isNotEmpty()) {
                        if (selectedRoomId.isNotEmpty()) {
                            Column() {
                                RaidView(
                                    db,
                                    selectedRoomId
                                ) {
                                    loaCellViewModel.hideRoomInfo()
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
