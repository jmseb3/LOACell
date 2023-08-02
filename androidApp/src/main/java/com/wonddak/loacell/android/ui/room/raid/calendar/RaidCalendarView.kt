package com.wonddak.loacell.android.ui.room.raid.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.database.model.Day
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.util.TimeHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaidCalendarView(
    timeStep: Int,
    timeSteps: List<Int>,
    showEmptyRow: Boolean,
    table: List<List<List<RaidInfo>>>,
    itemClick: (filterDay: List<RaidInfo>) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(timeStep) {
        listState.scrollToItem(0)
    }

    LazyColumn(state = listState) {
        stickyHeader {
            Column() {
                CalendarRow(
                    modifier = Modifier.background(Color.White),
                    timeText = "시간"
                ) { modifier, day ->
                    Text(
                        modifier = modifier,
                        text = day.text,
                        textAlign = TextAlign.Center
                    )
                }
                Divider()
            }
        }
        items(timeSteps) { totalMin ->
            val hour = totalMin / 60
            val minute = (totalMin % 60)
            val minuteIndex = (totalMin % 60) / timeStep
            val result = table[hour][minuteIndex]

            if (result.isEmpty() && !showEmptyRow) {
                return@items
            }
            CalendarRow(
                timeText = TimeHelper.makeTimeText(
                    hour,
                    minute,
                    timeStep
                )
            ) { modifier, day ->
                val filterDay = result.filter { it.day == day }.sortedBy { it.minute }
                val text = if (filterDay.isEmpty()) {
                    ""
                } else if (filterDay.size == 1) {
                    filterDay.first().title
                } else {
                    "${filterDay.first().title} 외 ${filterDay.size - 1}"
                }

                Text(
                    modifier = modifier.noRippleClickable {
                        itemClick(filterDay)
                    },
                    text = text,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )

            }
            Divider()

        }
    }

}

@Composable
fun CalendarRow(
    modifier: Modifier = Modifier,
    timeText: String,
    dayItem: @Composable (modifier: Modifier, day: Day) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeText,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
        Divider(
            modifier = Modifier
                .height(20.dp)
                .width(1.dp)
        )
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .weight(7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Day.MON.getList().forEach { day ->
                dayItem(Modifier.weight(1f), day)
                Divider(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                )
            }
        }
    }
}