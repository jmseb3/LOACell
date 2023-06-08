package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    var errorMsg by remember {
        mutableStateOf("")
    }
    LaunchedEffect(usePassword) {
        if (!usePassword) {
            password = ""
        }
    }

    BaseSheet(
        title = "방 만들기",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            addAction(title, description, password)
        },
        errorMsg = errorMsg,
        enabledButton = title.isNotEmpty() && ((!usePassword && password.isEmpty()) || (usePassword && password.length == 6)),
        updateErrorMsg = { errorMsg = it }
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
                    onClick = { value ->
                        usePassword = value
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
                    maxLength = 6,
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
