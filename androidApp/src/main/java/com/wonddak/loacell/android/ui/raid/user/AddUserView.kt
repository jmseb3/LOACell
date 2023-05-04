package com.wonddak.loacell.android.ui.raid.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun AddUserView(
    modifier: Modifier = Modifier,
    addAction: suspend (user: String, characterName :String) -> Unit
) {
    Column(
        modifier = modifier.padding(10.dp)
    ) {
        var characterName by remember {
            mutableStateOf("")
        }

        var user by remember {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        val textFieldModifier = Modifier.fillMaxWidth()
        var showProgress by remember {
            mutableStateOf(false)
        }
            Column() {
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it.replace(" ", "") },
                    label = {
                        Text(text = "유저 이름")
                    },
                    placeholder = {
                        Text(text = "유저 이름 입력")
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = textFieldModifier,
                    singleLine = true
                )
                OutlinedTextField(
                    value = characterName,
                    onValueChange = { characterName = it.replace(" ", "") },
                    label = {
                        Text(text = "대표 캐릭터")
                    },
                    placeholder = {
                        Text(text = "대표 캐릭터 입력")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = textFieldModifier,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            showProgress = true
                            addAction(
                                user.replace("/n",""),
                                characterName.replace("/n","")
                            )
                            showProgress = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "추가하기")
                }
                if (showProgress) {
                    Text(text = "${user}님의 캐릭터 정보를 불러 옵니다.")
                    CircularProgressIndicator()
                }
            }

    }
}
