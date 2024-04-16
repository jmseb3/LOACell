package com.wonddak.loacell.android.ui.room.raid

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.database.ext.getMaxParty
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.database.model.Day
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.util.FileUtil
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.Sheet
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import kotlin.math.pow


@Composable
fun RaidFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val baseUrl by loaCellViewModel.defaultUrl.collectAsState(initial = "")
    val tabs = arrayListOf("all").also {
        totalRoomInfo.raidInfo?.let { raidInfo ->
            when (raidInfo.getMaxParty()) {
                4 -> {
                    it.addAll(arrayListOf("1", "2", "3", "4"))
                }

                2 -> {
                    it.addAll(arrayListOf("1", "2"))
                }

                else -> {
                    it.add("1")
                }
            }
        }

    }
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
                    runCatching {
                        totalRoomInfo.partyCharacterList.subList(
                            stIdx,
                            stIdx + 4
                        )
                    }.getOrNull()?.let { party ->
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
