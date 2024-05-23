package com.wonddak.loacell.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.login.LoginView
import com.wonddak.loacell.android.ui.modal.ModalHost
import com.wonddak.loacell.android.ui.room.RooListView
import com.wonddak.loacell.android.ui.room.RoomView
import com.wonddak.loacell.android.ui.setting.SettingView
import com.wonddak.loacell.android.ui.theme.LoaCellTheme
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.storage.CommonFireStorageHelper
import org.koin.compose.koinInject


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent(
    loaCellViewModel: LoaCellViewModel = koinInject()
) {
    val user = loaCellViewModel.user
    val totalRoomInfo = loaCellViewModel.totalRoomInfoValue
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
        if (user == null) {
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
                ModalHost(dialogStatus, loaCellViewModel.getDialogAction()) {
                    MainContentView(it,loaCellViewModel)
                }
            }
        }
    }
}

@Composable
private fun MainContentView(
    padding: PaddingValues,
    loaCellViewModel: LoaCellViewModel = koinInject()
) {
    val selectedRoomId = loaCellViewModel.roomId
    val syncData = loaCellViewModel.syncData

    LaunchedEffect(true) {
        CommonFireStorageHelper.parseSynergyJson()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(padding)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (loaCellViewModel.showSetting) {
                SettingView(loaCellViewModel)
            } else {
                Column(Modifier.fillMaxSize()) {
                    AnimatedVisibility(selectedRoomId.isEmpty()) {
                        Column() {
                            RooListView(
                                roomList = loaCellViewModel.roomList,
                                showRoomInfo = { roomId ->
                                    loaCellViewModel.showRoomInfo(roomId)
                                }
                            )
                        }
                    }
                    AnimatedVisibility(selectedRoomId.isNotEmpty()) {
                        if (selectedRoomId.isNotEmpty()) {
                            RoomView(loaCellViewModel)
                        }
                    }
                }
            }
        }

        if (syncData) {
            LoadingView("데이터를 동기화 중입니다.")
        }
    }

}
