package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.getDayText
import com.wonddak.loacell.model.getImage
import com.wonddak.loacell.model.getRaidText
import com.wonddak.loacell.model.makeGateText
import com.wonddak.loacell.model.RoomType
import com.wonddak.loacell.rememberDataModalStatus
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace
import com.wonddak.loacell.ui.modal.dialog.SelectIdDialog
import com.wonddak.loacell.ui.modal.sheet.FilterSheet
import com.wonddak.loacell.ui.raidRoom.raid.calendar.RaidCalendarView
import com.wonddak.loacell.viewModel.RaidViewModel
import io.github.aakira.napier.Napier
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.calendar
import loacell.sharedui.generated.resources.filter
import loacell.sharedui.generated.resources.task_finish_done
import loacell.sharedui.generated.resources.task_finish_not
import org.jetbrains.compose.resources.painterResource

@Composable
fun RaidListView(
    raidViewModel: RaidViewModel,
    navigation: (RaidInfo) -> Unit,
) {
    val filterSheetStatus = rememberModalStatus()
    val calendarIdStatus = rememberDataModalStatus<List<RaidInfo>>()
    val filter: Filter by raidViewModel.filter.collectAsState()
    val showType: RoomType = raidViewModel.showType
    val filterList = filter.makeFilterList(raidViewModel.raidList, raidViewModel.userList)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoaCellSpace.md, vertical = LoaCellSpace.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("레이드 일정", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "목록을 확인하고 필요한 일정만 골라보세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            FilledTonalButton(
                onClick = {
                    raidViewModel.showType = if (showType == RoomType.Default) {
                        RoomType.Calendar
                    } else {
                        RoomType.Default
                    }
                },
            ) {
                Icon(painterResource(Res.drawable.calendar), contentDescription = null)
                Spacer(Modifier.width(LoaCellSpace.xs))
                Text(if (showType == RoomType.Default) "캘린더" else "목록")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoaCellSpace.md)
                .padding(bottom = LoaCellSpace.xs),
            horizontalArrangement = Arrangement.End,
        ) {
            OutlinedButton(onClick = filterSheetStatus::show) {
                Icon(
                    painter = painterResource(Res.drawable.filter),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(LoaCellSpace.xs))
                Text("필터")
            }
        }

        if (showType == RoomType.Default) {
            if (filterList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(LoaCellSpace.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "표시할 레이드 일정이 없습니다.\n필터를 조정하거나 새 일정을 추가해 보세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = LoaCellSpace.md,
                        end = LoaCellSpace.md,
                        top = LoaCellSpace.xs,
                        bottom = LoaCellSpace.lg,
                    ),
                    verticalArrangement = Arrangement.spacedBy(LoaCellSpace.xs),
                ) {
                    items(filterList) { raidInfo ->
                        RaidItemRow(
                            raidInfo = raidInfo,
                            onToggleFinish = { raidViewModel.toggleRaidFinish(raidInfo) },
                            onClick = { navigation(raidInfo) },
                        )
                    }
                }
            }
        } else {
            RaidCalendarView(
                filter.timeStep,
                filter.timeSteps,
                filter.showEmptyCalendarRow,
                filter.makeTable(filterList),
            ) { filterDay ->
                when {
                    filterDay.isEmpty() -> Unit
                    filterDay.size == 1 -> navigation(filterDay.first())
                    else -> {
                        calendarIdStatus.subItem = filterDay
                        calendarIdStatus.show()
                    }
                }
            }
        }
    }

    FilterSheet(filterSheetStatus, filter, raidViewModel.userList, raidViewModel::updateFilter)
    SelectIdDialog(calendarIdStatus, navigation)
}

@Composable
fun RaidItemRow(
    modifier: Modifier = Modifier,
    raidInfo: RaidInfo,
    onToggleFinish: () -> Unit,
    onClick: () -> Unit,
) {
    val imageSize = 88.dp
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(LoaCellRadius.card),
        colors = CardDefaults.cardColors(
            containerColor = if (raidInfo.isFinish) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSize + LoaCellSpace.lg)
                .padding(LoaCellSpace.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(raidInfo.getImage())
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).size(imageSize / 2),
                        )
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(LoaCellRadius.image)),
                    onError = { Napier.e(tag = "JWH") { it.result.toString() } },
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = LoaCellSpace.sm, end = LoaCellSpace.lg),
                    verticalArrangement = Arrangement.spacedBy(LoaCellSpace.xxs),
                ) {
                    Text(
                        text = raidInfo.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = raidInfo.getRaidText(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (raidInfo.day != Day.NONE) {
                        Surface(
                            shape = RoundedCornerShape(LoaCellRadius.image),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = LoaCellSpace.xs, vertical = LoaCellSpace.xxs),
                                text = "${raidInfo.getDayText()} · ${raidInfo.makeGateText()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            FinishButton(
                raidInfo = raidInfo,
                modifier = Modifier.align(Alignment.BottomEnd),
                onToggleFinish = onToggleFinish,
            )
        }
    }
}

@Composable
fun FinishButton(
    raidInfo: RaidInfo,
    modifier: Modifier = Modifier,
    onToggleFinish: () -> Unit,
) {
    val icon = if (raidInfo.isFinish) Res.drawable.task_finish_done else Res.drawable.task_finish_not
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onToggleFinish,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = if (raidInfo.isFinish) "완료 취소" else "완료로 표시",
            tint = if (raidInfo.isFinish) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
