package com.wonddak.loacell.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.dialog.DialogHost
import com.wonddak.loacell.android.ui.dialog.RoomEnterPasswordDialog
import com.wonddak.loacell.android.ui.login.LoginView
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.ui.setting.SettingView
import com.wonddak.loacell.android.ui.theme.LoaCellTheme
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.store.initFBRoomInfo


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val userInfo by loaCellViewModel.user.collectAsState()
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val dialogStatus = totalRoomInfo.dialogState
    LoaCellTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        loaCellViewModel.apply {
            LaunchedEffect(snackBarMessage) {
                snackBarMessage?.let { item ->
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
                DialogHost(dialogStatus, loaCellViewModel.getDialogAction()) {
                    MainContentView(db, loaCellViewModel)
                }
            }
        }
    }
}

@Composable
private fun MainContentView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val showRoomEnterPasswordByIntent by loaCellViewModel.showRoomEnterPasswordByIntent.collectAsState()
    val syncData by loaCellViewModel.syncData.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                                RoomView(loaCellViewModel)
                            }
                        }
                    }
                }
            }

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
        }

        if (syncData) {
            LoadingView("데이터를 동기화 중입니다.")
        }
    }

}
