package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.rememberPartyIndexModalStatus
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.DeleteDialog
import com.wonddak.loacell.ui.modal.sheet.RaidUserAddSheet
import kotlinx.coroutines.launch

@Composable
fun RaidDetailView(
    raidId: String,
    raidList: List<RaidInfo>,
    userList: List<UserInfo>,
    onBack: () -> Unit,
) {
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
        val deleteDialogStatus = rememberPartyIndexModalStatus<List<String>>()
        val addUserStatus = rememberPartyIndexModalStatus<Any>()

        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    raidInfo.title,
                    onBack = onBack
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
                                    addUserStatus.partyIndex = page - 1
                                    addUserStatus.subItem = subIndex
                                    addUserStatus.show()
                                },
                                deleteAction = { subIndex ->
                                    deleteDialogStatus.partyIndex = page - 1
                                    deleteDialogStatus.subItem = raidInfo
                                        .getPartyByIndex(deleteDialogStatus.partyIndex)
                                        .toMutableList().also {
                                            it[subIndex] = ""
                                        }
                                    deleteDialogStatus.show()
                                }
                            )
                        }
                    }
                }
            }
        }

        DeleteDialog(
            deleteDialogStatus,
            title = Dialog.CHARACTER_DELETE.title,
            confirm = {
                CommonRaidHelper.updatePartList(
                    raidInfo.roomId,
                    raidInfo.raidId,
                    deleteDialogStatus.partyIndex + 1,
                    deleteDialogStatus.subItem!!,
                    {
                        deleteDialogStatus.hide()
                    },
                    {
                        deleteDialogStatus.hide()
                    }
                )
            },
        ) {
            Text(text = "선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
        }

        RaidUserAddSheet(
            addUserStatus,
            getUserMap(raidInfo, raidList, userList)
        )
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