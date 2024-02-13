package com.wonddak.loacell.android.ui.dialog

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
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.sharedapi.firebase.model.FBDataItem

@Composable
fun ProfileNameDialog(
    nowName: String = "",
    dismiss: () -> Unit,
    success: (name: String) -> Unit,
) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeOwnerDialog(
    owner :String,
    fbDataList: List<FBDataItem>,
    confirm: (uid: String) -> Unit,
    dismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem: FBDataItem? by remember { mutableStateOf(null) }

    BaseDialog(
        dismiss = dismiss,
        titleText = "소유자 변경",
        confirmButtonAction = {
            confirm(selectedItem!!.uid)
        },
        confirmButtonText = "변경",
        confirmButtonEnabled = (owner != selectedItem?.uid),
        dismissButtonText = "취소"
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedItem?.getName() ?: "",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = "변경할 유저 선택")
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                modifier = Modifier
                    .height(200.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                fbDataList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.getName(),
                                fontWeight = if (selectedItem == item) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            selectedItem = item
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}