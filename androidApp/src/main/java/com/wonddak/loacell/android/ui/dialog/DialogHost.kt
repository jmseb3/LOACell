package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRoomSheet
import com.wonddak.loacell.android.ui.bottomSheet.FilterSheet
import com.wonddak.loacell.android.ui.bottomSheet.ShareSheet
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.model.Sheet


@Composable
fun DialogHost(
    dialogStatus: Modal?,
    dialogAction: DialogAction,
    content: @Composable () -> Unit
) {
    val dismiss = { dialogAction.hideDialog() }
    Box() {
        content()
        dialogStatus.let { modal ->
            when (modal) {
                Dialog.ROOM_ACTION -> {
                    RoomActionDialog(
                        dismiss = dismiss
                    ) { status ->
                        dialogAction.dialogRoomAction(status)
                    }
                }
                Sheet.ROOM_ADD -> {
                    AddRoomSheet(
                        onDismissRequest = dismiss
                    ) { title, description, password ->
                        dialogAction.dialogRoomAdd(title, description, password)
                    }
                }
                Dialog.ROOM_ENTER -> {
                    RoomEnterDialog(
                        nowEnterRoomList = dialogAction.getRoomListToUniqueId(),
                        dismiss = dismiss
                    ) { roomId, roomInfo ->
                        dialogAction.dialogRoomEnter(roomId, roomInfo)
                    }
                }
                Dialog.ROOM_ENTER_BY_SCHEME -> {
                    RoomEnterDialog(
                        nowEnterRoomList = dialogAction.getRoomListToUniqueId(),
                        prevData = dialogAction.getSchemeData(),
                        dismiss = dismiss
                    ) { roomId, roomInfo ->
                        dialogAction.dialogRoomEnterByScheme(roomId, roomInfo)
                    }
                }
                Dialog.ROOM_ENTER_ERROR -> {
                    RoomEnterErrorDialog(
                        dismiss = dismiss
                    ) {
                        dialogAction.dialogRoomEnterError()
                    }
                }
                Dialog.ROOM_EXIT -> {
                    RoomExitDialog(
                        dismiss = dismiss,
                    ) {
                        dialogAction.dialogRoomExit()
                    }
                }
                Sheet.ROOM_EDIT -> {
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
                Sheet.USER_ADD -> {
                    AddUserSheet(dialogAction =  dialogAction)
                }
                Sheet.RAID_ADD -> {
                    AddRaidSheet(
                        onDismissRequest = dismiss
                    ) { fbRaidInfo ->
                        dialogAction.dialogRaidAdd(fbRaidInfo)
                    }
                }
                Sheet.RAID_EDIT -> {
                    EditRaidSheet(
                        raidInfo = dialogAction.getRaidInfo(),
                        onDismissRequest = dismiss
                    ) { fbRaidInfo ->
                        dialogAction.dialogRaidEdit(fbRaidInfo)
                    }
                }
                Sheet.RAID_FILTER -> {
                    FilterSheet(
                        dialogAction.getTotalRoomInfo(),
                        dismiss = dismiss
                    ) { filter ->
                        dialogAction.dialogFilterUpdate(filter)
                    }
                }
                Dialog.RAID_DELETE -> {
                    DeleteRaidDialog(
                        dismiss = dismiss
                    ) {
                        dialogAction.dialogRaidDelete()
                    }
                }
                Sheet.RAID_USER_ADD -> {
                    val userAndCharacterMap = dialogAction.getUserAndCharacterMap()
                    if (userAndCharacterMap.isNotEmpty()) {
                        AddRaidUserSheet(
                            userAndCharacterMap = userAndCharacterMap,
                            onDismissRequest = dismiss
                        ) { character -> dialogAction.dialogUserAdd(character) }
                    }
                }
                Dialog.RAID_USER_DELETE -> {
                    DeleteRaidUserDialog(dismiss = dismiss) {
                        dialogAction.dialogUserDelete()
                    }
                }
                Dialog.CHARACTER_EDIT -> {
                    EditCharacterDialog(
                        userInfo = dialogAction.getUserInfo(),
                        characterList = dialogAction.getCharacterList(),
                        dismiss = dismiss
                    ) { name ->
                        dialogAction.dialogCharacterEdit(name)
                    }
                }
                Dialog.CHARACTER_DELETE -> {
                    DeleteCharacterDialog(
                        name = dialogAction.getUserInfo().name,
                        dismiss = dismiss
                    ) {
                        dialogAction.dialogCharacterDelete()
                    }

                }
                Dialog.SETTING_EDIT_NAME -> {
                    ProfileNameDialog(
                        dialogAction.getDisplayName(),
                        dismiss = dismiss
                    ) {
                        dialogAction.dialogEditName(it)
                    }
                }
                Sheet.SHARE_SHEET -> {
                    ShareSheet(
                        roomInfo = dialogAction.getRoomInfo(),
                        onDismissRequest = dismiss
                    )
                }
                Sheet.TEST_SHEET -> {
                    BaseSheet(
                        title = "여백 테스트",
                        buttonText = "확인",
                        onDismissRequest = dismiss,
                        buttonClickAction = dismiss
                    ) {
                        Text(text = "테스트 문구")
                    }
                }
            }
        }
    }
}