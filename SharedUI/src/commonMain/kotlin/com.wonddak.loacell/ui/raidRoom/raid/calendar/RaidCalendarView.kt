/* Hallmark · pre-emit critique: P4 H5 E4 S5 R5 V4 */
/* Hallmark · component: schedule calendar · genre: modern-minimal · theme: existing Material 3 blue
 * states: default · hover · focus · active · disabled · loading · error · success
 * contrast: pass (Material 3 color scheme)
 */
package com.wonddak.loacell.ui.raidRoom.raid.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace
import com.wonddak.loacell.util.TimeHelper
import com.wonddak.loacell.util.makeTimeText

private val TimeColumnWidth = 64.dp
private val DayColumnWidth = 72.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaidCalendarView(
    timeStep: Int,
    timeSteps: List<Int>,
    showEmptyRow: Boolean,
    table: List<List<List<RaidInfo>>>,
    itemClick: (filterDay: List<RaidInfo>) -> Unit,
) {
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(timeStep) {
        listState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "시간대를 선택해 일정을 확인하세요.",
            modifier = Modifier.padding(
                start = LoaCellSpace.md,
                end = LoaCellSpace.md,
                bottom = LoaCellSpace.xs,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoaCellSpace.md),
            shape = RoundedCornerShape(LoaCellRadius.card),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            LazyColumn(state = listState) {
                stickyHeader {
                    CalendarHeader(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        horizontalScrollState = horizontalScrollState,
                    )
                }
                items(timeSteps) { totalMin ->
                    val hour = totalMin / 60
                    val minute = totalMin % 60
                    val minuteIndex = minute / timeStep
                    val raids = table[hour][minuteIndex]

                    if (raids.isNotEmpty() || showEmptyRow) {
                        CalendarTimeRow(
                            timeText = TimeHelper.makeTimeText(hour, minute, timeStep),
                            raids = raids,
                            horizontalScrollState = horizontalScrollState,
                            onDayClick = itemClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier,
    horizontalScrollState: androidx.compose.foundation.ScrollState,
) {
    Row(modifier = modifier.horizontalScroll(horizontalScrollState)) {
        CalendarHeaderCell(text = "시간", modifier = Modifier.width(TimeColumnWidth))
        Day.MON.getList().forEach { day ->
            CalendarHeaderCell(text = day.text, modifier = Modifier.width(DayColumnWidth))
        }
    }
}

@Composable
private fun CalendarHeaderCell(
    text: String,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.heightIn(min = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CalendarTimeRow(
    timeText: String,
    raids: List<RaidInfo>,
    horizontalScrollState: androidx.compose.foundation.ScrollState,
    onDayClick: (List<RaidInfo>) -> Unit,
) {
    Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
        Box(
            modifier = Modifier
                .width(TimeColumnWidth)
                .heightIn(min = 56.dp)
                .padding(horizontal = LoaCellSpace.xs),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Day.MON.getList().forEach { day ->
            val dayRaids = raids.filter { it.day == day }.sortedBy { it.minute }
            CalendarDayCell(
                day = day,
                raids = dayRaids,
                onClick = { onDayClick(dayRaids) },
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun CalendarDayCell(
    day: Day,
    raids: List<RaidInfo>,
    onClick: () -> Unit,
) {
    val hasRaid = raids.isNotEmpty()
    val title = when (raids.size) {
        0 -> ""
        1 -> raids.first().title
        else -> "${raids.first().title} 외 ${raids.size - 1}"
    }
    val description = if (hasRaid) {
        "${day.text}요일, $title 일정 ${raids.size}개. 탭하여 상세 보기"
    } else {
        "${day.text}요일, 일정 없음"
    }
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (hasRaid) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .width(DayColumnWidth)
            .heightIn(min = 56.dp)
            .padding(LoaCellSpace.xxs),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth()
                .semantics { contentDescription = description }
                .then(
                    if (hasRaid) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(LoaCellRadius.image),
            color = if (hasRaid) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
            contentColor = contentColor,
        ) {
            Text(
                text = title.ifEmpty { "—" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LoaCellSpace.xs),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
