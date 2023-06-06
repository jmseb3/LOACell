package com.wonddak.loacell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.DriverFactory
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.SettingView
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.login.LoginView
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomActionDialog
import com.wonddak.loacell.android.ui.room.RoomEnterDialog
import com.wonddak.loacell.android.ui.room.RoomEnterErrorDialog
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.ui.theme.LoaCellTheme
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.android.viewModel.LoaCellViewModelFactory
import com.wonddak.loacell.ext.checkTimeOver
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.RoomHelper

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
    val userInfo by LoaCellApp.user.collectAsState()
    LoaCellTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        loaCellViewModel.apply {
            LaunchedEffect(snackBarMessage) {
                snackBarMessage.let {
                    // show가 true일때만 실행됩니다.
                    if (it.show) {
                        val snackBarResult = snackBarHostState.showSnackbar(
                            it.message,
                            it.actionLabel,
                            false,
                            it.duration
                        )
                        when (snackBarResult) {
                            SnackbarResult.Dismissed -> {
                                resetSnackBar()
                            }

                            SnackbarResult.ActionPerformed -> {
                                it.performAction()
                            }
                        }
                    }
                }
            }
        }
        if (userInfo == null) {
            LoginView(loaCellViewModel)
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                bottomBar = {
                    MyBottomAppBar(loaCellViewModel)
                },
                topBar = {
                    MyTopAppBar(loaCellViewModel = loaCellViewModel)
                }
            ) {

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val roomList by db.roomInfoQueriesHelper.getAll()
                            .collectAsState(initial = emptyList())

                        if (loaCellViewModel.showSetting) {
                            SettingView(loaCellViewModel)
                        } else {
                            Column(Modifier.fillMaxSize()) {
                                AnimatedVisibility(selectedRoomId.isEmpty()) {
                                    Column() {
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
                        loaCellViewModel.apply {
                            if (showRoomDialog) {
                                RoomActionDialog(
                                    confirm = { status ->
                                        when (status) {
                                            1 -> showRoomEnter = true
                                            2 -> showRoomAdd = true
                                        }
                                        showRoomDialog = false
                                    },
                                    dismiss = { showRoomDialog = false }
                                )
                            }
                            if (showRoomAdd) {
                                AddRoomSheet(
                                    onDismissRequest = { showRoomAdd = false }
                                ) { title, description, password ->
                                    val owner = userInfo!!.uid
                                    RoomHelper.makeInfo(
                                        title, description, password, owner
                                    ) { id ->
                                        db.roomInfoQueriesHelper.addRoomInfo(
                                            title,
                                            description,
                                            id,
                                            owner = owner
                                        )
                                    }
                                    showRoomAdd = false
                                }
                            }
                            if (showRoomEnter) {
                                RoomEnterDialog(
                                    nowEnterRoomList = roomList.map { it.uniqueId },
                                    success = { roomId ->
                                        RoomHelper.updateUser(
                                            roomId,
                                            userInfo!!.uid,
                                            userInfo!!.isAnonymous,
                                            successAction = {
                                                showRoomEnter = false
                                            },
                                            failAction = { error ->
                                                showSnackBar(error ?: "입장에 실패했습니다.")
                                                showRoomEnter = false
                                            }
                                        )
                                    },
                                    dismiss = {
                                        showRoomEnter = false
                                    }
                                )

                            }
                        }

                    }

                    if (loaCellViewModel.showRoomEnterError) {
                        RoomEnterErrorDialog(
                            confirm = {
                                db.roomInfoQueriesHelper.deleteRoomInfo(loaCellViewModel.roomId.value)
                                loaCellViewModel.hideRoomInfo()
                                loaCellViewModel.showRoomEnterError = false
                            },
                            dismiss = {
                                loaCellViewModel.showRoomEnterError = false
                            }
                        )
                    }
                    if (loaCellViewModel.syncData) {
                        LaunchedEffect(loaCellViewModel.syncData) {
                            CommonRoomHelper.syncInfo(
                                userInfo!!.uid,
                                db,
                                failAction = {e ->},
                                successAction = {
                                    loaCellViewModel.syncData = false
                                }
                            )
                        }
                        LoadingView("데이터를 동기화 중입니다.")
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
    val focusUserInfo by loaCellViewModel.userInfo.collectAsState()
    val focusRaidInfo by loaCellViewModel.raidInfo.collectAsState()

    loaCellViewModel.apply {
        BottomAppBar(
            floatingActionButton = {
                SmallFloatingActionButton(
                    content = {
                        if (focusUserInfo != null || focusRaidInfo != null) {
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
                    selectedRoomId.isNotEmpty() && (focusUserInfo == null) && (focusRaidInfo == null)
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
                    focusUserInfo != null
                ) {
                    focusUserInfo?.let {
                        Row() {
                            IconButton(onClick = { clearFocusItem() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                            MyIconButton(SharedRes.images.change_person) {
                                openCharacterEditDialog = true
                            }
                            MyIconButton(
                                SharedRes.images.refresh,
                                enabled = it.checkTimeOver(System.currentTimeMillis())
                            ) {
                                showLoading = true
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    focusRaidInfo != null
                ) {
                    Row() {
                        IconButton(onClick = { clearFocusItem() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
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
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState("")
    val focusUserName by loaCellViewModel.focusUserName.collectAsState("")
    TopAppBar(
        title = {
            if (loaCellViewModel.showSetting) {
                Text(text = "설정")
            } else if (focusUserName.isNotEmpty()) {
                Text(text = "${focusUserName}님 캐릭터 정보")
            } else if (focusRaidId.isNotEmpty()) {
                val raidInfo by loaCellViewModel.raidInfo.collectAsState()
                raidInfo?.let { raidInfo ->
                    Text(text = raidInfo.title)
                }
            } else if (selectedRoomId.isNotEmpty()) {
                val roomInfo by loaCellViewModel.roomInfo.collectAsState()
                roomInfo?.let { roomInfo ->
                    Text(text = roomInfo.title)
                }
            } else {
                Text(text = "LoaCell")
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