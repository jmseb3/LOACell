package com.wonddak.loacell.android.ui.room.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character
import com.wonddak.loacell.android.ui.common.DeleteDialog

@OptIn(ExperimentalMaterial3Api::class)
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
        containerColor = Color.White,
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
                        .height(200.dp)
                        .background(Color.White),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    characters.forEach { item ->
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