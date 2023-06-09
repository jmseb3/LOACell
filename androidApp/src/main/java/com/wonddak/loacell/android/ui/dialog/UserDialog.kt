package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character
import com.wonddak.loacell.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterDialog(
    userInfo: UserInfo,
    characterList: List<Character>,
    confirm: (name: String) -> Unit,
    dismiss: () -> Unit
) {
    val representativeCharacter = userInfo.representativeCharacter
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(representativeCharacter) }

    BaseDialog(
        dismiss = dismiss,
        titleText = "대표 캐릭터 변경",
        confirmButtonAction = {
            confirm(selectedText)
        },
        confirmButtonText = "변경",
        confirmButtonEnabled = (representativeCharacter != selectedText),
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
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = "대표 캐릭터 선택")
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                modifier = Modifier
                    .height(200.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                characterList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.name,
                                fontWeight = if (selectedText == item.name) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            selectedText = item.name
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteCharacterDialog(
    name: String,
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    DeleteDialog(
        title = "유저 정보 삭제",
        confirm = confirm,
        dismiss = dismiss
    ) {
        Column() {
            Text(text = "${name}님 의 정보를 삭제 하시겠습니까?")
        }
    }
}