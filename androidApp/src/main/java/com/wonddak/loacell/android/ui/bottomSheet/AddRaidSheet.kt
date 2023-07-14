package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.database.model.Day
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.android.ui.common.CheckBoxRow
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.FBRaidInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRaidSheet(
    roomId: String,
    onDismissRequest: () -> Unit,
    successAction: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var fbRaidInfo :FBRaidInfo by remember {
        mutableStateOf(
            FBRaidInfo(
                title = "",
                type = RaidType.VALTAN,
                difficulty = Difficulty.Normal,
                startGateNumber = 1,
                endGateNumber = 1,
                day = Day.NONE,
                hour = 0,
                minute = 0,
            )
        )
    }
    LaunchedEffect(fbRaidInfo) {
        if (!fbRaidInfo.type.accessibleDifficulty().contains(fbRaidInfo.difficulty)) {
            fbRaidInfo = fbRaidInfo.copy(difficulty = Difficulty.Normal)
        }
        if (fbRaidInfo.type != RaidType.ABRELSHUD) {
            val maxGate = fbRaidInfo.type.getMaxGate()
            fbRaidInfo = fbRaidInfo.copy(startGateNumber = 1, endGateNumber = maxGate)
        }
    }
    val radioOptions = Difficulty.values()
    val textFieldModifier = Modifier.fillMaxWidth()
    var errorMsg by remember { mutableStateOf("") }
    var showDayUse by remember {mutableStateOf(false)}

    BaseSheet(
        title = "레이드 정보 추가",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            if (fbRaidInfo.title.isNotEmpty()) {
                CommonRaidHelper.add(
                    roomId,
                    fbRaidInfo,
                    {e -> errorMsg = e.errorMsg},
                    successAction
                )
            } else {
                errorMsg = "제목을 입력해주세요."
            }
        },
        errorMsg = errorMsg,
        updateErrorMsg = { errorMsg = it }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LengthLimitTextField(
                modifier = textFieldModifier.padding(10.dp),
                text = fbRaidInfo.title,
                label = "제목",
                placeHolder = "제목을 입력하세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                textChange = {
                   fbRaidInfo = fbRaidInfo.copy(title = it)
                }
            )
            //타입
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    modifier = textFieldModifier
                        .menuAnchor()
                        .padding(horizontal = 10.dp),
                    value = fbRaidInfo.type.toKorString(),
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
                                    fontWeight = if (fbRaidInfo.type == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                fbRaidInfo = fbRaidInfo.copy(type = item)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                RaidSheetHeaderText(text = "난이도 선택")
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    radioOptions.forEach { difficulty ->
                        val selected = difficulty == fbRaidInfo.difficulty
                        val enabled = fbRaidInfo.type.accessibleDifficulty().contains(difficulty)

                        val update = {
                            fbRaidInfo = fbRaidInfo.copy(difficulty = difficulty)
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = selected,
                                    onClick = update
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                enabled = enabled,
                                onClick = update,
                                colors = RadioButtonDefaults.colors()
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
            }

            AnimatedVisibility(visible = fbRaidInfo.type == RaidType.ABRELSHUD) {
                var checked1 by remember { mutableStateOf(true) }
                var checked2 by remember { mutableStateOf(false) }
                var checked3 by remember { mutableStateOf(false) }
                val update = { start: Int, end: Int ->
                    fbRaidInfo = fbRaidInfo.copy(startGateNumber = start, endGateNumber = end)

                }
                LaunchedEffect(checked1, checked2, checked3) {
                    val endGateNumber = if (checked3) {
                        3
                    } else if (checked2) {
                        2
                    } else {
                        1
                    }
                    val startGateNumber = if (checked1) {
                        1
                    } else if (checked2) {
                        2
                    } else {
                        3
                    }
                    update(startGateNumber,endGateNumber)
                }
                LaunchedEffect(fbRaidInfo.difficulty) {
                    if (fbRaidInfo.difficulty == Difficulty.Hell) {
                        checked1 = false
                        checked2 = false
                        checked3 = true
                        update(3,3)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    RaidSheetHeaderText(text = "관문 선택")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CheckBoxRow(
                            modifier = Modifier.weight(1f),
                            text = "1~2",
                            value = checked1,
                            enabled = fbRaidInfo.difficulty != Difficulty.Hell,
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
                            modifier = Modifier.weight(1f),
                            text = "3~4",
                            value = checked2,
                            enabled = fbRaidInfo.difficulty != Difficulty.Hell,
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
                            modifier = Modifier.weight(1f),
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
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    RaidSheetHeaderText(text = "입장 레벨")
                    Text(text = fbRaidInfo.getMinLevelText())
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    RaidSheetHeaderText(text = "입장 인원")
                    Text(text = fbRaidInfo.type.maxPerson.toString())
                }
            }
            CheckBoxRow(
                modifier = Modifier.fillMaxWidth(1f),
                text = "일정 지정",
                value = showDayUse,
                enabled = true,
                onClick = {
                    showDayUse = it
                }
            )
            AnimatedVisibility(showDayUse) {
                Row() {
                    Day.values().forEach {
                        if (it.index >= 0) {
                            Text(text = it.text)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RaidSheetHeaderText(text: String) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
