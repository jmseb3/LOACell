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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidUserSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.getMaxParty
import com.wonddak.loacell.getPartyList
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
        val context = LocalContext.current
        var focusIndex by remember {
            mutableStateOf(-1)
        }
        var focusPartyIndex by remember {
            mutableStateOf(0)
        }
        raidInfo?.let { raidInfo ->
            val allUserList by db.userInfoQueriesHelper.getUsersByRoomIdFilterCharacter(
                roomId,
                raidInfo.getPartyList()
            ).collectAsState(initial = emptyList())

            Column() {
                Text(text = raidInfo.getRaidText())
                Text(text = raidInfo.makeGateText())
            }
            PartyView(raidInfo) { index, partyIndex ->
                focusIndex = index
                focusPartyIndex = partyIndex
                loaCellViewModel.showRaidUserAdd()
                if (allUserList.isEmpty()) {
                    Toast.makeText(context,"추가 가능한 인원이 없습니다.",Toast.LENGTH_SHORT).show()
                    loaCellViewModel.hideRaidUserAdd()
                }
            }
            loaCellViewModel.apply {
                if (openRaidDeleteDialog) {
                    DeleteRaidDialog(
                        confirm = {
                            FireStoreHelper.deleteRaidInfo(
                                roomId,
                                raidInfo.raidId,
                                failAction = { e ->
                                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            ) {
                                clearFocusItem()
                                openRaidDeleteDialog = false
                            }
                        },
                        dismiss = {
                            openRaidDeleteDialog = false
                        }
                    )
                }
                if (openRaidUserAddDialog && allUserList.isNotEmpty()) {
                    val partyList = when (focusPartyIndex) {
                        2 -> {
                            raidInfo.party2characterList
                        }

                        1 -> {
                            raidInfo.party1characterList
                        }

                        else -> {
                            emptyList()
                        }
                    }
                    AddRaidUserSheet(
                        allUserList = allUserList,
                        db = db,
                        onDismissRequest = {
                            hideRaidUserAdd()
                        }
                    ) { character ->
                        val partyTemp = partyList.toMutableList()
                        partyTemp[focusIndex] = character.name
                        FireStoreHelper.updateRaidUser(
                            roomId,
                            raidInfo.raidId,
                            focusPartyIndex,
                            partyTemp
                        ) {
                            focusIndex = -1
                            focusPartyIndex = 0
                            hideRaidUserAdd()
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun PartyView(
    raidInfo: RaidInfo,
    openAction: (index: Int, partyIndex: Int) -> Unit
) {
    val maxParty = raidInfo.getMaxParty()

    Column() {
        RaidPartyView(
            list = raidInfo.party1characterList
        ) { index ->
            openAction(index, 1)
        }
        if (maxParty == 2) {
            Divider()
            RaidPartyView(
                list = raidInfo.party2characterList
            )
            { index ->
                openAction(index, 2)
            }
        }
    }

}

@Composable
fun RaidPartyView(
    list: List<String>,
    openAction: (index: Int) -> Unit
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            list.forEachIndexed { index, name ->
                RaidUserView(name = name) {
                    openAction(index)
                }
            }
        }
    }
}

@Composable
fun RaidUserView(name: String, addAction: () -> Unit) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
    if (name.isEmpty()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "캐릭터를 추가해주세요")
            MyIconButton(SharedRes.images.add) {
                addAction()
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$name")
        }
    }
}
