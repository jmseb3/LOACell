package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.ui.common.LengthLimitTextField

@Composable
fun DeleteRaidDialog(
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    DeleteDialog(
        title = "레이드 정보 삭제",
        confirm = confirm,
        dismiss = dismiss
    ) {
        Column() {
            Text(text = "레이드 정보를 삭제 하시겠습니까?")
        }
    }
}

@Composable
fun DeleteRaidUserDialog(
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    DeleteDialog(
        title = "레이드 유저 정보 삭제",
        confirm = confirm,
        dismiss = dismiss
    ) {
        Column() {
            Text(text = "선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
        }
    }
}

@Composable
fun EditRaidTitleDialog(
    nowTitle: String,
    success: (title: String) -> Unit,
    dismiss: () -> Unit
) {
    var typeText by remember {
        mutableStateOf(TextFieldValue(nowTitle, TextRange(0,nowTitle.length)))
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    BaseDialog(
        dismiss = dismiss,
        titleText = "제목 변경",
        confirmButtonAction = {
            success(typeText.text)
        },
        confirmButtonEnabled = typeText.text.isNotEmpty(),
        confirmButtonText = "변경",
        dismissButtonText = "취소"
    ) {
        LengthLimitTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .focusRequester(focusRequester),
            textFieldValue = typeText,
            label = "제목",
            placeHolder = "제목을 입력하세요.",
            maxLine = 1,
            maxLength = 10,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            textChange = {
                typeText = it
            }
        )
    }

}