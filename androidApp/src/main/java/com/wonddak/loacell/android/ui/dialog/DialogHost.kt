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
                    RoomActionDialog(dialogAction = dialogAction)
                }
                Sheet.ROOM_ADD -> {
                    AddRoomSheet(dialogAction = dialogAction)
                }
                Dialog.ROOM_ENTER -> {
                    RoomEnterDialog(
                        dialogAction = dialogAction
                    )
                }
                Dialog.ROOM_ENTER_BY_SCHEME -> {
                    RoomEnterDialog(
                        dialogAction = dialogAction,
                        prevData = dialogAction.getSchemeData(),
                    )

                }
                Dialog.ROOM_ENTER_ERROR -> {
                    RoomEnterErrorDialog(
                        dismiss = dismiss
                    ) {
                        dialogAction.dialogRoomEnterError()
                    }
                }
                Dialog.ROOM_EXIT -> {
                    RoomExitDialog(dialogAction)
                }
                Sheet.ROOM_EDIT -> {
                    EditRoomSheet(dialogAction = dialogAction)
                }
                Sheet.USER_ADD -> {
                    AddUserSheet(dialogAction =  dialogAction)
                }
                Sheet.RAID_ADD -> {
                    AddRaidSheet(dialogAction = dialogAction)
                }
                Sheet.RAID_EDIT -> {
                    EditRaidSheet(dialogAction = dialogAction)
                }
                Sheet.RAID_FILTER -> {
                    FilterSheet(dialogAction)
                }
                Dialog.RAID_DELETE -> {
                    DeleteRaidDialog(dialogAction = dialogAction)
                }
                Sheet.RAID_USER_ADD -> {
                    val userAndCharacterMap = dialogAction.getUserAndCharacterMap()
                    if (userAndCharacterMap.isNotEmpty()) {
                        AddRaidUserSheet(
                            userAndCharacterMap = userAndCharacterMap,
                            dialogAction = dialogAction
                        )
                    }
                }
                Dialog.RAID_USER_DELETE -> {
                    DeleteRaidUserDialog(dialogAction = dialogAction)
                }
                Dialog.CHARACTER_EDIT -> {
                    EditCharacterDialog(dialogAction = dialogAction)
                }
                Dialog.CHARACTER_DELETE -> {
                    DeleteCharacterDialog(dialogAction = dialogAction)
                }
                Dialog.SETTING_EDIT_NAME -> {
                    ProfileNameDialog(dialogAction = dialogAction)
                }
                Sheet.SHARE_SHEET -> {
                    ShareSheet(dialogAction = dialogAction)
                }
                Sheet.TEST_SHEET -> {
                    BaseSheet(
                        title = modal.title,
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