package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.android.ui.common.CheckBoxRow
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.model.Sheet


@Composable
fun AddRoomSheet(
    dialogAction: DialogAction
) {
    RoomSheetBase(
        sheetTitle = Sheet.ROOM_ADD.title,
        onDismissRequest = { dialogAction.hideDialog() },
        addAction = {  title, description, password ->
            dialogAction.dialogRoomAdd(title,description,password)
        }
    )
}

@Composable
fun EditRoomSheet(
    dialogAction: DialogAction
) {
    val roomInfo = dialogAction.getRoomInfo()
    RoomSheetBase(
        useCloseButton = true,
        buttonText = "수정",
        sheetTitle = Sheet.ROOM_EDIT.title,
        getTitle = roomInfo.title,
        getDescription = roomInfo.description,
        getPassword = roomInfo.enterPassword,
        onDismissRequest = {
            dialogAction.hideDialog()
        },
        addAction = { title, description, password ->
            dialogAction.dialogRoomEdit(title, description, password)
        }
    )
}

@Composable
private fun RoomSheetBase(
    useCloseButton :Boolean = false,
    buttonText :String = "추가",
    sheetTitle: String,
    getTitle : String = "",
    getDescription : String = "",
    getPassword : String = "",
    onDismissRequest: () -> Unit,
    addAction: (title: String, description: String, password: String) -> Unit
) {

    var title by remember {
        mutableStateOf(getTitle)
    }
    var description by remember {
        mutableStateOf(getDescription)
    }
    var usePassword by remember {
        mutableStateOf(getPassword.isNotEmpty())
    }
    var password by remember {
        mutableStateOf(getPassword)
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    BaseSheet(
        title = sheetTitle,
        errorMsg = errorMsg,
        useCloseIcon = useCloseButton,
        buttonText = buttonText,
        enabledButton = title.isNotEmpty() && ((!usePassword && password.isEmpty()) || (usePassword && password.isNotEmpty())),
        updateErrorMsg = { errorMsg = it },
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            addAction(title, description, password)
        },
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
            Row() {
                CheckBoxRow(
                    modifier = Modifier.weight(1f),
                    text = "비밀번호 사용",
                    value = usePassword,
                    enabled = true,
                    onClick = {
                        if (!it) {
                            password = ""
                        }
                        usePassword = it
                    }
                )
            }
            AnimatedVisibility(usePassword) {
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
                    textChange = {
                        password = it
                    }
                )
            }
        }
    }
}
