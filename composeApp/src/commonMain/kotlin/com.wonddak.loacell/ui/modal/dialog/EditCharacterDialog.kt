package com.wonddak.loacell.ui.modal.dialog

import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCharacterDialog(
    modalStatus: ModalStatus,
    userInfo: UserInfo,
    confirm: (new: String) -> Unit,
) {
    val representativeCharacter = userInfo.representativeCharacter
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(representativeCharacter) }

    BaseDialog(
        modalStatus = modalStatus,
        titleText = Dialog.CHARACTER_EDIT.title,
        confirmButtonAction = {
            confirm(selectedText)
            modalStatus.hide()
        },
        confirmButtonText = "변경",
        confirmButtonEnabled = (representativeCharacter != selectedText) && selectedText.isNotEmpty(),
        dismissButtonText = "취소"
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true,
                ),
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
                userInfo.characterList.forEach { item ->
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
