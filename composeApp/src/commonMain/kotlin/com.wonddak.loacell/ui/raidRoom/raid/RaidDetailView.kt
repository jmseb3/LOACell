package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.*
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.rememberPartyIndexModalStatus
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.DeleteDialog
import com.wonddak.loacell.ui.modal.sheet.AddUserSheet
import com.wonddak.loacell.ui.modal.sheet.RaidUserAddSheet
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.delete
import loacell.composeapp.generated.resources.room_setting

@Composable
fun RaidDetailView(
    roomInfo : RoomInfo?,
    raidId: String,
    raidList: List<RaidInfo>,
    userList: List<UserInfo>,
    navigationEdit: (RaidInfo) -> Unit,
    onBack: () -> Unit,
) {
    SetBackAction(true) {
        onBack()
    }
    val raidInfo = raidList.find { it.raidId == raidId }
    if (raidInfo == null) {
        TextButton(onClick = onBack) {
            Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
        }
    } else {
        val tabs = arrayListOf("All").also { arr ->
            (1..raidInfo.getMaxParty()).forEach {
                arr.add(it.toString())
            }
        }
        val tabIndex = rememberPagerState(pageCount = {
            tabs.size
        })
        val scope = rememberCoroutineScope()
        val raidUserDeleteDialogStatus = rememberPartyIndexModalStatus<List<String>>()
        val raidDetailDeleteDialog = rememberModalStatus()
        val raidUserAddSheetStatus = rememberPartyIndexModalStatus<List<String>>()
        val showUserAddSheet = rememberModalStatus()
        val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
        val fabStatus = rememberModalStatus()

        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    raidInfo.title,
                    actionContent = {
                        FinishButton(raidInfo, modifier = Modifier.padding(end = 5.dp))
                    },
                    onBack = onBack
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            floatingActionButton = {
                OpenableFabMenu(
                    fabStatus,
                    listOf(
                        FABInfo.Default(
                            Res.drawable.room_setting
                        ) {
                            navigationEdit(raidInfo)
                        },
                        FABInfo.Default(
                            Res.drawable.delete
                        ) {
                            raidDetailDeleteDialog.show()
                        }
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                TabRow(selectedTabIndex = tabIndex.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = tabIndex.currentPage == index,
                            onClick = {
                                scope.launch {
                                    tabIndex.scrollToPage(index)
                                }
                            }
                        )
                    }
                }
                HorizontalPager(
                    tabIndex,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            RaidPartySimpleView(
                                raidInfo,
                                getCharacterList(null, raidInfo, userList)
                            )
                        }

                        1, 2, 3, 4 -> {
                            RaidPartyView(
                                getCharacterList(page - 1, raidInfo, userList),
                                openAction = { subIndex ->
                                    val charMap = getUserMap(raidInfo, raidList, userList)
                                    if (charMap.isEmpty()) {
                                        scope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            val snackbar = snackbarHostState.showSnackbar(
                                                "추가 가능한 인원이 없습니다.",
                                                actionLabel = "추가"
                                            )
                                            when (snackbar) {
                                                SnackbarResult.ActionPerformed -> {
                                                    showUserAddSheet.show()
                                                }

                                                SnackbarResult.Dismissed -> {

                                                }
                                            }
                                        }
                                    } else {
                                        raidUserAddSheetStatus.partyIndex = page - 1
                                        raidUserAddSheetStatus.subIndex = subIndex
                                        raidUserAddSheetStatus.subItem = raidInfo
                                            .getPartyByIndex(raidUserDeleteDialogStatus.partyIndex)
                                            .toMutableList()
                                        raidUserAddSheetStatus.show()
                                    }
                                },
                                deleteAction = { subIndex ->
                                    raidUserDeleteDialogStatus.partyIndex = page - 1
                                    raidUserDeleteDialogStatus.subIndex = subIndex
                                    raidUserDeleteDialogStatus.subItem = raidInfo
                                        .getPartyByIndex(raidUserDeleteDialogStatus.partyIndex)
                                        .toMutableList().also {
                                            it[subIndex] = ""
                                        }
                                    raidUserDeleteDialogStatus.show()
                                }
                            )
                        }
                    }
                }
            }
        }

        DeleteDialog(
            raidUserDeleteDialogStatus,
            title = Dialog.CHARACTER_DELETE.title,
            confirm = {
                CommonRaidHelper.deletePartyList(
                    raidUserDeleteDialogStatus,
                    raidInfo
                )
            },
        ) {
            Text(text = "선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
        }

        DeleteDialog(
            raidDetailDeleteDialog,
            title = Dialog.RAID_DELETE.title,
            confirm = {
                CommonRaidHelper.delete(
                    raidInfo.roomId,
                    raidInfo.raidId,
                    {
                        raidDetailDeleteDialog.hide()
                    },
                    {
                        raidDetailDeleteDialog.hide()
                        onBack()
                    }
                )
            },
        ) {
            Text(text = "레이드 정보를 삭제 하시겠습니까?")
        }

        RaidUserAddSheet(
            raidUserAddSheetStatus,
            getUserMap(raidInfo, raidList, userList)
        ) { character ->
            CommonRaidHelper.changePartyList(raidUserAddSheetStatus, raidInfo, character)
        }

        roomInfo?.let {
            AddUserSheet(
                showUserAddSheet,
                Modifier,
                it
            )
        }
    }
}

private fun getCharacterList(
    index: Int? = null,
    raidInfo: RaidInfo,
    userList: List<UserInfo>,
): List<Character?> {

    val findList = index?.let { raidInfo.getPartyByIndex(it) } ?: raidInfo.getAllPartyList()
    val result: MutableList<Character?> = List(findList.size) { null }.toMutableList()
    val findNames = findList.filter { it.isNotEmpty() }.toMutableList()
    for (userInfo in userList) {
        val iterator = findNames.iterator()
        while (iterator.hasNext()) {
            val name = iterator.next()
            val find = userInfo.characterList.find { it.name == name }
            if (find != null) {
                result[findList.indexOf(name)] = find
            }
        }
    }
    return result
}

private fun getUserMap(
    raidInfo: RaidInfo,
    raidList: List<RaidInfo>,
    userList: List<UserInfo>,
): Map<String, List<Character>> {
    //현재 레이드 정보에 들어가있는 캐릭터 이름을 가져옴
    val characterNameInParty = raidInfo.getPartNameList()

    //현재 레이드 타입에 맞는 것만 필터링 한뒤 파티에 가입된 캐릭터를 모두 추가한다.
    val totalNameList = mutableSetOf<String>()
    raidList
        .filter { it.type == raidInfo.type }
        .forEach { it ->
            //각 레이드 정보에있는 캐릭터 이름을 모두 넣는다.
            totalNameList.addAll(it.party1characterList)
            totalNameList.addAll(it.party2characterList)
            totalNameList.addAll(it.party3characterList)
            totalNameList.addAll(it.party4characterList)
        }

    //빈값 삭제해줌
    totalNameList.remove("")

    val result: MutableMap<String, List<Character>> = mutableMapOf()
    userList.forEach { user ->

        val lcFilter: Set<Character> = user.characterList.filter { character ->
            character.getLevel() >= raidInfo.getMinLevel()
        }.toSet()

        val nameList: Set<String> = lcFilter.map { it.name }.toSet()

        if (characterNameInParty.intersect(nameList).isEmpty()) {
            // 현재 파티에 추가되지 않은 경우
            // 다른곳에 추가된 캐릭터를 제외하고 새로운 리스트를 만든다.(

            val newList = lcFilter.filterNot { totalNameList.contains(it.name) }
                .sortedByDescending { it.getLevel() }

            if (newList.isNotEmpty()) {
                //비어있지 않다면 추가해준다.
                result[user.name] = newList
            }
        }

    }
    return result
}