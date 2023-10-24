package com.wonddak.loacell.android.ui.room

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.room.raid.RaidView
import com.wonddak.loacell.android.ui.room.setting.SettingRoomView
import com.wonddak.loacell.android.ui.room.user.UserView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState

@Composable
fun RoomView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val tabState = totalRoomInfo.tabState
    val dialogStatus = totalRoomInfo.dialogState

    BackHandler(dialogStatus != DialogStatus.RAID_ADD && dialogStatus != DialogStatus.USER_ADD) {
        loaCellViewModel.hideRoomInfo()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(tabState != RoomState.Setting) {
            RoomTitleView(loaCellViewModel)
        }
        Spacer(modifier = Modifier.height(10.dp))
        when (tabState) {
            RoomState.Raid -> {
                RaidView(loaCellViewModel)
            }

            RoomState.User -> {
                UserView(loaCellViewModel)
            }

            RoomState.Setting -> {
                SettingRoomView(loaCellViewModel)
            }
        }
    }
}

@Composable
fun RoomTitleView(
    loaCellViewModel: LoaCellViewModel,
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val roomInfo = totalRoomInfo.roomInfo
    val focusUserName = totalRoomInfo.focusUserName
    val focusRaidId = totalRoomInfo.focusRaidId

    val role = loaCellViewModel.myRole

    roomInfo?.let {
        AnimatedVisibility(focusRaidId.isEmpty() && focusUserName.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = roomInfo.description,
                        modifier = Modifier
                    )
                    Text(
                        text = roomInfo.uniqueId,
                        modifier = Modifier.noRippleClickable {
                            loaCellViewModel.showDialog(DialogStatus.SHARE_SHEET)
                        },
                    )
                    Divider()
                }

                when (role) {
                    RoomRole.OWNER -> {

                    }

                    RoomRole.NONE -> {

                    }

                    else -> {
                        MyIconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            imageResource = SharedRes.images.room_exit
                        ) {
                            loaCellViewModel.showDialog(DialogStatus.ROOM_EXIT)
                        }
                    }
                }
            }
        }
    }
}
