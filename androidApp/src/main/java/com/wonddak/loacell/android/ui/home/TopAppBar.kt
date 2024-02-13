package com.wonddak.loacell.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wonddak.loacell.android.viewModel.LoaCellViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    loaCellViewModel: LoaCellViewModel,
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()

    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val roomInfo = totalRoomInfo.roomInfo
    val raidInfo = totalRoomInfo.raidInfo
    val focusUserName = totalRoomInfo.focusUserName
    TopAppBar(
        title = {
            if (loaCellViewModel.showSetting) {
                Text(text = "설정")
            } else if (focusUserName.isNotEmpty()) {
                Text(text = "${focusUserName}님 캐릭터 정보")
            } else {
                Text(text = raidInfo?.title ?: roomInfo?.title ?: "LoaCell")
            }
        },
        actions = {
            AnimatedVisibility(selectedRoomId.isEmpty()) {
                Row() {
                    IconButton(
                        onClick = { loaCellViewModel.showSetting = true },
                        enabled = !loaCellViewModel.showSetting
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                    }
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