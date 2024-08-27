package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.RaidInfo

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

