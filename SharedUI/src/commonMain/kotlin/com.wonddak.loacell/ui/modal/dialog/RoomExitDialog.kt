package com.wonddak.loacell.ui.modal.dialog

import androidx.compose.runtime.Composable
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Dialog
import io.ktor.websocket.Frame

@Composable
fun RoomExitDialog(
    modalStatus: ModalStatus,
    confirm: () -> Unit,
) {
    BaseDialog(
        modalStatus = modalStatus,
        titleText = Dialog.ROOM_EXIT.title,
        confirmButtonText = "나가기",
        confirmButtonAction = {
            confirm()
            modalStatus.hide()
        },
        dismissButtonText = "취소"
    ) {
        Frame.Text(text = "방에서 나가시겠습니까?")
    }
}