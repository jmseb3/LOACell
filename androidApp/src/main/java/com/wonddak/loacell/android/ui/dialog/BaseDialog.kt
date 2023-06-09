package com.wonddak.loacell.android.ui.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun BaseDialog(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    confirmButtonText: String? = null,
    confirmButtonEnabled: Boolean = true,
    confirmButtonAction: () -> Unit = {},
    dismissButtonText: String? = null,
    dismiss: () -> Unit = {},
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = dismiss,
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
                    onClick = dismiss,
                ) {
                    Text(it)
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        properties = dialogProperties
    )
}