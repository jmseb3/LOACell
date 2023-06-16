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
import com.wonddak.loacell.android.ui.common.CheckBoxRow
import com.wonddak.loacell.android.ui.common.LengthLimitTextField


@Composable
fun AddRoomSheet(
    onDismissRequest: () -> Unit,
    addAction: (title: String, description: String, password: String) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }
    var usePassword by remember {
        mutableStateOf(false)
    }
    var password by remember {
        mutableStateOf("")
    }

    RoomSheetBase(
        sheetTitle = "방 만들기",
        getTitle = title,
        updateTitle = {title = it},
        getDescription = description,
        updateDescription = {description = it},
        getPassword = password,
        updatePassword = {password = it},
        usePassword = usePassword,
        updateUsePassword = {usePassword = it},
        onDismissRequest = onDismissRequest,
        addAction = addAction
    )
}

@Composable
fun EditRoomSheet(
    getTitle : String,
    getDescription : String,
    getPassword : String,
    onDismissRequest: () -> Unit,
    editAction: (title: String, description: String, password: String) -> Unit
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
    RoomSheetBase(
        useCloseButton = true,
        buttonText = "수정",
        sheetTitle = "수정하기",
        getTitle = title,
        updateTitle = {title = it},
        getDescription = description,
        updateDescription = {description = it},
        getPassword = password,
        updatePassword = {password = it},
        usePassword = usePassword,
        updateUsePassword = {usePassword = it},
        onDismissRequest = onDismissRequest,
        addAction = editAction
    )
}


@Composable
private fun RoomSheetBase(
    useCloseButton :Boolean = false,
    buttonText :String = "추가",
    sheetTitle: String,
    getTitle : String,
    updateTitle :(title:String) -> Unit,
    getDescription : String,
    updateDescription :(description:String) -> Unit,
    getPassword : String,
    updatePassword :(password:String) -> Unit,
    usePassword :Boolean,
    updateUsePassword :(update:Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    addAction: (title: String, description: String, password: String) -> Unit
) {
    var errorMsg by remember {
        mutableStateOf("")
    }
    BaseSheet(
        title = sheetTitle,
        errorMsg = errorMsg,
        useCloseIcon = useCloseButton,
        buttonText = buttonText,
        enabledButton = getTitle.isNotEmpty() && ((!usePassword && getPassword.isEmpty()) || (usePassword && getPassword.isNotEmpty())),
        updateErrorMsg = { errorMsg = it },
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            addAction(getTitle, getDescription, getPassword)
        },
    ) {
        Column() {
            val focusManager = LocalFocusManager.current
            val textFieldModifier = Modifier
                .fillMaxWidth()
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = getTitle,
                label = "제목",
                placeHolder = "제목을 입력하세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                textChange = updateTitle
            )
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = getDescription,
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
                textChange = updateDescription
            )
            Row() {
                CheckBoxRow(
                    modifier = Modifier.weight(1f),
                    text = "비밀번호 사용",
                    value = usePassword,
                    enabled = true,
                    onClick = {
                        if (!it) {
                            updatePassword("")
                        }
                        updateUsePassword(it)
                    }
                )
            }
            AnimatedVisibility(usePassword) {
                LengthLimitTextField(
                    modifier = textFieldModifier,
                    text = getPassword,
                    label = "방 입장 비밀번호",
                    placeHolder = "비밀번호를 입력하세요.",
                    maxLine = 1,
                    maxLength = 10,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    textChange = updatePassword
                )
            }
        }
    }
}
