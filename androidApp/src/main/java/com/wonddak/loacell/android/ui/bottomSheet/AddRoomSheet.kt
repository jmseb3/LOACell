package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.wonddak.loacell.android.ui.common.LengthLimitTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomSheet(
    onDismissRequest: () -> Unit,
    addAction: (title: String, description: String) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var errorMsg by remember {
        mutableStateOf("")
    }

    BaseSheet(
        title = "방 만들기",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            if (title.isNotEmpty()) {
                addAction(title, description)
            } else {
                errorMsg = "제목이 비어있습니다."
            }

        },
        errorMsg =  errorMsg,
        updateErrorMsg = {errorMsg = it}
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
        }
    }
}
