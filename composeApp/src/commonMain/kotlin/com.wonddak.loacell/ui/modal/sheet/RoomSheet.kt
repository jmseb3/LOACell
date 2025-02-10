package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.ui.common.LengthLimitTextField
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.task_finish_done
import loacell.composeapp.generated.resources.task_finish_not
import org.jetbrains.compose.resources.painterResource


@Composable
fun RoomSheet(
    modalStatus: ModalStatus,
    roomInfo: RoomInfo? = null,
    addAction: (title: String, description: String, password: String) -> Unit,
) {
    RoomSheetBase(
        modalStatus = modalStatus,
        roomInfo = roomInfo,
        addAction = addAction
    )
}

@Composable
fun RoomSheetBase(
    modalStatus: ModalStatus,
    roomInfo: RoomInfo?,
    addAction: (title: String, description: String, password: String) -> Unit,
) {

    var title by remember {
        mutableStateOf(roomInfo?.title ?: "")
    }
    var description by remember {
        mutableStateOf(roomInfo?.description ?: "")
    }
    var usePassword by remember {
        mutableStateOf(roomInfo?.enterPassword?.isNotEmpty() ?: false)
    }
    var password by remember {
        mutableStateOf(roomInfo?.enterPassword ?: "")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    BaseSheet(
        modalStatus = modalStatus,
        title = roomInfo?.let { Sheet.ROOM_EDIT.title } ?: Sheet.ROOM_ADD.title,
        useCloseIcon = true,
        errorMsg = errorMsg,
        enabledButton = title.isNotEmpty() && ((!usePassword && password.isEmpty()) || (usePassword && password.isNotEmpty())),
        buttonText = roomInfo?.let { "수정" } ?: "추가",
        updateErrorMsg = { errorMsg = it },
        buttonClickAction = {
            addAction(title, description, password)
        }
    ) {
        Column() {
            val focusManager = LocalFocusManager.current
            val textFieldModifier = Modifier
                .fillMaxWidth()
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = title,
                label = "제목",
                placeHolder = "제목을 입력하세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                textChange = {
                    title = it
                }
            )
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = description,
                label = "방 설명",
                placeHolder = "방 설명을 입력하세요.",
                maxLine = 3,
                maxLength = 100,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                textChange = {
                    description = it
                }
            )
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = password,
                label = "방 입장 비밀번호",
                placeHolder = "비밀번호를 입력하세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    IconButton({
                        if (usePassword) {
                            password = ""
                            usePassword = false
                        } else {
                            usePassword = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(if (usePassword) Res.drawable.task_finish_done else Res.drawable.task_finish_not),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                enabled = usePassword,
                textChange = {
                    password = it
                }
            )
        }
    }
}
