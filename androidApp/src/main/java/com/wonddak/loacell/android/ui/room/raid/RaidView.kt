package com.wonddak.loacell.android.ui.room.raid

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getMaxParty
import com.wonddak.loacell.ext.getRaidText
import com.wonddak.loacell.ext.makeGateText
import com.wonddak.loacell.model.RaidType
import com.wonddak.loacell.store.CommonRaidHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidView(
    db: AppDataBase, roomId: String, loaCellViewModel: LoaCellViewModel
) {

    val raidInfoList by loaCellViewModel.raidInfoList.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

    val raidTypes = RaidType.values()
    Box() {
        Column(modifier = Modifier.fillMaxSize()) {
            Row() {
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
            val filterByFinish = when (loaCellViewModel.filterFinish) {
                1 -> raidInfoList.filter { it.isFinish }
                2 -> raidInfoList.filter { !it.isFinish }
                else -> raidInfoList
            }
            val filterByType = filterByFinish.filter { loaCellViewModel.filterRaidType.contains(it.type)}
            LazyColumn(
                modifier = Modifier.padding(10.dp)
            ) {
                items(filterByType) { raidInfo ->
                    RaidItemRow(raidInfo) {
                        loaCellViewModel.setNowRaidInfo(raidInfo.raidId)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
        if (focusRaidId.isNotEmpty()) {
            FocusRaidView(db, roomId, loaCellViewModel)
        }
        loaCellViewModel.apply {
            if (showRaidFilter) {
                BaseSheet(title = "필터 설정",
                    onDismissRequest = { showRaidFilter = false }) {
                    Column {
                        Text(text = "레이드 종류")
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            content = {
                                items(raidTypes) { type ->
                                    val selected = filterRaidType.contains(type)
                                    FilterChip(
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
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors()
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Divider()
                        Text(text = "완료 여부")
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(0,1,2).forEach { finish ->
                                val selected =filterFinish == finish

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
                                        onClick = { filterFinish = finish  },
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
                }
            }
        }
    }
}

@Composable
fun FocusRaidView(
    db: AppDataBase, roomId: String, loaCellViewModel: LoaCellViewModel
) {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(md_theme_light_background)
        .noRippleClickable() { }) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        val raidInfo: RaidInfo? by loaCellViewModel.raidInfo.collectAsState(null)
        val context = LocalContext.current
        var focusIndex by remember {
            mutableStateOf(-1)
        }
        var characterList: List<Character?> by remember {
            mutableStateOf(emptyList())
        }
        LaunchedEffect(raidInfo) {
            raidInfo?.let { info ->
                val maxParty = info.getMaxParty()
                val characters = mutableListOf<Character?>()
                characters.addAll(info.party1characterList.map {
                    db.characterInfoQueriesHelper.characterName(
                        it
                    )
                })

                if (maxParty == 2) {
                    characters.addAll(info.party2characterList.map {
                        db.characterInfoQueriesHelper.characterName(
                            it
                        )
                    })
                }
                characterList = characters
            }
        }
        raidInfo?.let { raidInfo ->
            val allUserList by db.getUsersByRoomIdFilterCharacterAndType(
                roomId, raidInfo
            ).collectAsState(initial = emptyList())

            Column() {
                Text(text = raidInfo.getRaidText())
                Text(text = raidInfo.makeGateText())
            }

            loaCellViewModel.apply {

                RaidPartyView(db, characterList, openAction = { index ->
                    if (allUserList.isEmpty()) {
                        showSnackBar(
                            message = "추가 가능한 인원이 없습니다.",
                            label = "이동",
                        ) {
                            clearFocusItem()
                            setTabStatus(1)
                            showUserAdd = true
                        }
                    } else {
                        focusIndex = index
                        openRaidUserAddDialog = true
                    }
                }, deleteAction = { index ->
                    focusIndex = index
                    openRaidUserDeleteDialog = true
                })
                if (openRaidDeleteDialog) {
                    DeleteRaidDialog(confirm = {
                        CommonRaidHelper.delete(roomId, raidInfo.raidId, failAction = { error ->
                            Toast.makeText(
                                context, error.errorMsg, Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            clearFocusItem()
                            openRaidDeleteDialog = false
                        }
                    }, dismiss = {
                        openRaidDeleteDialog = false
                    })
                }
                if (openRaidUserAddDialog && allUserList.isNotEmpty()) {
                    AddRaidUserSheet(raidInfo = raidInfo,
                        allUserList = allUserList,
                        db = db,
                        onDismissRequest = {
                            openRaidUserAddDialog = false
                        }) { character ->
                        val partyIndex = focusIndex / 4
                        val tempList =
                            if (partyIndex == 0) raidInfo.party1characterList else raidInfo.party2characterList
                        val partyTemp = tempList.toMutableList()
                        partyTemp[focusIndex % 4] = character.name
                        CommonRaidHelper.updatePartList(roomId,
                            raidInfo.raidId,
                            partyIndex + 1,
                            partyTemp,
                            failAction = { error ->
                                loaCellViewModel.showSnackBar("인원 추가에 실패했습니다.")
                            }) {
                            focusIndex = -1
                            openRaidUserAddDialog = false
                        }


                    }
                }

                if (openRaidUserDeleteDialog) {
                    val partyIndex = focusIndex / 4
                    val tempList =
                        if (partyIndex == 0) raidInfo.party1characterList else raidInfo.party2characterList
                    val partyTemp = tempList.toMutableList()
                    partyTemp[focusIndex % 4] = ""
                    DeleteRaidUserDialog(confirm = {
                        CommonRaidHelper.updatePartList(roomId,
                            raidInfo.raidId,
                            partyIndex + 1,
                            partyTemp,
                            failAction = { error ->
                                showSnackBar("유저 삭제에 실패했습니다.")
                            }) {
                            openRaidUserDeleteDialog = false
                        }
                    }, dismiss = {
                        openRaidUserDeleteDialog = false
                    })
                }
            }
        }
    }
}

@Composable
fun RaidPartyView(
    db: AppDataBase,
    list: List<Character?>,
    openAction: (index: Int) -> Unit,
    deleteAction: (index: Int) -> Unit,
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
    ) {
        LazyColumn(modifier = Modifier.padding(5.dp)) {
            itemsIndexed(list) { index, item ->
                val modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)

                if (index == 4) {
                    Divider()
                }
                if (item == null) {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "캐릭터를 추가해주세요")
                        MyIconButton(SharedRes.images.add) {
                            openAction(index)
                        }
                    }
                } else {
                    val character = db.characterInfoQueriesHelper.characterName(item.name)
                    character?.let { info ->
                        Row(
                            modifier = modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier.weight(5f)
                            ) {
                                Text(text = info.name)
                                Row(
                                    modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = info.className)
                                    Text(text = info.level)
                                }
                            }
                            IconButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    deleteAction(index)
                                },
                            ) {
                                Icon(
                                    modifier = Modifier.size(size = 30.dp),
                                    painter = painterResource(SharedRes.images.delete.drawableResId),
                                    contentDescription = ""
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
