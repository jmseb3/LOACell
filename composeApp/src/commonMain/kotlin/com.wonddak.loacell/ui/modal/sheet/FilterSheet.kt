package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.noRippleClickable
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.arrow
import org.jetbrains.compose.resources.painterResource

@Composable
fun FilterSheet(
    modalStatus: ModalStatus,
    filter: Filter,
    userInfoList: List<UserInfo>,
    updateFilter: (Filter) -> Unit,
) {
    BaseSheet(
        modalStatus = modalStatus,
        title = Sheet.RAID_FILTER.title,
        useCloseIcon = true,
    ) {
        Column {
            FilterSection(section = "레이드 종류") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    content = {
                        items(RaidItem.getAllRaidData()) { item ->
                            ElevatedFilterChip(
                                selected = filter.isSelectedType(item.name),
                                onClick = { updateFilter(filter.updateRaidType(item.name)) },
                                label = {
                                    Text(
                                        text = Translate.getTranslate(item.name),
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
                    Filter.FINISH.entries.forEach { finish ->
                        val selected = filter.isSelected(finish)

                        val updateFinish = {
                            updateFilter(filter.updateFinish(finish))
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .selectable(
                                    selected = selected,
                                    onClick = updateFinish
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = updateFinish,
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
                                selected = filter.isSelectedUser(name),
                                onClick = {
                                    updateFilter(filter.updateUser(name))
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
                                    updateFilter(filter.updateTimeStep(it))
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
                            onCheckedChange = { updateFilter(filter.updateEmptyCalendarRow(it)) }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "빈 행 보이기")
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        updateFilter(filter.clear())
                    }
                ) {
                    Text("필터 초기화")
                }

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
                    painter = painterResource(Res.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(if (show) 90f else 0f)
                        .align(Alignment.CenterEnd)
                )
            }
            AnimatedVisibility(show) {
                content()
            }
        }
    }
}