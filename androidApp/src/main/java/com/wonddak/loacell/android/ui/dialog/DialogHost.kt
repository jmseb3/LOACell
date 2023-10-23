package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRoomSheet
import com.wonddak.loacell.android.ui.bottomSheet.ShareSheet
import com.wonddak.loacell.model.DialogStatus


@Composable
fun DialogHost(
    dialogStatus: DialogStatus,
    dialogAction: DialogAction,
    content: @Composable () -> Unit
) {
    val dismiss = { dialogAction.hideDialog() }
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        when (dialogStatus) {
            DialogStatus.NONE -> {}
            DialogStatus.ROOM_ACTION -> {
                RoomActionDialog(
                    dismiss = dismiss
                ) { status ->
                    dialogAction.dialogRoomAction(status)
                }
            }

            DialogStatus.ROOM_ADD -> {
                AddRoomSheet(
                    onDismissRequest = dismiss
                ) { title, description, password ->
                    dialogAction.dialogRoomAdd(title, description, password)
                }
            }

            DialogStatus.ROOM_ENTER -> {
                RoomEnterDialog(
                    nowEnterRoomList = dialogAction.getRoomListToUniqueId(),
                    dismiss = dismiss
                ) { roomId, roomInfo ->
                    dialogAction.dialogRoomEnter(roomId, roomInfo)
                }
            }

            DialogStatus.ROOM_ENTER_ERROR -> {
                RoomEnterErrorDialog(
                    dismiss = dismiss
                ) {
                    dialogAction.dialogRoomEnterError()
                }
            }

            DialogStatus.ROOM_EXIT -> {
                RoomExitDialog(
                    dismiss = dismiss,
                ) {
                    dialogAction.dialogRoomExit()
                }
            }

            DialogStatus.ROOM_EDIT -> {
                val roomInfo = dialogAction.getRoomInfo()
                EditRoomSheet(
                    getTitle = roomInfo.title,
                    getDescription = roomInfo.description,
                    getPassword = roomInfo.enterPassword,
                    onDismissRequest = dismiss,
                ) { title, description, password ->
                    dialogAction.dialogRoomEdit(title, description, password)
                }
            }

            DialogStatus.USER_ADD -> {
                AddUserSheet(
                    roomId = dialogAction.getRoomInfoUniqueId(),
                    onDismissRequest = dismiss
                )
            }

            DialogStatus.RAID_ADD -> {
                AddRaidSheet(
                    roomId = dialogAction.getRoomInfoUniqueId(),
                    onDismissRequest = dismiss,
                    successAction = dismiss
                )
            }

            DialogStatus.RAID_EDIT -> {
                EditRaidSheet(
                    raidInfo = dialogAction.getRaidInfo(),
                    onDismissRequest = dismiss,
                    successAction = dismiss
                )
            }

            DialogStatus.RAID_FILTER -> {

            }
            DialogStatus.RAID_DELETE -> {
                DeleteRaidDialog(
                    dismiss = dismiss
                ) {
                    dialogAction.dialogRaidDelete()
                }
            }

            DialogStatus.RAID_USER_ADD -> {
                val userAndCharacterMap = dialogAction.getUserAndCharacterMap()
                if (userAndCharacterMap.isNotEmpty()) {
                    AddRaidUserSheet(
                        userAndCharacterMap = userAndCharacterMap,
                        onDismissRequest = dismiss
                    ) { character -> dialogAction.dialogUserAdd(character) }
                }
            }

            DialogStatus.RAID_USER_DELETE -> {
                DeleteRaidUserDialog(dismiss = dismiss) {
                    dialogAction.dialogUserDelete()
                }
            }

            DialogStatus.CHARACTER_EDIT -> {
                EditCharacterDialog(
                    userInfo = dialogAction.getUserInfo(),
                    characterList = dialogAction.getCharacterList(),
                    dismiss = dismiss
                ) { name ->
                    dialogAction.dialogCharacterEdit(name)
                }
            }

            DialogStatus.CHARACTER_DELETE -> {
                DeleteCharacterDialog(
                    name = dialogAction.getUserInfo().name,
                    dismiss = dismiss
                ) {
                    dialogAction.dialogCharacterDelete()
                }

            }

            DialogStatus.SETTING_EDIT_NAME -> {

            }
            DialogStatus.SHARE_SHEET -> {
                ShareSheet(roomInfo = dialogAction.getRoomInfo()) {
                    dismiss
                }
            }
        }
    }
}