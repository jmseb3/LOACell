package com.wonddak.loacell.android.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString

@Composable
fun CheckBoxRow(
    modifier: Modifier = Modifier,
    text: String,
    value: Boolean,
    enabled: Boolean,
    onClick: (value: Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = value,
            enabled = enabled,
            onCheckedChange = onClick
        )
        ClickableText(
            text = AnnotatedString(text),
            onClick = {
                if (enabled) {
                    onClick(!value)
                }
            })
    }
}