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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.database.AppDataBase

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserInfoCard(
    db: AppDataBase,
    user: UserInfo
) {

    val characters by db.getCharacters(user.userId).collectAsState(initial = emptyList())
    var showCharacters by rememberSaveable {
        mutableStateOf(false)
    }
    var showMore by remember {
        mutableStateOf(false)
    }
    var openDialog by remember { mutableStateOf(false) }

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
                text = "${user.name} - ${user.representativeCharacter}",
                textAlign = TextAlign.Center
            )
            AnimatedVisibility(visible = showCharacters) {
                Column {
                    Row() {
                        OutlinedButton(onClick = { openDialog = true }) {
                            Text(text = "대표 캐릭터 변경")
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

    if (openDialog) {
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(user.representativeCharacter) }

        AlertDialog(
            onDismissRequest = {
                openDialog = false
            },
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        characters.map { it.name }.forEach { item ->
                            DropdownMenuItem(
                                content = { Text(
                                    text = item,
                                    fontWeight = if (selectedText == item) FontWeight.Bold else FontWeight.Normal
                                ) },
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
                        db.updateUserRepresentativeCharacter(user.userId, selectedText)
                        openDialog = false
                    }
                ) {
                    Text("변경")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
