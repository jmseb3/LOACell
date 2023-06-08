package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.database.model.RaidType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    loaCellViewModel: LoaCellViewModel
) {
    val raidTypes = RaidType.values()
    val userInfoList by loaCellViewModel.userInfoList.collectAsState(emptyList())

    loaCellViewModel.apply {
        BaseSheet(title = "필터 설정",
            useCloseIcon = true,
            onDismissRequest = { showRaidFilter = false }) {
            Column {
                FilterSection(section = "레이드 종류") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            items(raidTypes) { type ->
                                val selected = filterRaidType.contains(type)
                                ElevatedFilterChip(
                                    selected = selected,
                                    onClick = {
                                        val temp = filterRaidType.toMutableList()
                                        if (selected) {
                                            temp.remove(type)
                                        } else {
                                            temp.add(type)
                                        }
                                        filterRaidType = temp.toTypedArray()
                                    },
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
                        listOf(0, 1, 2).forEach { finish ->
                            val selected = filterFinish == finish

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = selected,
                                        onClick = { filterFinish = finish }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { filterFinish = finish },
                                    colors = RadioButtonDefaults.colors()
                                )
                                Text(
                                    text = when (finish) {
                                        1 -> "완료"
                                        2 -> "미완료"
                                        else -> "전체"
                                    },
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .fillMaxWidth(),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                }
                FilterSection("특정 유저 모두 포함") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            items(userInfoList) { userInfo ->
                                val name = userInfo.name
                                val selected = filterUser.contains(name)
                                ElevatedFilterChip(
                                    selected = selected,
                                    onClick = {
                                        val temp = filterUser.toMutableList()
                                        if (selected) {
                                            temp.remove(name)
                                        } else {
                                            temp.add(name)
                                        }
                                        filterUser = temp
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
}

@Composable
fun FilterSection(
    section :String,
    useDivider :Boolean = true,
    content : @Composable () -> Unit
) {
    Column {
        Text(text = section)
        content()
        if (useDivider){
            Divider()
        }
    }
}