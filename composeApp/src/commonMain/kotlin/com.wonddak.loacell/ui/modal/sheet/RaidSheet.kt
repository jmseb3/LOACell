package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.ui.common.LengthLimitTextField

@Composable
fun AddRaidSheet(
    modalStatus: ModalStatus,
    onAdd: (RaidInfo) -> Unit,
) {
    RaidSheetBase(
        modalStatus = modalStatus,
        raidInfo = null,
        title = Sheet.RAID_ADD.title,
        buttonText = "추가",
        buttonAction = onAdd
    )
}

@Composable
fun EditRaidSheet(
    modalStatus: ModalStatus,
    raidInfo: RaidInfo? = null,
    onEdit: (RaidInfo) -> Unit,
) {
    RaidSheetBase(
        modalStatus = modalStatus,
        raidInfo = raidInfo,
        title = Sheet.RAID_EDIT.title,
        buttonText = "수정",
        buttonAction = onEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidSheetBase(
    modalStatus: ModalStatus,
    raidInfo: RaidInfo?,
    title: String,
    buttonText: String,
    buttonAction: (RaidInfo) -> Unit,
) {
    var raidInfoData: RaidInfo by remember {
        mutableStateOf(
            raidInfo ?: RaidInfo()
        )
    }
    var expanded by remember { mutableStateOf(false) }
    val textFieldModifier = Modifier.fillMaxWidth()

    val showDayUse by remember {
        derivedStateOf { raidInfoData.day != Day.NONE }
    }

    BaseSheet(
        modalStatus = modalStatus,
        title = title,
        buttonText = buttonText,
        enabledButton = raidInfoData.title.isNotEmpty() && (if (showDayUse) raidInfoData.day != Day.NONE else true),
        buttonClickAction = {
            buttonAction(raidInfoData)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LengthLimitTextField(modifier = textFieldModifier.padding(10.dp),
                text = raidInfoData.title,
                label = "제목",
                placeHolder = "제목을 입력하세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                textChange = {
                    raidInfoData = raidInfoData.copy(title = it)
                })
            //타입
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = {
//                    expanded = !expanded
//                },
//            ) {
//                OutlinedTextField(modifier = textFieldModifier
//                    .menuAnchor()
//                    .padding(horizontal = 10.dp),
//                    value = fbRaidInfo.type.toKorString(),
//                    onValueChange = {},
//                    readOnly = true,
//                    label = {
//                        Text(text = "레이드 정보 선택")
//                    },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })
//
//                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                    RaidType.entries.forEach { item ->
//                        DropdownMenuItem(text = {
//                            Text(
//                                text = item.toKorString(),
//                                fontWeight = if (fbRaidInfo.type == item) FontWeight.Bold else FontWeight.Normal
//                            )
//                        }, onClick = {
//                            update(fbRaidInfo.updateType(item))
//                            expanded = false
//                        })
//                    }
//                }
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp)
//            ) {
//                RaidSheetHeaderText(text = "난이도 선택")
//                Column {
//                    DifficultyRow(
//                        fbRaidInfo = fbRaidInfo, difficultyList = listOf(
//                            Difficulty.Normal, Difficulty.Hard, Difficulty.Hell
//                        )
//                    ) { difficulty ->
//                        update(fbRaidInfo.updateDifficulty(difficulty))
//                    }
//                    if (Const.useExtreme) {
//                        DifficultyRow(
//                            fbRaidInfo = fbRaidInfo, difficultyList = listOf(
//                                Difficulty.ExtremeNormal, Difficulty.ExtremeHard
//                            )
//                        ) { difficulty ->
//                            update(fbRaidInfo.updateDifficulty(difficulty))
//                        }
//                    }
//                }
//
//            }
//
//            AnimatedVisibility(visible = (fbRaidInfo.type == RaidType.ABRELSHUD) && fbRaidInfo.difficulty != Difficulty.Hell) {
//                var abStart by remember { mutableIntStateOf(1) }
//                var abEnd by remember { mutableIntStateOf(1) }
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 10.dp),
//                ) {
//                    RaidSheetHeaderText(text = "관문 선택")
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        listOf(1, 2, 3, 4).forEach { idx ->
//                            CheckBoxRow(modifier = Modifier.weight(1f),
//                                text = idx.toString(),
//                                value = idx in abStart..abEnd,
//                                enabled = true,
//                                onClick = { value ->
//                                    if (value) {
//                                        abStart = min(abStart, idx)
//                                        abEnd = abEnd.coerceAtLeast(idx)
//                                    } else {
//                                        if (abStart == idx) {
//                                            abStart = idx + 1
//                                        } else if (abEnd == idx) {
//                                            abEnd = idx - 1
//                                        }
//                                        if (abEnd < abStart) {
//                                            abStart = 1
//                                            abEnd = 1
//                                        }
//                                    }
//                                    update(fbRaidInfo.updateGate(abStart, abEnd))
//                                })
//                        }
//                    }
//                }
//            }

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                ) {
//                    RaidSheetHeaderText(text = "입장 레벨")
//                    Text(text = raidInfo.getMinLevelText())
//                }
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                ) {
//                    RaidSheetHeaderText(text = "입장 인원")
//                    Text(text = fbRaidInfo.type.maxPerson.toString())
//                }
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                CheckBoxRow(modifier = Modifier,
//                    text = "일정 지정",
//                    value = showDayUse,
//                    enabled = true,
//                    onClick = {
//                        showDayUse = it
//                        if (!showDayUse) {
//                            update(fbRaidInfo.copy(day = Day.NONE, hour = 0, minute = 0))
//                        }
//                    })
//                Spacer(modifier = Modifier.weight(1f))
//            }
//            AnimatedVisibility(showDayUse) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 10.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    WheelTimePicker(
//                        modifier = Modifier.weight(2f),
//                        startTime = LocalTime.of(fbRaidInfo.hour.toInt(), fbRaidInfo.minute.toInt())
//                    ) { time ->
//                        update(
//                            fbRaidInfo.updateTime(time.hour.toLong(), time.minute.toLong())
//                        )
//                    }
//                    val days = Day.entries
//                    val weight = Modifier.weight(1f)
//
//                    Column(
//                        modifier = Modifier
//                            .padding(horizontal = 10.dp)
//                            .weight(3f),
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        val updateAction = { day: Day ->
//                            update(
//                                if (fbRaidInfo.day == day) {
//                                    fbRaidInfo.updateDay(day = Day.NONE)
//                                } else {
//                                    fbRaidInfo.updateDay(day = day)
//                                }
//                            )
//                        }
//                        Row() {
//                            days.subList(1, 5).forEach {
//                                DayButton(
//                                    day = it, modifier = weight, selected = fbRaidInfo.day == it
//                                ) {
//                                    updateAction(it)
//                                }
//                            }
//                        }
//                        Row() {
//                            days.subList(5, 8).forEach {
//                                DayButton(
//                                    day = it, modifier = weight, selected = fbRaidInfo.day == it
//                                ) {
//                                    updateAction(it)
//                                }
//                            }
//                            Spacer(modifier = weight)
//                        }
//                    }
//                }
//
//            }
        }
    }
}

@Composable
fun DayButton(
    day: Day, modifier: Modifier, selected: Boolean, update: () -> Unit,
) {
    val color = if (selected) Color.Black else Color.Transparent
    TextButton(
        modifier = modifier.border(BorderStroke(1.dp, color), shape = RoundedCornerShape(8.dp)),
        onClick = update
    ) {
        Text(
            text = day.text, textAlign = TextAlign.Center, color = Color.Black
        )
    }
}

@Composable
fun RaidSheetHeaderText(text: String) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

//@Composable
//fun DifficultyRow(
//    fbRaidInfo: FBRaidInfo, difficultyList: List<Difficulty>, update: (Difficulty) -> Unit,
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        difficultyList.forEach { difficulty ->
//            val selected = fbRaidInfo.difficultySelected(difficulty)
//            val enabled = fbRaidInfo.difficultyEnabled(difficulty)
//            Row(
//                modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically
//            ) {
//                RadioButton(
//                    selected = selected,
//                    enabled = enabled,
//                    onClick = { update(difficulty) },
//                    colors = RadioButtonDefaults.colors()
//                )
//                Text(
//                    text = difficulty.toKorString(),
//                    modifier = Modifier
//                        .padding(start = 6.dp)
//                        .fillMaxWidth()
//                        .clickable(enabled = enabled) {
//                            update(difficulty)
//                        },
//                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
//                    color = if (enabled) Color.Black else Color.Gray
//                )
//            }
//        }
//    }
//}