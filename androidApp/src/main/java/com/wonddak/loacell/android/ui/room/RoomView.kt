package com.wonddak.loacell.android.ui.room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.RoomState
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddUserSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.dialog.RoomExitDialog
import com.wonddak.loacell.android.ui.room.raid.RaidView
import com.wonddak.loacell.android.ui.room.setting.SettingRoomView
import com.wonddak.loacell.android.ui.room.user.UserView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.store.CommonRoomHelper

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
        roomInfo?.let { roomInfo ->
            RoomTitleView(db, loaCellViewModel, roomInfo)
            when (loaCellViewModel.tabState) {
                RoomState.Raid -> {
                    RaidView(
                        db = db,
                        roomId = roomInfo.uniqueId,
                        loaCellViewModel = loaCellViewModel
                    )
                }

                RoomState.User -> {
                    UserView(
                        db = db,
                        roomId = roomInfo.uniqueId,
                        loaCellViewModel = loaCellViewModel
                    )
                }

                RoomState.Setting -> {
                    SettingRoomView(loaCellViewModel)
                }
            }

            loaCellViewModel.apply {
                if (showRaidAdd) {
                    val close = { showRaidAdd = false }
                    AddRaidSheet(
                        roomInfo.uniqueId,
                        onDismissRequest = close,
                        successAction = close
                    )
                }

                if (showUserAdd) {
                    val close = { showUserAdd = false }
                    AddUserSheet(
                        roomId = roomInfo.uniqueId,
                        onDismissRequest = close,
                        addAction = close
                    )
                }
            }
        }
    }
}

@Composable
fun RoomTitleView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel,
    roomInfo: RoomInfo
) {
    val focusUserName by loaCellViewModel.focusUserName.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()
    val user by LoaCellApp.user.collectAsState(null)
    val role by loaCellViewModel.myRole.collectAsState()

    val context = LocalContext.current

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
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText("Room Id", roomInfo.uniqueId)
                        clipboard.setPrimaryClip(clip)
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
                        loaCellViewModel.showRoomExit = true
                    }
                }
            }

            if (loaCellViewModel.showRoomExit && user != null) {
                RoomExitDialog(
                    success = {
                        CommonRoomHelper.exitRoom(
                            roomInfo.uniqueId,
                            user!!.uid,
                            role,
                            successAction = {
                                loaCellViewModel.hideRoomInfo()
                                db.roomInfoQueriesHelper.deleteRoomInfo(roomInfo.uniqueId)
                            },
                            failAction = {

                            }
                        )
                    },
                    dismiss = {
                        loaCellViewModel.showRoomExit = false
                    }
                )
            }
        }
    }

}
