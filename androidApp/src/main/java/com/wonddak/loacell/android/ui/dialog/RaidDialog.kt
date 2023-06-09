package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteRaidDialog(
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    DeleteDialog(
        title = "레이드 정보 삭제",
        confirm = confirm,
        dismiss = dismiss
    ) {
        Column() {
            Text(text = "레이드 정보를 삭제 하시겠습니까?")
        }
    }
}

@Composable
fun DeleteRaidUserDialog(
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    DeleteDialog(
        title = "레이드 유저 정보 삭제",
        confirm = confirm,
        dismiss = dismiss
    ) {
        Column() {
            Text(text = "선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
        }
    }
}