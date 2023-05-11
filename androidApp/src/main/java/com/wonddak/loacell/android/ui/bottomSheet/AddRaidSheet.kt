package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRaidSheet() {
    BaseSheet(title = "레이드 정보 추가") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expanded by remember { mutableStateOf(false) }
            var nowType: RaidType by remember {
                mutableStateOf(RaidType.VALTAN)
            }
            var nowDifficulty: Difficulty by remember {
                mutableStateOf(Difficulty.Normal)
            }
            val radioOptions = Difficulty.values()

            Spacer(modifier = Modifier.height(10.dp))

            //타입
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(),
                    value = nowType.toKorString(),
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
                    RaidType.values().forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item.toKorString(),
                                    fontWeight = if (nowType == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                nowType = item
                                if (!nowType.accessibleDifficulty().contains(nowDifficulty)) {
                                    nowDifficulty = Difficulty.Normal
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }

            //난이도
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                radioOptions.forEach { difficulty ->
                    val selected = difficulty == nowDifficulty
                    val enabled = nowType.accessibleDifficulty().contains(difficulty)

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selected,
                                onClick = {
                                    nowDifficulty = difficulty
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            enabled = enabled,
                            onClick = { nowDifficulty = difficulty },
                            colors = RadioButtonDefaults.colors(
                                //TODO Match Theme Color
                                selectedColor = Color(0xFF6200EE)
                            )
                        )
                        Text(
                            text = difficulty.toKorString(),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .fillMaxWidth(),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (enabled) Color.Black else Color.Gray
                        )
                    }
                }
            }

            var startGateNumber by remember { mutableStateOf(1) }
            var endGateNumber by remember { mutableStateOf(1) }
            AnimatedVisibility(visible = nowType == RaidType.ABRELSHUD) {
                var checked1 by remember { mutableStateOf(true) }
                var checked2 by remember { mutableStateOf(false) }
                var checked3 by remember { mutableStateOf(false) }
                LaunchedEffect(checked1, checked2, checked3) {
                    endGateNumber = if (checked3) {
                        3
                    } else if (checked2) {
                        2
                    } else {
                        1
                    }
                    startGateNumber = if (checked1) {
                        1
                    } else if (checked2) {
                        2
                    } else {
                        3
                    }
                }
                LaunchedEffect(nowDifficulty) {
                    if (nowDifficulty == Difficulty.Hell) {
                        checked1 = false
                        checked2 = false
                        checked3 = true
                        startGateNumber = 3
                        endGateNumber = 3
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    RaidSheetHeaderText(text = "관문 선택")
                    Row() {
                        CheckBoxRow(
                            text = "1~2",
                            value = checked1,
                            enabled = nowDifficulty != Difficulty.Hell,
                            onClick = { value ->
                                if (!value && !checked2 && !checked3) {
                                    return@CheckBoxRow
                                }
                                if (value && !checked2 && checked3) {
                                    checked2 = true
                                }
                                checked1 = value
                            })
                        CheckBoxRow(
                            text = "3~4",
                            value = checked2,
                            enabled = nowDifficulty != Difficulty.Hell,
                            onClick = { value ->
                                if (!checked1 && !value && !checked3) {
                                    return@CheckBoxRow
                                }
                                if (checked1 && !value && checked3) {
                                    return@CheckBoxRow
                                }
                                checked2 = value
                            })
                        CheckBoxRow(
                            text = "5~6",
                            value = checked3,
                            enabled = true,
                            onClick = { value ->
                                if (!checked1 && !checked2 && !value) {
                                    return@CheckBoxRow
                                }
                                if (checked1 && !checked2 && value) {
                                    checked2 = true
                                }
                                checked3 = value
                            })
                    }
                    Text(text = "${startGateNumber * 2 -1} ~ ${endGateNumber * 2} 관문")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        }

    }
}

@Composable
fun RaidSheetHeaderText(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun CheckBoxRow(
    text: String,
    value: Boolean,
    enabled: Boolean,
    onClick: (value: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = value,
            enabled = enabled,
            onCheckedChange = onClick
        )
        Text(
            text = text
        )
    }
}

@Preview
@Composable
fun AddRaidSheetPreview() {
    AddRaidSheet()
}