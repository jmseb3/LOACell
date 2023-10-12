package com.wonddak.loacell.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.dialog.RoomActionDialog
import com.wonddak.loacell.android.ui.dialog.RoomEnterDialog
import com.wonddak.loacell.android.ui.dialog.RoomEnterErrorDialog
import com.wonddak.loacell.android.ui.dialog.RoomEnterPasswordDialog
import com.wonddak.loacell.android.ui.login.LoginView
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.ui.setting.SettingView
import com.wonddak.loacell.android.ui.theme.LoaCellTheme
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.initFBRoomInfo


@Composable
fun MainContent(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val userInfo by loaCellViewModel.user.collectAsState()
    val showRoomEnterPasswordByIntent by loaCellViewModel.showRoomEnterPasswordByIntent.collectAsState()
    val dialogStatus by loaCellViewModel.dialogStatus.collectAsState()
    LoaCellTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        loaCellViewModel.apply {
            LaunchedEffect(snackBarMessage) {
                snackBarMessage?.let {item ->
                    val snackBarResult = snackBarHostState.showSnackbar(
                        item.message,
                        item.actionLabel,
                        false,
                        item.duration
                    )
                    when (snackBarResult) {
                        SnackbarResult.Dismissed -> {
                            resetSnackBar()
                        }

                        SnackbarResult.ActionPerformed -> {
                            item.performAction()
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
                    BottomAppBar(loaCellViewModel)
                },
                topBar = {
                    TopAppBar(loaCellViewModel = loaCellViewModel)
                }
            ) {

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val roomList by loaCellViewModel.roomList.collectAsState()

                        if (loaCellViewModel.showSetting) {
                            SettingView(db, loaCellViewModel)
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
                            if (showRoomEnterPasswordByIntent != null) {
                                showRoomEnterPasswordByIntent?.let {
                                    RoomEnterPasswordDialog(
                                        roomId = it.first,
                                        fbRoomInfo = it.second,
                                        success = { id, roomInfo ->
                                            db.initFBRoomInfo(roomInfo, id)
                                            loaCellViewModel.clearEnterPasswordByIntent()
                                        }
                                    ) {
                                        loaCellViewModel.clearEnterPasswordByIntent()
                                    }
                                }

                            }
                            when (dialogStatus) {
                                DialogStatus.ROOM_ACTION -> {
                                    RoomActionDialog(
                                        confirm = { status ->
                                            when (status) {
                                                1 -> showDialog(DialogStatus.ROOM_ENTER)
                                                2 -> showDialog(DialogStatus.ROOM_ADD)
                                            }
                                        },
                                        dismiss = { hideDialog() }
                                    )
                                }
                                DialogStatus.ROOM_ADD -> {
                                    AddRoomSheet(
                                        onDismissRequest = {  hideDialog() }
                                    ) { title, description, password ->
                                        val owner = userInfo!!.uid
                                        CommonRoomHelper.makeInfo(
                                            title, description, password, owner
                                        ) { id ->
                                            db.roomInfoQueriesHelper.addRoomInfo(
                                                title,
                                                description,
                                                id,
                                                owner,
                                                password,
                                                emptyList(),
                                                emptyList(),
                                            )
                                        }
                                        hideDialog()
                                    }
                                }
                                DialogStatus.ROOM_ENTER -> {
                                    RoomEnterDialog(
                                        nowEnterRoomList = roomList.map { it.uniqueId },
                                        success = { roomId, roomInfo ->
                                            CommonRoomHelper.enterRoom(
                                                roomId,
                                                userInfo!!.uid,
                                                successAction = {
                                                    hideDialog()
                                                },
                                                failAction = { error ->
                                                    showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                                                    hideDialog()
                                                }
                                            )
                                            db.initFBRoomInfo(roomInfo, roomId)
                                        },
                                        dismiss = {
                                            hideDialog()
                                        }
                                    )

                                }
                                DialogStatus.ROOM_ENTER_ERROR -> {
                                    RoomEnterErrorDialog(
                                        confirm = {
                                            db.roomInfoQueriesHelper.deleteRoomInfo(roomId.value)
                                            hideRoomInfo()
                                            hideDialog()
                                        },
                                        dismiss = {
                                            hideDialog()
                                        }
                                    )
                                }
                                else -> {

                                }
                            }
                        }
                    }

                    val syncData by loaCellViewModel.syncData.collectAsState()
                    if (syncData) {
                        LoadingView("데이터를 동기화 중입니다.")
                    }
                }
            }
        }
    }
}
