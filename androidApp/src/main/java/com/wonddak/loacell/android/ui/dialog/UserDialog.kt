package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.model.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterDialog(
    dialogAction: DialogAction
) {
    val userInfo = dialogAction.getUserInfo()
    val representativeCharacter = userInfo.representativeCharacter
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(representativeCharacter) }

    BaseDialog(
        dismiss = {
            dialogAction.hideDialog()
        },
        titleText = Dialog.CHARACTER_EDIT.title,
        confirmButtonAction = {
            dialogAction.dialogCharacterEdit(selectedText)
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
                dialogAction.getCharacterList().forEach { item ->
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
    dialogAction: DialogAction,
) {
    DeleteDialog(
        title = Dialog.CHARACTER_DELETE.title,
        confirm = { dialogAction.dialogCharacterDelete() },
        dismiss = { dialogAction.hideDialog() }
    ) {
        Column() {
            Text(text = "${dialogAction.getUserInfo().name}님 의 정보를 삭제 하시겠습니까?")
        }
    }
}