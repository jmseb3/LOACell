package com.wonddak.loacell.android.ui.raid.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.database.AppDataBase

@Composable
fun UserInfoCard(
    name :String,
    representativeCharacter :String,
    characters : List<Character>,
    editCharacter :(name:String)  -> Unit,
    deleteUser :() -> Unit
) {

    var showCharacters by remember {
        mutableStateOf(false)
    }
    var showMore by remember {
        mutableStateOf(false)
    }
    var openCharacterEditDialog by remember { mutableStateOf(false) }
    var openCharacterDeleteDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCharacters = !showCharacters },
                text = "$name - $representativeCharacter",
                textAlign = TextAlign.Center
            )
            AnimatedVisibility(visible = showCharacters) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MyIconButton(
                            SharedRes.images.change_person
                        ) {
                            openCharacterEditDialog = true
                        }
                        MyIconButton(
                            SharedRes.images.delete
                        ) {
                            openCharacterDeleteDialog = true
                        }
                    }

                    Divider()
                    if (characters.size <= 6) {
                        characters.forEach { UserInfoCharacters(it) }
                    } else {
                        if (showMore) {
                            characters.forEach { UserInfoCharacters(it) }
                        } else {
                            characters.subList(0, 6).forEach { UserInfoCharacters(it) }
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMore = !showMore },
                            text = if (showMore) "닫기" else "더보기 (${characters.size - 6})",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (openCharacterEditDialog) {
        EditCharacterDialog(
            representativeCharacter = representativeCharacter,
            characters = characters,
            confirm = { name ->
                editCharacter(name)
                openCharacterEditDialog = false
            },
            dismiss = {
                openCharacterEditDialog = false
            }
        )
    }
    if (openCharacterDeleteDialog) {
        DeleteCharacterDialog(
            name = name,
            confirm = {
                deleteUser()

                openCharacterDeleteDialog = false
            },
            dismiss = {
                openCharacterDeleteDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditCharacterDialog(
    representativeCharacter: String,
    characters: List<Character>,
    confirm: (name: String) -> Unit,
    dismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(representativeCharacter) }

    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(text = "대표 캐릭터 변경")
        },
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "대표 캐릭터 선택")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )

                ExposedDropdownMenu(
                    modifier = Modifier.height(200.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    characters.map { it.name }.forEach { item ->
                        DropdownMenuItem(
                            content = {
                                Text(
                                    text = item,
                                    fontWeight = if (selectedText == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedText = item
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm(selectedText)
                },
                enabled = (representativeCharacter != selectedText)
            ) {
                Text("변경")
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text("취소")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun DeleteCharacterDialog(
    name: String,
    confirm: () -> Unit,
    dismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(text = "유저 정보 삭제")
        },
        text = {
            Column() {
                Text(text = "${name}님 의 정보를 삭제 하시겠습니까?")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm()
                },
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text("취소")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}