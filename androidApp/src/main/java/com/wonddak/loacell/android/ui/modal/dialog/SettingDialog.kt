package com.wonddak.loacell.android.ui.modal.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.sharedapi.firebase.model.FBDataItem

@Composable
fun ProfileNameDialog(
    dialogAction: DialogAction,
) {
    val nowName = dialogAction.getDisplayName()
    var name by remember {
        mutableStateOf(TextFieldValue(nowName, TextRange(0, nowName.length)))
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    BaseDialog(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(0.8f),
        dismiss = {
                  dialogAction.hideDialog()
        },
        titleText = Dialog.SETTING_EDIT_NAME.title,
        confirmButtonText = "변경",
        confirmButtonEnabled = name.text.isNotEmpty(),
        confirmButtonAction = {
            if (name.text.isNotEmpty()) {
                dialogAction.dialogEditName(name.text)
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
                maxLength = 12,
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