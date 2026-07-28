package com.wonddak.loacell.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CheckBoxRow(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    enabled: Boolean,
    onClick: (value: Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onClick
        )
        Text(
            text = text,
            modifier = Modifier.clickable(enabled = enabled) { onClick(!checked) },
        )
    }
}
