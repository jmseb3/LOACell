package com.wonddak.loacell.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.raid.RaidView
import com.wonddak.loacell.android.ui.raid.RoomView
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory

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
                                    "hxQlFNqieMviWhQPp4HL"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    content: @Composable () -> Unit
) {
    MyApplicationTheme() {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = Color.White
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
