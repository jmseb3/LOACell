package com.wonddak.loacell.android.ui.room

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.room.raid.RaidView
import com.wonddak.loacell.android.ui.room.setting.SettingRoomView
import com.wonddak.loacell.android.ui.room.user.UserView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.Sheet

@Composable
fun RoomView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo = loaCellViewModel.totalRoomInfoValue
    val tabState = totalRoomInfo.tabState
    val dialogStatus = totalRoomInfo.dialogState
    val roomInfo = totalRoomInfo.roomInfo
    val focusUserName = totalRoomInfo.focusUserName
    val focusRaidId = totalRoomInfo.focusRaidId

    val role = loaCellViewModel.myRole


    BackHandler(dialogStatus != Sheet.RAID_ADD && dialogStatus != Sheet.USER_ADD) {
        loaCellViewModel.hideRoomInfo()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(tabState != RoomState.Setting && focusRaidId.isEmpty() && focusUserName.isEmpty()) {
            roomInfo?.let {
                RoomTitleView(roomInfo,role) {
                    loaCellViewModel.showDialog(it)
                }
            }
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
