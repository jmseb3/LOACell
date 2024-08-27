package com.wonddak.loacell.ui.modal.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.SetBackAction

@Composable
fun BaseDialog(
    modalStatus: ModalStatus,
    modifier: Modifier = Modifier,
    titleText: String? = null,
    confirmButtonText: String? = null,
    confirmButtonEnabled: Boolean = true,
    confirmButtonAction: () -> Unit = {},
    dismissButtonText: String? = null,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    SetBackAction(modalStatus.status) {
        modalStatus.hide()
    }
    if (modalStatus.status) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                modalStatus.hide()
            },
            title = {
                if (titleText != null) {
                    Text(text = titleText)
                }
            },
            text = {
                content()
            },
            confirmButton = {
                confirmButtonText?.let {
                    TextButton(
                        onClick = confirmButtonAction,
                        enabled = confirmButtonEnabled
                    ) {
                        Text(it)
                    }
                }
            },
            dismissButton = {
                dismissButtonText?.let {
                    TextButton(
                        onClick = {
                            modalStatus.hide()
                        },
                    ) {
                        Text(it)
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            properties = dialogProperties
        )
    }
}

@Composable
fun ConfirmDialog(
    modalStatus: ModalStatus,
    title: String? = null,
    bodyText: String,
    confirmButtonText: String = "확인",
    confirm: () -> Unit,
) {
    BaseDialog(
        modalStatus = modalStatus,
        titleText = title,
        confirmButtonText = confirmButtonText,
        confirmButtonAction = confirm,
        dismissButtonText = "취소",
        content = {
            Text(text = bodyText)
        }
    )
}

@Composable
fun DeleteDialog(
    modalStatus: ModalStatus,
    title: String? = null,
    confirm: () -> Unit,
    bodyContent: @Composable () -> Unit,
) {
    BaseDialog(
        modalStatus = modalStatus,
        titleText = title,
        confirmButtonText = "삭제",
        confirmButtonAction = confirm,
        dismissButtonText = "취소",
        content = bodyContent
    )
}