package com.wonddak.loacell.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.SettingView
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.android.viewModel.LoaCellViewModelFactory
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.DriverFactory

class MainActivity : ComponentActivity() {
    private lateinit var loginHelper: LoginHelper
    private lateinit var loaCellViewModel: LoaCellViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHelper = LoginHelper(this)
        val db = AppDataBase(DriverFactory(this))

        loaCellViewModel = ViewModelProvider(
            this,
            LoaCellViewModelFactory(db)
        )[LoaCellViewModel::class.java]

        setContent {
            MainContent(db, loaCellViewModel)
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
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    MyApplicationTheme() {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = Color.White,
            bottomBar = {
                MyBottomAppBar(loaCellViewModel)
            },
            topBar = {
                MyTopAppBar(loaCellViewModel = loaCellViewModel)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                val roomList by db.roomInfoQueriesHelper.getALl()
                    .collectAsState(initial = emptyList())

                if (loaCellViewModel.showSetting) {
                    SettingView(loaCellViewModel)
                } else {
                    Column(Modifier.fillMaxSize()) {
                        AnimatedVisibility(selectedRoomId.isEmpty()) {
                            Column() {
                                if (BuildConfig.DEBUG) {
                                    val roomInfos by db.roomInfoQueriesHelper.getALl()
                                        .collectAsState(
                                            initial = emptyList()
                                        )
                                    val testId = "testRoom"
                                    if (!roomInfos.map { it.uniqueId }.contains(testId)) {
                                        OutlinedButton(onClick = {
                                            db.roomInfoQueriesHelper.addRoomInfo(
                                                "",
                                                "",
                                                "testRoom"
                                            )
                                        }) {
                                            Text(text = "ADD TestRoom")
                                        }
                                    }
                                }
                                RooListView(
                                    roomList = roomList,
                                    showRoomInfo = { roomId ->
                                        loaCellViewModel.showRoomInfo(roomId)
                                    }
                                )
                            }
                        }
                        AnimatedVisibility(selectedRoomId.isNotEmpty()) {
                            if (selectedRoomId.isNotEmpty()) {
                                Column() {
                                    RoomView(
                                        db,
                                        loaCellViewModel
                                    )
                                }
                            }
                        }
                    }
                }
                val context = LocalContext.current
                if (loaCellViewModel.showRoomAdd) {
                    BottomSheetDialog(
                        onDismissRequest = { loaCellViewModel.showRoomAdd = false },
                        properties = BottomSheetDialogProperties(dismissWithAnimation = true),
                    ) {
                        AddRoomSheet() { title, description ->
                            if (title.isNotEmpty()) {
                                FireStoreHelper.addRoomInfo(
                                    title, description
                                ) { id ->
                                    db.roomInfoQueriesHelper.addRoomInfo(
                                        title,
                                        description,
                                        id
                                    )
                                }
                                loaCellViewModel.showRoomAdd = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "title이 비어있습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBottomAppBar(
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val focusUserName by loaCellViewModel.focusUserName.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

    loaCellViewModel.apply {
        BottomAppBar(
            floatingActionButton = {
                SmallFloatingActionButton(
                    content = {
                        if (focusUserName.isNotEmpty()) {
                            Icon(
                                painter = painterResource(id = SharedRes.images.delete.drawableResId),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(Icons.Filled.Add, null)
                        }
                    },
                    onClick = {
                        bottomAddAction()
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(2.dp),
                )
            },
            actions = {
                val showLevel1 =
                    selectedRoomId.isNotEmpty() && (focusUserName.isEmpty()) && (focusRaidId.isEmpty())
                val showLevel2User = (focusUserName.isNotEmpty())
                val showLevel2Raid = (focusRaidId.isNotEmpty())
                AnimatedVisibility(
                    showLevel1,
                ) {
                    Row() {
                        MyIconButton(
                            imageResource = SharedRes.images.room,
                            enabled = tabState != 0
                        ) { loaCellViewModel.setTabStatus(0) }
                        MyIconButton(
                            imageResource = SharedRes.images.person,
                            enabled = tabState != 1
                        ) { loaCellViewModel.setTabStatus(1) }
                    }

                }
                AnimatedVisibility(
                    showLevel2User,
                ) {
                    Row() {
                        IconButton(onClick = { clearFocusItem() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                        MyIconButton(SharedRes.images.change_person) {
                            openCharacterEditDialog = true
                        }
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    loaCellViewModel: LoaCellViewModel,
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    TopAppBar(
        title = {
            if (loaCellViewModel.showSetting) {
                Text(text = "설정")
            } else if (selectedRoomId.isNotEmpty()) {
                val roomInfo by loaCellViewModel.roomInfo.collectAsState()
                roomInfo?.let { roomInfo ->
                    Text(text = roomInfo.title)
                }
            }
        },
        actions = {
            Row() {
                IconButton(
                    onClick = { loaCellViewModel.showSetting = true },
                    enabled = !loaCellViewModel.showSetting
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                }
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                selectedRoomId.isNotEmpty() || loaCellViewModel.showSetting,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        loaCellViewModel.topBackAction()
                    },
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}