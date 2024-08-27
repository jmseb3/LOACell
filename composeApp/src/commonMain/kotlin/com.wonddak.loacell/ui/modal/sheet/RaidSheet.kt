package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.Sheet

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
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
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