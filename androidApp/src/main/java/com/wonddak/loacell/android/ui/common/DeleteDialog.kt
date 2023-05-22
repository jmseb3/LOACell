package com.wonddak.loacell.android.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DeleteDialog(
    title: String? = null,
    confirm: () -> Unit,
    dismiss: () -> Unit,
    bodyContent: @Composable () -> Unit,
) {

    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            title?.let {
                Text(text = it)
            }
        },
        text = {
            bodyContent()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm()
                },
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text("취소")
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}