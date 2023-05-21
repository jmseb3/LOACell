package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AddRoomSheet(
    addAction: (title: String, description: String) -> Unit
) {
    BaseSheet(title = "방 만들기") {
        Column() {
            var title by remember {
                mutableStateOf("")
            }

            var description by remember {
                mutableStateOf("")
            }
            val focusManager = LocalFocusManager.current

            val textFieldModifier = Modifier
                .fillMaxWidth()
            AddRoomTextField(
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
            AddRoomTextField(
                modifier = textFieldModifier,
                text = description,
                label = "방 설명",
                placeHolder = "방 설명을 입력하세요.",
                maxLine = 3,
                maxLength = 100,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                textChange = {
                    description = it
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    addAction(title, description)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "ADD")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomTextField(
    modifier: Modifier,
    text: String,
    label: String,
    placeHolder: String,
    maxLine: Int,
    maxLength: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    textChange: (text: String) -> Unit
) {

    Column(
        modifier = modifier.padding(10.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${text.length}/$maxLength",
            textAlign = TextAlign.End
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if (it.length <= maxLength) {
                    textChange(it)
                } else {

                }
            },
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeHolder)
            },
            maxLines = maxLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
    }
}

@Preview
@Composable
fun AddRoomSheetPreview() {
    AddRoomSheet(addAction = { _, _ -> })
}