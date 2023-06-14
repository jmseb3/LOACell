package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
@Composable
fun ProfileNameDialog(
    nowName: String = "",
    success: (name: String) -> Unit,
    dismiss: () -> Unit,
) {
    var name by remember {
        mutableStateOf(TextFieldValue(nowName, TextRange(0,nowName.length)))
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    BaseDialog(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(0.8f),
        dismiss = dismiss,
        titleText = "이름 변경",
        confirmButtonText = "변경",
        confirmButtonEnabled = name.text.isNotEmpty(),
        confirmButtonAction = {
            if (name.text.isNotEmpty()) {
                success(name.text)
            }
        },
        dismissButtonText = "취소",
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column() {
            LengthLimitTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textFieldValue = name,
                label = "이름",
                placeHolder = "이름을 입력해주세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                textChange = {
                    name = it
                },
            )

        }
    }
}