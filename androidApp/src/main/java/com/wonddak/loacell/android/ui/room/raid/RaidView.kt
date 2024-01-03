package com.wonddak.loacell.android.ui.room.raid

import android.graphics.Picture
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.dialog.SelectIdDialog
import com.wonddak.loacell.android.ui.room.raid.calendar.RaidCalendarView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomType

@Composable
fun RaidView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val focusRaidId = totalRoomInfo.focusRaidId

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
                OutlinedButton(onClick = { loaCellViewModel.showDialog(DialogStatus.RAID_FILTER) }) {
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
                loaCellViewModel = loaCellViewModel,
                type = showType
            )
        }
        if (focusRaidId.isNotEmpty()) {
            RaidFocusView(loaCellViewModel)
        }
    }
}

@Composable
fun RaidTypeView(
    loaCellViewModel: LoaCellViewModel,
    type: RoomType
) {

    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val filter = totalRoomInfo.filter

    val timeStep = filter.timeStep
    val showEmptyRow: Boolean = filter.showEmptyCalendarRow
    val filterRaidInfoList = totalRoomInfo.filterList

    var showCalendarView: List<RaidInfo>? by remember {
        mutableStateOf(null)
    }
    val picture : Picture = Picture()

    if (type == RoomType.Default) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .drawWithCache {
                    // Example that shows how to redirect rendering to an Android Picture and then
                    // draw the picture into the original destination
                    val width = this.size.width.toInt()
                    val height = this.size.height.toInt()

                    onDrawWithContent {
                        val pictureCanvas =
                            androidx.compose.ui.graphics.Canvas(
                                picture.beginRecording(
                                    width,
                                    height
                                )
                            )
                        // requires at least 1.6.0-alpha01+
//                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
//                            this@onDrawWithContent.drawContent()
//                        }
                        picture.endRecording()

                        drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                    }
                }
        ) {
            items(filterRaidInfoList) { raidInfo ->
                RaidItemRow(raidInfo) {
                    loaCellViewModel.setNowRaidInfo(raidInfo.raidId)
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    } else {
        RaidCalendarView(
            timeStep = timeStep,
            timeSteps = filter.timeSteps,
            showEmptyRow = showEmptyRow,
            table = filter.makeTable(filterRaidInfoList)
        ) { filterDay ->
            if (filterDay.isEmpty()) {

            } else if (filterDay.size == 1) {
                loaCellViewModel.setNowRaidInfo(filterDay[0].raidId)
            } else {
                showCalendarView = filterDay
            }
        }
    }
    showCalendarView?.let { items ->
        SelectIdDialog(
            items,
            { showCalendarView = null }
        ) {
            loaCellViewModel.setNowRaidInfo(it.raidId)
            showCalendarView = null
        }
    }
}