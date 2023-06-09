package com.wonddak.loacell.android.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LengthLimitTextField(
    modifier: Modifier,
    text: String,
    label: String,
    placeHolder: String,
    maxLine: Int,
    maxLength: Int,
    enabled :Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    textChange: (text: String) -> Unit
) {

    Column(
        modifier = modifier,
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
            enabled = enabled
        )
    }
}

@Composable
fun LengthLimitTextField(
    modifier: Modifier,
    textFieldValue: TextFieldValue,
    label: String,
    placeHolder: String,
    maxLine: Int,
    maxLength: Int,
    enabled :Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    textChange: (text: TextFieldValue) -> Unit
) {

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${textFieldValue.text.length}/$maxLength",
            textAlign = TextAlign.End
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = {
                if (it.text.length <= maxLength) {
                    textChange(it)
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
            enabled = enabled
        )
    }
}