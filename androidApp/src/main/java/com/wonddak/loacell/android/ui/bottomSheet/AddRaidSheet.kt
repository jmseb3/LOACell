package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.wonddak.database.model.Day
import com.wonddak.database.model.Difficulty
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.android.ui.common.CheckBoxRow
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.FBRaidInfo
import java.time.LocalTime

@Composable
fun AddRaidSheet(
    roomId: String,
    onDismissRequest: () -> Unit,
    successAction: () -> Unit
) {
    var fbRaidInfo: FBRaidInfo by remember {
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
    RaidSheetBase(
        fbRaidInfo = fbRaidInfo,
        title = "레이드 정보 추가",
        buttonText = "추가",
        update = {
            fbRaidInfo = it
        },
        onDismissRequest = onDismissRequest
    ) {
        CommonRaidHelper.add(
            roomId,
            fbRaidInfo,
            { e -> },
            successAction
        )
    }
}

@Composable
fun EditRaidSheet(
    raidInfo: RaidInfo,
    onDismissRequest: () -> Unit,
    successAction: () -> Unit
) {
    var fbRaidInfo: FBRaidInfo by remember {
        mutableStateOf(
            FBRaidInfo(
                title = raidInfo.title,
                type = raidInfo.type,
                difficulty = raidInfo.Difficulty,
                startGateNumber = raidInfo.startGateNumber.toInt(),
                endGateNumber = raidInfo.endGateNumber.toInt(),
                isFinish = raidInfo.isFinish,
                party1 = raidInfo.party1characterList,
                party2 = raidInfo.party2characterList,
                day = raidInfo.day,
                hour = raidInfo.hour,
                minute = raidInfo.minute,
            )
        )
    }
    RaidSheetBase(
        fbRaidInfo = fbRaidInfo,
        title = "레이드 정보 수정",
        buttonText = "수정",
        update = {
            fbRaidInfo = it
        },
        onDismissRequest = onDismissRequest
    ) {
        println(fbRaidInfo.toString())
        CommonRaidHelper.update(
            raidInfo.roomId,
            raidInfo.raidId,
            fbRaidInfo,
            { e -> },
            successAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidSheetBase(
    fbRaidInfo: FBRaidInfo,
    title: String,
    buttonText: String,
    update: (fbRaidInfo: FBRaidInfo) -> Unit,
    onDismissRequest: () -> Unit,
    buttonAction: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val radioOptions = Difficulty.values()
    val textFieldModifier = Modifier.fillMaxWidth()
    var showDayUse by remember { mutableStateOf(fbRaidInfo.day != Day.NONE) }
    LaunchedEffect(fbRaidInfo) {
        if (!fbRaidInfo.type.accessibleDifficulty().contains(fbRaidInfo.difficulty)) {
            update(fbRaidInfo.copy(difficulty = Difficulty.Normal))
        }
        if (fbRaidInfo.type != RaidType.ABRELSHUD) {
            val maxGate = fbRaidInfo.type.getMaxGate()
            update(fbRaidInfo.copy(startGateNumber = 1, endGateNumber = maxGate))
        }
    }
    LaunchedEffect(showDayUse) {
        if (!showDayUse) {
            update(fbRaidInfo.copy(day = Day.NONE, hour = 0, minute = 0))
        }
    }

    BaseSheet(
        title = title,
        buttonText = buttonText,
        onDismissRequest = onDismissRequest,
        enabledButton = fbRaidInfo.title.isNotEmpty() && (if (showDayUse) fbRaidInfo.day != Day.NONE else true),
        buttonClickAction = buttonAction,
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
                    update(fbRaidInfo.copy(title = it))
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
                                update(fbRaidInfo.copy(type = item))
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

                        val updateAction = {
                            update(fbRaidInfo.copy(difficulty = difficulty))
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = selected,
                                    onClick = updateAction
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                enabled = enabled,
                                onClick = updateAction,
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
                val updateAction = { start: Int, end: Int ->
                    update(fbRaidInfo.copy(startGateNumber = start, endGateNumber = end))
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
                    updateAction(startGateNumber, endGateNumber)
                }
                LaunchedEffect(fbRaidInfo.difficulty) {
                    if (fbRaidInfo.difficulty == Difficulty.Hell) {
                        checked1 = false
                        checked2 = false
                        checked3 = true
                        updateAction(3, 3)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                CheckBoxRow(
                    modifier = Modifier,
                    text = "일정 지정",
                    value = showDayUse,
                    enabled = true,
                    onClick = {
                        showDayUse = it
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            AnimatedVisibility(showDayUse) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelTimePicker(
                        modifier = Modifier.weight(2f),
                        startTime = LocalTime.of(fbRaidInfo.hour.toInt(), fbRaidInfo.minute.toInt())
                    ) { time ->
                        update(
                            fbRaidInfo.copy(
                                hour = time.hour.toLong(),
                                minute = time.minute.toLong()
                            )
                        )
                    }
                    val days = Day.values().toList()
                    val weight = Modifier.weight(1f)

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(3f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val updateAction = { day: Day ->
                            update(
                                if (fbRaidInfo.day == day) {
                                    fbRaidInfo.copy(day = Day.NONE)
                                } else {
                                    fbRaidInfo.copy(day = day)
                                }
                            )
                        }
                        Row() {
                            days.subList(1, 5).forEach {
                                DayButton(
                                    day = it,
                                    modifier = weight,
                                    selected = fbRaidInfo.day == it
                                ) {
                                    updateAction(it)
                                }
                            }
                        }
                        Row() {
                            days.subList(5, 8).forEach {
                                DayButton(
                                    day = it,
                                    modifier = weight,
                                    selected = fbRaidInfo.day == it
                                ) {
                                    updateAction(it)
                                }
                            }
                            Spacer(modifier = weight)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun DayButton(
    day: Day,
    modifier: Modifier,
    selected: Boolean,
    update: () -> Unit
) {
    val color = if (selected) Color.Black else Color.Transparent
    TextButton(
        modifier = modifier.border(BorderStroke(1.dp, color), shape = RoundedCornerShape(8.dp)),
        onClick = update
    ) {
        Text(
            text = day.text,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
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
