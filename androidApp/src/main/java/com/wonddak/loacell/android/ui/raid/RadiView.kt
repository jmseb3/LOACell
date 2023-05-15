package com.wonddak.loacell.android.ui.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.android.R
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.ui.raid.user.AddUserView
import com.wonddak.loacell.android.ui.raid.user.UserInfoCard
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType

@Composable
fun RaidView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val scope = rememberCoroutineScope()

    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val roomInfo = db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(selectedRoomId)
        .collectAsState(initial = emptyList())

    BackHandler(!loaCellViewModel.showRaidAdd && !loaCellViewModel.showUserAdd) {
        loaCellViewModel.hideRoomInfo()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "SelectRoomId : ${roomInfo.uniqueId}")
        Text(text = "title : ${roomInfo.title}")
        Text(text = "description : ${roomInfo.description}")
        Divider()
        val titles = listOf("Raid", "User")
        TabRow(selectedTabIndex = loaCellViewModel.tabState) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = loaCellViewModel.tabState == index,
                    onClick = { loaCellViewModel.tabState = index }
                )
            }
        }
        when (loaCellViewModel.tabState) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                ) {
                    items(raidInfoList) { raidInfo ->
                        RaidItemRow(raidInfo)
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }

            1 -> {
                RaidUsersView(
                    db = db,
                    roomId = roomInfo.uniqueId
                )
            }
        }
    }

    if (loaCellViewModel.showRaidAdd) {
        BackHandler(true) {
            loaCellViewModel.hideRaidDialog()
        }
        BottomSheetDialog(
            onDismissRequest = { loaCellViewModel.hideRaidDialog() },
            properties = BottomSheetDialogProperties(dismissWithAnimation = true),
        ) {
            AddRaidSheet(roomInfo.uniqueId) {
                loaCellViewModel.hideRaidDialog()
            }
        }
    }

    if (loaCellViewModel.showUserAdd) {
        BackHandler(true) {
            loaCellViewModel.hideUserDialog()
        }
        BottomSheetDialog(
            onDismissRequest = {
                loaCellViewModel.hideUserDialog()
            },
            properties = BottomSheetDialogProperties(
                dismissWithAnimation = true,
            ),
        ) {
            BaseSheet(title = "유저 정보 추가") {
                AddUserView(
                    roomId = roomInfo.uniqueId
                ) {
                    loaCellViewModel.hideUserDialog()

                }
            }
        }
    }
}

@Composable
fun RaidUsersView(
    db: AppDataBase,
    roomId: String
) {
    val userList by db.getUsersByRoomId(roomId).collectAsState(initial = emptyList())
    Column() {
        LazyColumn {
            items(userList) { userInfo ->
                UserInfoCard(
                    db,
                    roomId,
                    userInfo
                )
            }
        }
    }
}


@Composable
fun RaidItemRow(raidInfo: RaidInfo) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(size),
        shape = rShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
        ) {
            Image(
                painter = painterResource(id = R.drawable.valtan),
                contentDescription = null,
                Modifier
                    .size(size)
                    .clip(rShape)
            )
            Column() {
                Text(text = raidInfo.type!!.toKorString())
                Text(text = raidInfo.Difficulty!!.toKorString())
                Text(text = raidInfo.title)
            }
        }
    }

}


@Composable
@Preview
fun RaidItemRowPreview() {
    val raidInfo = RaidInfo(
        raidId = "",
        roomId = "",
        title = "test",
        type = RaidType.VALTAN,
        Difficulty = Difficulty.Normal,
        startGateNumber = 1L,
        endGateNumber = 3L,
        isFinish = false
    )
    RaidItemRow(raidInfo)
}
