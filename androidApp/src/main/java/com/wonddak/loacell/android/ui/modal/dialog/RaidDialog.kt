package com.wonddak.loacell.android.ui.modal.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.model.Dialog

@Composable
fun DeleteRaidDialog(
    dialogAction: DialogAction
) {
    DeleteDialog(
        title = Dialog.RAID_DELETE.title,
        confirm = {
            dialogAction.dialogRaidDelete()
        },
        dismiss = {
            dialogAction.hideDialog()
        }
    ) {
        Column() {
            Text(text = "레이드 정보를 삭제 하시겠습니까?")
        }
    }
}

@Composable
fun DeleteRaidUserDialog(
    dialogAction: DialogAction
) {
    DeleteDialog(
        title = Dialog.RAID_USER_DELETE.title,
        confirm = {
            dialogAction.dialogUserDelete()
        },
        dismiss = {
            dialogAction.hideDialog()
        }
    ) {
        Column() {
            Text(text = "선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectIdDialog(
    raidInfoList: List<RaidInfo>,
    dismiss: () -> Unit,
    confirm: (RaidInfo) -> Unit,
) {
    var selectedInfo: RaidInfo? by remember {
        mutableStateOf(null)
    }
    var expanded by remember { mutableStateOf(false) }

    BaseDialog(
        titleText = "레이드 정보 선택",
        confirmButtonText = "확인",
        confirmButtonEnabled = selectedInfo != null,
        confirmButtonAction = {
            confirm(selectedInfo!!)
        },
        dismissButtonText = "취소",
        dismiss = dismiss
    ) {
        Column {
            Text(text = "해당 일에는 여러개의 레이드 정보가 있습니다.\n정보를 볼 레이드를 선택해 주세요.")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(horizontal = 10.dp),
                    value = selectedInfo?.title ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "레이드 정보 선택")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    raidInfoList.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (selectedInfo == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedInfo = item
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}