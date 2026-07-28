package com.wonddak.loacell.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    modifier: Modifier,
    label: String,
    value: String,
    expand: Boolean,
    updateExpand: (Boolean) -> Unit,
    dropDownContent: @Composable ColumnScope. () -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expand,
        onExpandedChange = {
            updateExpand(!expand)
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    .padding(horizontal = 10.dp),
                value = value,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = label)
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand) })

            ExposedDropdownMenu(
                expanded = expand,
                onDismissRequest = { updateExpand(false) },
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                dropDownContent()
            }
        }

    }
}
