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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.database.ext.getAllPartyList
import com.wonddak.database.ext.getMaxParty
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomState
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.FilterSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.dialog.DeleteRaidDialog
import com.wonddak.loacell.android.ui.dialog.DeleteRaidUserDialog
import com.wonddak.loacell.android.ui.dialog.EditRaidTitleDialog
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.store.CommonRaidHelper

@Composable
fun RaidView(
    db: AppDataBase, roomId: String, loaCellViewModel: LoaCellViewModel
) {

    val raidInfoList by loaCellViewModel.raidInfoList.collectAsState()
    val userInfoList by loaCellViewModel.userInfoList.collectAsState()
    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

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
            val filterByType =
                filterByFinish.filter { loaCellViewModel.filterRaidType.contains(it.type) }

            val filterByUser = if (loaCellViewModel.filterUser.isEmpty()) {
                filterByType
            } else {
                filterByType.filter {
                    val names = mutableListOf<String>()
                    val filterUser = userInfoList.filter{ loaCellViewModel.filterUser.contains(it.name) }
                    it.getAllPartyList().forEach {character ->
                        if (character.isNotEmpty()) {
                            for (userInfo in filterUser) {
                                if(db.characterQueriesHelper.getAllNameList(userInfo).contains(character)) {
                                    names.add(userInfo.name)
                                    break
                                }
                            }
                        }
                    }
                    names.sorted() == loaCellViewModel.filterUser.sorted()
                }
            }

            LazyColumn(
                modifier = Modifier.padding(10.dp)
            ) {
                items(filterByUser) { raidInfo ->
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
                FilterSheet(loaCellViewModel = loaCellViewModel)
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
        val userInfoList  by loaCellViewModel.userInfoList.collectAsState()

        val context = LocalContext.current
        var focusIndex by remember { mutableIntStateOf(-1) }
        var characterList: List<Character?> by remember {
            mutableStateOf(emptyList())
        }
        LaunchedEffect(raidInfo) {
            raidInfo?.let { info ->
                val maxParty = info.getMaxParty()
                val findList = info.party1characterList.toMutableList()
                if (maxParty == 2) {
                    findList.addAll(info.party2characterList)
                }
                val result : MutableList<Character?> = List(findList.size) { null }.toMutableList()
                val findNames = findList.filter { it.isNotEmpty() }.toMutableList()

                for (userInfo in userInfoList) {
                    val iterator = findNames.iterator()
                    while (iterator.hasNext()) {
                        val name = iterator.next()
                        val find = db.characterQueriesHelper.getCharacterInfo(userInfo,name)
                        if (find != null) {
                            result[findList.indexOf(name)] = find
                        }
                    }
                }
                characterList = result
            }
        }
        raidInfo?.let { raidInfo ->
            val userAndCharacterMap by db.getUsersByRoomIdFilterCharacterAndType(roomId, raidInfo).collectAsState(initial = mapOf())

            Column() {
                Text(text = "${raidInfo.getRaidText()} ${ raidInfo.makeGateText()}")
            }
            loaCellViewModel.apply {
                RaidPartyView(characterList, openAction = { index ->
                    if (userAndCharacterMap.isEmpty()) {
                        showSnackBar(
                            message = "추가 가능한 인원이 없습니다.",
                            label = "이동",
                        ) {
                            clearFocusItem()
                            setTabStatus(RoomState.User)
                            showUserAdd = true
                        }
                    } else {
                        focusIndex = index
                        showRaidUserAdd = true
                    }
                }, deleteAction = { index ->
                    focusIndex = index
                    showRaidUserDelete = true
                })
                if (showRaidDelete) {
                    DeleteRaidDialog(confirm = {
                        CommonRaidHelper.delete(roomId, raidInfo.raidId, failAction = { error ->
                            Toast.makeText(
                                context, error.errorMsg, Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            clearFocusItem()
                            showRaidDelete = false
                        }
                    }, dismiss = {
                        showRaidDelete = false
                    })
                }
                if (showRaidUserAdd && userAndCharacterMap.isNotEmpty()) {
                    AddRaidUserSheet(
                        userAndCharacterMap = userAndCharacterMap,
                        onDismissRequest = {
                            showRaidUserAdd = false
                        }
                    ) { character ->
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
                            showRaidUserAdd = false
                        }
                    }
                }

                if (showRaidUserDelete) {
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
                            showRaidUserDelete = false
                        }
                    }, dismiss = {
                        showRaidUserDelete = false
                    })
                }

                if (showRaidEdit) {
                    EditRaidTitleDialog(
                        nowTitle = raidInfo.title,
                        success =  {
                            CommonRaidHelper.updateTitle(raidInfo.roomId,raidInfo.raidId,it)
                            showRaidEdit = false
                        }
                    ) {
                        showRaidEdit = false
                    }
                }
            }
        }
    }
}

@Composable
fun RaidPartyView(
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
                        MyIconButton(
                            imageResource = SharedRes.images.add
                        ) {
                            openAction(index)
                        }
                    }
                } else {
                    item.let { info ->
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
