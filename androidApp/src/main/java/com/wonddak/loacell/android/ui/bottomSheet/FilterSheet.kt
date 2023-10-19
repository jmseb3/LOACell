package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.Filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val filter = totalRoomInfo.filter
    val userInfoList = totalRoomInfo.userInfoList
    val raidTypes = RaidType.values()
    BaseSheet(title = "필터 설정",
        useCloseIcon = true,
        onDismissRequest = { loaCellViewModel.hideDialog() }) {
        Column {
            FilterSection(section = "레이드 종류") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    content = {
                        items(raidTypes) { type ->
                            ElevatedFilterChip(
                                selected = filter.isSelected(type),
                                onClick = { loaCellViewModel.updateFilterRaidType(type) },
                                label = {
                                    Text(
                                        text = type.toKorString(),
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center
                                    )
                                },
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FilterSection("완료 여부") {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Filter.FINISH.values().forEach { finish ->
                        val selected = filter.isSelected(finish)

                        val updateFilter = {
                            loaCellViewModel.updateFilterFinish(finish)
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = selected,
                                    onClick = updateFilter
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = updateFilter,
                                colors = RadioButtonDefaults.colors()
                            )
                            Text(
                                text = finish.title,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .fillMaxWidth(),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
            FilterSection("특정 유저 포함") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    content = {
                        items(userInfoList) { userInfo ->
                            val name = userInfo.name
                            ElevatedFilterChip(
                                selected = filter.isSelected(name),
                                onClick = {
                                    loaCellViewModel.updateFilterUser(name)
                                },
                                label = {
                                    Text(
                                        text = name,
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center
                                    )
                                },
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FilterSection("캘린더 조절") {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf(60, 30, 15).forEach {
                            TextButton(
                                onClick = {
                                    loaCellViewModel.updateFilterTimeStep(it)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${it}분",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = filter.showEmptyCalendarRow,
                            onCheckedChange = { loaCellViewModel.updateFilterShowEmptyRow(it) }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "빈 행 보이기")
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "필터 초기화",
                    modifier = Modifier.noRippleClickable {
                        loaCellViewModel.clearFilter()
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FilterSection(
    section: String,
    content: @Composable () -> Unit
) {
    var show by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier.padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    show = !show
                }
                .padding(horizontal = 5.dp)
            )
            {
                Text(
                    text = section,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Icon(
                    painter = painterResource(id = SharedRes.images.arrow.drawableResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(if (show) 90f else 0f)
                        .align(Alignment.CenterEnd)
                )
            }
            if (show) {
                content()
            }
        }
    }
}