package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.api.LostArkApi
import com.wonddak.loacell.api.model.CharacterInfo
import kotlinx.coroutines.launch

@Composable
fun AddUserSheet(
    addAction : (user:String,characterList:List<CharacterInfo>) -> Unit
) {
    BaseSheet(title = "Add User Info") {
        var characterName by remember {
            mutableStateOf("")
        }
        var characterList: List<CharacterInfo> by remember {
            mutableStateOf(emptyList())
        }
        var selectedList: Set<Int> by remember {
            mutableStateOf(emptySet())
        }
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        val textFieldModifier = Modifier.fillMaxWidth()
        var showProgress by remember {
            mutableStateOf(false)
        }
        if (showProgress) {
            CircularProgressIndicator()
        }
        if (characterList.isEmpty()) {
            OutlinedTextField(
                value = characterName,
                onValueChange = { characterName = it },
                label = {
                    Text(text = "대표 캐릭터")
                },
                placeholder = {
                    Text(text = "대표 캐릭터 입력")
                },
                maxLines = 3,
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
                        val api = LostArkApi()
                        characterList = api.getCharacterInfo(characterName)
                        characterName = ""
                        showProgress = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Search")
            }
        } else {
            Column(modifier = Modifier.fillMaxHeight(0.8f)) {
                var user by remember {
                    mutableStateOf("")
                }
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .weight(1f)
                ) {
                    itemsIndexed(characterList) { index, info ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedList.contains(index),
                                onCheckedChange = {
                                    selectedList = if (selectedList.contains(index)) {
                                        val temp = selectedList.toMutableSet()
                                        temp.remove(index)
                                        temp
                                    } else {
                                        val temp = selectedList.toMutableSet()
                                        temp.add(index)
                                        temp
                                    }
                                }
                            )
                            Text(text = info.characterName)
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        val selectedCharacterList = selectedList.map { index -> characterList[index] }
                        addAction(user, selectedCharacterList)
                    }
                ) {
                    Text("Hello")
                }
            }
        }

    }
}

@Composable
@Preview
fun AddUserSheetPreview() {
    AddUserSheet() {_,_ ->}
}