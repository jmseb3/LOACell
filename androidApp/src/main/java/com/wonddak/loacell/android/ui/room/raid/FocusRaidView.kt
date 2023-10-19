package com.wonddak.loacell.android.ui.room.raid

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.database.model.Day
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.bottomSheet.EditRaidSheet
import com.wonddak.loacell.android.ui.dialog.DeleteRaidDialog
import com.wonddak.loacell.android.ui.dialog.DeleteRaidUserDialog
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonRaidHelper

@Composable
fun FocusRaidView(
    loaCellViewModel: LoaCellViewModel
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(md_theme_light_background)
        .noRippleClickable() { }) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        val context = LocalContext.current

        val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
        val roomId = totalRoomInfo.roomId!!
        val dialogStatus = totalRoomInfo.dialogState
        val raidInfo = totalRoomInfo.raidInfo
        var characterList = totalRoomInfo.partyCharacterList

        var focusIndex by remember { mutableIntStateOf(-1) }

        raidInfo?.let { raidInfo ->

            Column() {
                Text(text = "${raidInfo.getRaidText()} ${raidInfo.makeGateText()}")
                if (raidInfo.day != Day.NONE) {
                    Text(text = raidInfo.getDayText())
                }
            }
            loaCellViewModel.apply {
                RaidPartyView(characterList, openAction = { index ->
                    val userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                    if (userAndCharacterMap.isEmpty()) {
                        showSnackBar(
                            message = "추가 가능한 인원이 없습니다.",
                            label = "이동",
                        ) {
                            clearFocusItem()
                            setTabStatus(RoomState.User)
                            showDialog(DialogStatus.USER_ADD)
                        }
                    } else {
                        focusIndex = index
                        showDialog(DialogStatus.RAID_USER_ADD)
                    }
                }, deleteAction = { index ->
                    focusIndex = index
                    showDialog(DialogStatus.RAID_USER_DELETE)
                })
                val close = { hideDialog() }
                when (dialogStatus) {
                    DialogStatus.RAID_DELETE -> {
                        DeleteRaidDialog(
                            confirm = {
                                CommonRaidHelper.delete(
                                    roomId,
                                    raidInfo.raidId,
                                    failAction = { error ->
                                        Toast.makeText(
                                            context, error.errorMsg, Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                    clearFocusItem()
                                    close()
                                }
                            },
                            dismiss = close
                        )
                    }
                    DialogStatus.RAID_USER_ADD -> {
                        val userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                        if (userAndCharacterMap.isNotEmpty()) {
                            AddRaidUserSheet(
                                userAndCharacterMap = userAndCharacterMap,
                                onDismissRequest = close
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
                                    close()
                                }
                            }
                        }
                    }
                    DialogStatus.RAID_USER_DELETE -> {
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
                               close()
                            }
                        }, dismiss = close)
                    }
                    DialogStatus.RAID_EDIT -> {
                        EditRaidSheet(
                            raidInfo = raidInfo,
                            onDismissRequest = close,
                            successAction = close
                        )
                    }

                    else -> {

                    }
                }
            }
        }
    }
}