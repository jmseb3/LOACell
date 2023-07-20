package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.database.AppDataBase
import com.wonddak.database.model.Day
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.bottomSheet.FilterSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.RoomType
import java.text.DecimalFormat

@Composable
fun RaidView(
    db: AppDataBase, roomId: String, loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()

    val raidInfoList = totalRoomInfo.raidInfoList
    val userInfoList = totalRoomInfo.userInfoList
    val filter by loaCellViewModel.filter.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

    var showType by remember {
        mutableStateOf(RoomType.Default)
    }

    Box() {
        Column(modifier = Modifier.fillMaxSize()) {
            Row() {
                MyIconButton(imageResource = SharedRes.images.calendar) {
                    showType = if (showType == RoomType.Default) {
                        RoomType.Calendar
                    } else {
                        RoomType.Default
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { loaCellViewModel.showRaidFilter = true }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = SharedRes.images.filter.drawableResId),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Filter",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Divider()
            RaidTypeView(
                type = showType,
                filterRaidInfoList = filter.filterList(raidInfoList, userInfoList, db),
                loaCellViewModel = loaCellViewModel
            )
        }
        if (focusRaidId.isNotEmpty()) {
            FocusRaidView(db, roomId, loaCellViewModel)
        }
        loaCellViewModel.apply {
            if (showRaidFilter) {
                FilterSheet(loaCellViewModel = loaCellViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaidTypeView(
    type: RoomType,
    filterRaidInfoList: List<RaidInfo>,
    loaCellViewModel: LoaCellViewModel
) {
    val timeStep = 30
    val timeSteps = (0 until (1440 / timeStep)).map { it * timeStep }

    val table = MutableList(24) { MutableList(60 / timeStep) { mutableListOf<RaidInfo>() } }

    filterRaidInfoList.forEach {
        if (it.day != Day.NONE) {
            table[it.hour.toInt()][(it.minute / timeStep).toInt()].add(it)
        }
    }

    if (type == RoomType.Default) {
        LazyColumn(
            modifier = Modifier.padding(10.dp)
        ) {
            items(filterRaidInfoList) { raidInfo ->
                RaidItemRow(raidInfo) {
                    loaCellViewModel.setNowRaidInfo(raidInfo.raidId)
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    } else {

        Box() {
            LazyColumn() {
                stickyHeader {
                    Column() {
                        CalendarRow(
                            modifier = Modifier.background(Color.White),
                            timeText = "시간"
                        ) { modifier ,day ->
                            Text(
                                modifier = modifier,
                                text = day.text,
                                textAlign = TextAlign.Center
                            )
                        }
                        Divider()
                    }
                }
                fun makeTimeText(
                    hour :Int,
                    minute :Int,
                    step :Int
                ):String {
                    val df = DecimalFormat("00")
                    val rs1 = "${df.format(hour)} : ${df.format(minute)}"
                    val timeTotal = hour * 60 + minute + step
                    val newHour = timeTotal / 60
                    val newMinute = timeTotal % 60
                    val rs2 = "${df.format(newHour)} : ${df.format(newMinute)}"
                    return  "$rs1\n~\n$rs2"

                }
                items(timeSteps) { totalMin ->
                    val hour = totalMin / 60
                    val minute = (totalMin % 60)
                    val minuteIndex = (totalMin % 60) / timeStep
                    val result = table[hour][minuteIndex]

                    CalendarRow(timeText = makeTimeText(hour,minute,timeStep)) { modifier,day ->
                        val filterDay = result.filter { it.day == day }.sortedBy { it.minute }
                        val text = if (filterDay.isEmpty()) {
                            ""
                        } else if (filterDay.size == 1) {
                            filterDay.first().title
                        } else {
                            "${filterDay.first().title} 외 ${filterDay.size - 1}"
                        }
                        Text(
                            modifier = modifier,
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                    Divider()

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "",
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Day.MON.getList().forEach { day ->
                        Text("",Modifier.weight(1f))
                        if (day.index <6) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxHeight()  //fill the max height
                                    .width(1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarRow(
    modifier: Modifier = Modifier,
    timeText: String,
    dayItem: @Composable (modifier: Modifier,day: Day) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeText,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Day.MON.getList().forEach { day ->
                dayItem(Modifier.weight(1f),day)
            }
        }
    }
}