package com.wonddak.loacell.android.ui.room.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.getMaxParty
import com.wonddak.loacell.getRaidText
import com.wonddak.loacell.makeGateText

@Composable
fun RaidView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {

    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(roomId)
        .collectAsState(initial = emptyList())

    val focusRaidId by loaCellViewModel.focusRaidId.collectAsState()

    Box() {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.padding(10.dp)
            ) {
                items(raidInfoList) { raidInfo ->
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
    }

}


@Composable
fun FocusRaidView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .noRippleClickable() { }
    ) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        val raidInfo: RaidInfo? by loaCellViewModel.raidInfo.collectAsState(null)
        raidInfo?.let { raidInfo ->
            val maxParty = raidInfo.getMaxParty()
            Column() {
                Text(text = raidInfo.getRaidText())
                Text(text = raidInfo.makeGateText())
            }
            if (maxParty == 1) {
                SinglePartyView(raidInfo)
            } else {
                MultiplePartyView(raidInfo)
            }
        }
    }
}

@Composable
fun SinglePartyView(
    raidInfo: RaidInfo
) {
    Column() {
        RaidPartyView(list = raidInfo.party1characterList)
    }
}

@Composable
fun MultiplePartyView(raidInfo: RaidInfo) {
    Column {
        RaidPartyView(list = raidInfo.party1characterList)
        Divider()
        RaidPartyView(list = raidInfo.party2characterList)
    }
}


@Composable
fun RaidPartyView(
    list: List<String>
) {
    Column() {
        list.forEachIndexed { index, name ->
            RaidUserView(name = name, index = index)
        }
    }
}

@Composable
fun RaidUserView(name: String, index: Int) {
    if (name.isEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "캐릭터를 추가해주세요")
            MyIconButton(SharedRes.images.add) {
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$name")
        }
    }
}

@Composable
@Preview
fun RaidUserViewPreview() {
    Column {
        RaidUserView("아이스크림",0)
        Divider()
        RaidUserView(name = "",1)
    }

}