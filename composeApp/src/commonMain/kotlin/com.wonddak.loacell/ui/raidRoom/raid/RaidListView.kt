package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wonddak.loacell.android.ui.modal.bottomSheet.FilterSheet
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomType
import com.wonddak.loacell.rememberDataModalStatus
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.ui.modal.dialog.SelectIdDialog
import com.wonddak.loacell.ui.raidRoom.raid.calendar.RaidCalendarView
import com.wonddak.loacell.viewModel.RaidViewModel
import io.github.aakira.napier.Napier
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.calendar
import loacell.composeapp.generated.resources.filter
import loacell.composeapp.generated.resources.task_finish_done
import loacell.composeapp.generated.resources.task_finish_not
import org.jetbrains.compose.resources.painterResource


@Composable
fun RaidListView(
    raidViewModel: RaidViewModel,
    navigation: (RaidInfo) -> Unit,
) {
    val filterSheetStatus = rememberModalStatus()
    val calendarIdStatus = rememberDataModalStatus<List<RaidInfo>>()

    val raidList = raidViewModel.raidList
    val userList = raidViewModel.userList
    val filter: Filter by raidViewModel.filter.collectAsState()
    val showType: RoomType = raidViewModel.showType

    Column(modifier = Modifier.fillMaxSize()) {
        Row() {
            IconButton(
                {
                    raidViewModel.showType = if (showType == RoomType.Default) {
                        RoomType.Calendar
                    } else {
                        RoomType.Default
                    }
                }
            ) {
                Icon(painter = painterResource(Res.drawable.calendar), null)
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = {
                    filterSheetStatus.show()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.filter),
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
        Column {
            val filterList = filter.makeFilterList(raidList, userList)
            if (showType == RoomType.Default) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    items(filterList) { raidInfo ->
                        RaidItemRow(
                            Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                            raidInfo
                        ) {
                            navigation(raidInfo)
                        }
                    }
                }
            } else {
                RaidCalendarView(
                    filter.timeStep,
                    filter.timeSteps,
                    filter.showEmptyCalendarRow,
                    filter.makeTable(filterList)
                ) { filterDay ->
                    when {
                        filterDay.isEmpty() -> {

                        }

                        filterDay.size == 1 -> {
                            navigation(filterDay[0])
                        }

                        else -> {
                            calendarIdStatus.subItem = filterDay
                            calendarIdStatus.show()
                        }
                    }
                }
            }
        }

    }
    FilterSheet(
        filterSheetStatus,
        filter,
        userList
    ) {
        raidViewModel.updateFilter(it)
    }
    SelectIdDialog(calendarIdStatus) {
        navigation(it)
    }
}

@Composable
fun RaidItemRow(
    modifier: Modifier = Modifier,
    raidInfo: RaidInfo,
    onClick: () -> Unit,
) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(size)
            .clickable { onClick() },
        shape = rShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(raidInfo.getImage())
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator(
                            modifier
                                .align(Alignment.Center)
                                .size(size / 2)
                        )
                    },
                    error = {

                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .clip(rShape),
                    onError = {
                        Napier.e(tag = "JWH") { it.result.toString() }
                    }
                )
                Column(
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = raidInfo.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = raidInfo.getRaidText())
                    Text(text = raidInfo.makeGateText())
                }
            }
            FinishButton(
                raidInfo,
                Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun FinishButton(
    raidInfo: RaidInfo,
    modifier: Modifier = Modifier,
) {
    val icon = if (raidInfo.isFinish) {
        Res.drawable.task_finish_done
    } else {
        Res.drawable.task_finish_not
    }
    IconButton(
        modifier = modifier.size(30.dp),
        onClick = {
            CommonRaidHelper.updateFinish(raidInfo)
        }
    ) {
        Icon(
            painter = painterResource(icon),
            null
        )
    }
}