package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.ui.common.DropDownTextField

@Composable
fun RaidUserAddSheet(
    modalStatus: ModalStatus,
    userAndCharacterMap: Map<String, List<Character>>,
    confirm: (Character) -> Unit,
) {

    var selectedUser: String? by remember { mutableStateOf(null) }
    var selectedCharacter: Character? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        selectedUser = null
        selectedCharacter = null
    }

    val textFieldModifier = Modifier.fillMaxWidth()
    var expandUser by remember {
        mutableStateOf(false)
    }
    var expandCharacter by remember {
        mutableStateOf(false)
    }

    BaseSheet(
        modalStatus,
        title = Sheet.RAID_USER_ADD.title,
        enabledButton = selectedCharacter != null,
        buttonClickAction = {
            selectedCharacter?.let { confirm(it) }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DropDownTextField(
                modifier = textFieldModifier,
                label = "유저 선택",
                value = selectedUser ?: "유저를 선택해 주세요",
                expand = expandUser,
                updateExpand = {
                    expandUser = it
                }
            ) {
                userAndCharacterMap.forEach { (userName, list) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = userName,
                                fontWeight = if (selectedUser == userName) FontWeight.Bold else FontWeight.Normal
                            )
                        }, onClick = {
                            selectedUser = userName
                            selectedCharacter = runCatching {
                                list.first()
                            }.getOrNull()
                            expandUser = false
                        }
                    )
                }
            }
            userAndCharacterMap[selectedUser]?.let { chrList ->
                DropDownTextField(
                    modifier = textFieldModifier,
                    label = "캐릭터 선택",
                    value = selectedCharacter?.name ?: "캐릭터를 선택해 주세요",
                    expand = expandCharacter,
                    updateExpand = {
                        expandCharacter = it
                    }
                ) {
                    chrList.forEach { character ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = character.name,
                                    fontWeight = if (selectedCharacter == character) FontWeight.Bold else FontWeight.Normal
                                )
                            }, onClick = {
                                selectedCharacter = character
                                expandCharacter = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            selectedCharacter?.let { character ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = character.className)
                    Text(text = character.getLevel().toString())
                }
            }
        }
    }
}