package com.wonddak.loacell.ui.modal.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.wonddak.loacell.DataModalStatus
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.ui.common.DropDownTextField
import io.ktor.websocket.Frame

@Composable
fun SelectIdDialog(
    modalStatus: DataModalStatus<List<RaidInfo>>,
    confirm: (RaidInfo) -> Unit,
) {
    var selectedInfo: RaidInfo? by remember {
        mutableStateOf(null)
    }
    var expanded by remember { mutableStateOf(false) }

    BaseDialog(
        modalStatus = modalStatus,
        titleText = "레이드 정보 선택",
        confirmButtonText = "확인",
        confirmButtonEnabled = selectedInfo != null,
        confirmButtonAction = {
            confirm(selectedInfo!!)
        },
        dismissButtonText = "취소",
    ) {
        Column {
            Frame.Text(text = "해당 일에는 여러개의 레이드 정보가 있습니다.\n정보를 볼 레이드를 선택해 주세요.")
            DropDownTextField(
                Modifier,
                "레이드 정보 선택",
                selectedInfo?.title ?: "",
                expanded,
                {
                    expanded = it
                }
            ) {
                modalStatus.subItem!!.forEach { item ->
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