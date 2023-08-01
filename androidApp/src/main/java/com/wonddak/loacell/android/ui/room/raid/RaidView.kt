package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.database.model.Day
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.bottomSheet.FilterSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.room.raid.calendar.RaidCalendarView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.RoomType

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
                timeStep = filter.timeStep,
                showEmptyRow = filter.showEmptyCalendarRow,
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

@Composable
fun RaidTypeView(
    type: RoomType,
    timeStep :Int,
    showEmptyRow :Boolean,
    filterRaidInfoList: List<RaidInfo>,
    loaCellViewModel: LoaCellViewModel
) {
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
        RaidCalendarView(timeStep = timeStep, timeSteps = timeSteps, showEmptyRow = showEmptyRow,table = table)
    }
}