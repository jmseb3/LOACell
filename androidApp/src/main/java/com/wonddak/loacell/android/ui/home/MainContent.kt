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
import com.wonddak.loacell.android.ui.login.LoginView
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.ui.setting.SettingView
import com.wonddak.loacell.android.ui.theme.LoaCellTheme
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.store.CommonRoomHelper


@Composable
fun MainContent(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val userInfo by loaCellViewModel.user.collectAsState()
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
                        val roomList by db.roomInfoQueriesHelper.getAll()
                            .collectAsState(initial = emptyList())

                        if (loaCellViewModel.showSetting) {
                            SettingView(db,loaCellViewModel)
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
                            if (showRoomAction) {
                                RoomActionDialog(
                                    confirm = { status ->
                                        when (status) {
                                            1 -> showRoomEnter = true
                                            2 -> showRoomAdd = true
                                        }
                                        showRoomAction = false
                                    },
                                    dismiss = { showRoomAction = false }
                                )
                            }
                            if (showRoomAdd) {
                                AddRoomSheet(
                                    onDismissRequest = { showRoomAdd = false }
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
                                            emptyList(),
                                            emptyList(),
                                            emptyList()
                                        )
                                    }
                                    showRoomAdd = false
                                }
                            }
                            if (showRoomEnter) {
                                RoomEnterDialog(
                                    nowEnterRoomList = roomList.map { it.uniqueId },
                                    success = { roomId, roomInfo ->
                                        CommonRoomHelper.enterRoom(
                                            roomId,
                                            userInfo!!.uid,
                                            userInfo!!.isAnonymous,
                                            successAction = {
                                                showRoomEnter = false
                                            },
                                            failAction = { error ->
                                                showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                                                showRoomEnter = false
                                            }
                                        )
                                        db.roomInfoQueriesHelper.addRoomInfo(
                                            roomInfo.title,
                                            roomInfo.description,
                                            roomId,
                                            roomInfo.owner,
                                            roomInfo.enterUser,
                                            roomInfo.editableUser,
                                            roomInfo.anonymousUser
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
