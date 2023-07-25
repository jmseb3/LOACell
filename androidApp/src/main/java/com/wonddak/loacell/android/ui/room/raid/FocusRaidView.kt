package com.wonddak.loacell.android.ui.room.raid

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wonddak.database.AppDataBase
import com.wonddak.database.ext.getMaxParty
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.database.model.Day
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRaidSheet
import com.wonddak.loacell.android.ui.dialog.DeleteRaidDialog
import com.wonddak.loacell.android.ui.dialog.DeleteRaidUserDialog
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonRaidHelper

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
        val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()

        val raidInfo: RaidInfo? by loaCellViewModel.raidInfo.collectAsState(null)
        val userInfoList  = totalRoomInfo.userInfoList

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
                if (raidInfo.day != Day.NONE) {
                    Text(text = raidInfo.getDayText())
                }
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
                    val close = { showRaidEdit = false }
                    EditRaidSheet(
                        raidInfo = raidInfo,
                        onDismissRequest = close,
                        successAction = close
                    )
                }
            }
        }
    }
}