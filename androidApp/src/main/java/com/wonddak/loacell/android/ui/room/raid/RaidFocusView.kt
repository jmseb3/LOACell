package com.wonddak.loacell.android.ui.room.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.database.model.Day
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomState

@Composable
fun RaidFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(md_theme_light_background)
        .noRippleClickable() { }) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
        val raidInfo = totalRoomInfo.raidInfo
        var characterList = totalRoomInfo.partyCharacterList

        raidInfo?.let { raidInfo ->

            Column() {
                Text(text = "${raidInfo.getRaidText()} ${raidInfo.makeGateText()}")
                if (raidInfo.day != Day.NONE) {
                    Text(text = raidInfo.getDayText())
                }
            }
            loaCellViewModel.apply {
                RaidPartyView(
                    characterList,
                    openAction = { index ->
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
                            updatePartyFocusIndex(index)
                            showDialog(DialogStatus.RAID_USER_ADD)
                        }
                    },
                    deleteAction = { index ->
                        updatePartyFocusIndex(index)
                        showDialog(DialogStatus.RAID_USER_DELETE)
                    }
                )
            }
        }
    }
}