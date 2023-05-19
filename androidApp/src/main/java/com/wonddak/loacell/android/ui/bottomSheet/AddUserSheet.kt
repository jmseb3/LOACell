package com.wonddak.loacell.android.ui.room.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.api.LostArkApi
import com.wonddak.loacell.api.onError
import com.wonddak.loacell.api.onException
import com.wonddak.loacell.api.onSuccess
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserView(
    modifier: Modifier = Modifier,
    roomId:String,
    addAction: () -> Unit
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

        var errorMsg by remember {
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
                Spacer(modifier = Modifier.height(5.dp))
                if (errorMsg.isNotEmpty()) {
                    Text(text = errorMsg)
                }
                if (showProgress) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "${user}님의 캐릭터 정보를 불러 옵니다.")
                        CircularProgressIndicator()
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            errorMsg = ""
                            if (user.isEmpty()) {
                                errorMsg = "유저 이름이 비어있습니다."
                                return@launch
                            }
                            if (characterName.isEmpty()){
                                errorMsg = "캐릭터 이름이 비어있습니다."
                                return@launch
                            }
                            showProgress = true

                            val characterResult = LostArkApi().getCharacterInfo(characterName)
                            characterResult.onSuccess {list ->
                                FireStoreHelper.addCharacters(list)
                                FireStoreHelper.addUser(
                                    roomId = roomId,
                                    name = user,
                                    representativeCharacter = characterName,
                                    characterList = list
                                )
                                addAction()
                            }
                            characterResult.onError { code, message ->
                                errorMsg = "$message($code)"
                            }
                            characterResult.onException {
                                errorMsg = it.message ?: "exception"
                            }
                            showProgress = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "추가하기")
                }
            }

    }
}
