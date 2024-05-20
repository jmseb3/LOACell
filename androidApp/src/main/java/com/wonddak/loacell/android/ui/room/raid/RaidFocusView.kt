package com.wonddak.loacell.android.ui.room.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wonddak.database.ext.getMaxParty
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.Sheet


@Composable
fun RaidFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo = loaCellViewModel.totalRoomInfo
    val baseUrl = loaCellViewModel.defaultUrl
    val tabs = totalRoomInfo.getTabList()
    var tabIndex by remember { mutableIntStateOf(0) }
    val partyIndex = arrayOf(0,4,8,12)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(md_theme_light_background)
            .noRippleClickable() { },
    ) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        totalRoomInfo.raidInfo?.let { raidInfo ->
            when (tabIndex) {
                0 -> {
                    RaidPartySimpleView(
                        loaCellViewModel,
                        raidInfo,
                        totalRoomInfo.partyCharacterList,
                        baseUrl = baseUrl
                    )

                }

                1, 2, 3, 4 -> {
                    val stIdx = partyIndex[tabIndex-1]
                    totalRoomInfo.getSubPartyList(stIdx)?.let { party ->
                            RaidPartyView(
                                party,
                                baseUrl = baseUrl,
                                openAction = { index ->
                                    val newIndex = stIdx + index
                                    val userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                                    with(loaCellViewModel){
                                        if (userAndCharacterMap.isEmpty()) {
                                            showSnackBar(
                                                message = "추가 가능한 인원이 없습니다.",
                                                label = "이동",
                                            ) {
                                                clearFocusItem()
                                                setTabStatus(RoomState.User)
                                                showDialog(Sheet.USER_ADD)

                                            }
                                        } else {
                                            updatePartyFocusIndex(newIndex)
                                            showDialog(Sheet.RAID_USER_ADD)
                                        }
                                    }
                                },
                                deleteAction = { index ->
                                    val newIndex = stIdx + index
                                    with(loaCellViewModel) {
                                        updatePartyFocusIndex(newIndex)
                                        showDialog(Dialog.RAID_USER_DELETE)
                                    }
                                }
                            )

                    }
                }

                else -> {
                    Text("Error")
                }
            }
        }
    }
}
