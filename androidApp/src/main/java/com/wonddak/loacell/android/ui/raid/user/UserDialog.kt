package com.wonddak.loacell.android.ui.raid.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterDialog(
    representativeCharacter: String,
    characters: List<String>,
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
                    modifier = Modifier.height(200.dp).background(Color.White),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    characters.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    fontWeight = if (selectedText == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedText = item
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