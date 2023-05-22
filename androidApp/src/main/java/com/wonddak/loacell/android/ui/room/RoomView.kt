package com.wonddak.loacell.android.ui.room

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddUserSheet
import com.wonddak.loacell.android.ui.room.raid.RaidView
import com.wonddak.loacell.android.ui.room.user.UserView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun RoomView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val roomInfo by loaCellViewModel.roomInfo.collectAsState()
    BackHandler(!loaCellViewModel.showRaidAdd && !loaCellViewModel.showUserAdd) {
        loaCellViewModel.hideRoomInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        roomInfo?.let {roomInfo ->
            RoomTitleView(loaCellViewModel = loaCellViewModel, roomInfo = roomInfo)
            when (loaCellViewModel.tabState) {
                0 -> {
                    RaidView(
                        db = db,
                        roomId = roomInfo.uniqueId,
                        loaCellViewModel = loaCellViewModel
                    )
                }

                1 -> {
                    UserView(
                        db = db,
                        roomId = roomInfo.uniqueId,
                        loaCellViewModel = loaCellViewModel
                    )
                }
            }

            if (loaCellViewModel.showRaidAdd) {
                AddRaidSheet(
                    roomInfo.uniqueId,
                    onDismissRequest = {
                        loaCellViewModel.hideRaidDialog()
                    }
                ) {
                    loaCellViewModel.hideRaidDialog()
                }
            }

            if (loaCellViewModel.showUserAdd) {
                AddUserSheet(
                    roomId = roomInfo.uniqueId,
                    onDismissRequest = {
                        loaCellViewModel.hideUserDialog()
                    }
                ) {
                    loaCellViewModel.hideUserDialog()
                }
            }
        }
    }
}

@Composable
fun RoomTitleView(
    loaCellViewModel: LoaCellViewModel,
    roomInfo: RoomInfo
) {
    val focusUserName by loaCellViewModel.focusUserName.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

    AnimatedVisibility(focusRaidId.isEmpty() && focusUserName.isEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = roomInfo.description,
                modifier = Modifier
            )
            Text(
                text = roomInfo.uniqueId,
                modifier = Modifier
            )
            Divider()
        }
    }
}
