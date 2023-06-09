package com.wonddak.loacell.android.ui.dialog

import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    title: String? = null,
    confirm: () -> Unit,
    dismiss: () -> Unit,
    bodyContent: @Composable () -> Unit,
) {
    BaseDialog(
        titleText = title,
        confirmButtonText = "삭제",
        confirmButtonAction = confirm,
        dismissButtonText = "취소",
        dismiss = dismiss,
        content = bodyContent
    )
}