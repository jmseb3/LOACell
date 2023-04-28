package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AddRoomSheet(
    addAction: (title: String, description: String) -> Unit
) {
    BaseSheet(title = "Room Info") {
        var title by remember {
            mutableStateOf("")
        }

        var description by remember {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current

        val textFieldModifier = Modifier.fillMaxWidth()
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text(text = "Title")
            },
            placeholder = {
                Text(text = "Input Room Title")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = textFieldModifier,
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text(text = "Description")
            },
            placeholder = {
                Text(text = "Input Room Description")
            },
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = textFieldModifier,
            keyboardActions = KeyboardActions{
                focusManager.clearFocus()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                addAction(title, description)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "ADD")
        }
    }
}

@Preview
@Composable
fun AddRoomSheetPreview() {
    AddRoomSheet(addAction = { _, _ -> })
}