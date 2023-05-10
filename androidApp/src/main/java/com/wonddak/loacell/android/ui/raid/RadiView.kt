package com.wonddak.loacell.android.ui.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.raid.user.AddUserView
import com.wonddak.loacell.android.ui.raid.user.UserInfoCard
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.database.AppDataBase
import kotlinx.coroutines.launch

@Composable
fun RaidView(
    db: AppDataBase,
    selectedRoomId: String,
    backAction: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val roomInfo = db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(selectedRoomId)
        .collectAsState(initial = emptyList())

    FireStoreHelper.observeUsers(selectedRoomId,db)

    var showAddRaidSheet by remember {
        mutableStateOf(false)
    }
    var showAddUserSheet by remember {
        mutableStateOf(false)
    }

    var showLoadingProgress by remember {
        mutableStateOf(false)
    }
    var showLoadingProgressText by remember {
        mutableStateOf("")
    }
    BackHandler(!showAddUserSheet && !showAddRaidSheet) {
        backAction()
    }

    Box {
        if (showLoadingProgress) {
            val interactionSource = MutableInteractionSource()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(0.6f))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {

                    }
                    .zIndex(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                )
                if (showLoadingProgressText.isNotEmpty()) {
                    Text(text = "${showLoadingProgressText}님의 정보를 업데이트 중입니다.")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            OutlinedButton(onClick = { backAction() }) {
                Text(text = "BACK")
            }

            Text(text = "SelectRoomId : ${roomInfo.uniqueId}")
            Text(text = "title : ${roomInfo.title}")
            Text(text = "description : ${roomInfo.description}")
            Divider()
            var tabState by remember { mutableStateOf(0) }
            val titles = listOf("Raid", "User")
            TabRow(selectedTabIndex = tabState) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabState == index,
                        onClick = { tabState = index }
                    )
                }
            }
            when (tabState) {
                0 -> {
                    MyIconButton(
                        SharedRes.images.add
                    ) {
                        showAddRaidSheet = true
                    }
                    LazyColumn {
                        items(raidInfoList) { raidInfo ->
                            RaidItemRow(raidInfo)
                        }
                    }
                }

                1 -> {
                   RaidUsersView(
                       db = db,
                       roomId = roomInfo.uniqueId,
                       addAction = {
                           showAddUserSheet = true
                       },
                       refreshAction = {
                           scope.launch {
                               showLoadingProgress = true
                               showLoadingProgress = false
                           }
                       }
                   )
                }
            }
        }
    }


    if (showAddRaidSheet) {
        BackHandler(true) {
            showAddRaidSheet = false
        }
        BottomSheetDialog(
            onDismissRequest = { showAddRaidSheet = false },
            properties = BottomSheetDialogProperties(dismissWithAnimation = true),
        ) {
            AddRaidSheet()
        }
    }

    if (showAddUserSheet) {
        BackHandler(true) {
            showAddUserSheet = false
        }
        BottomSheetDialog(
            onDismissRequest = { showAddUserSheet = false },
            properties = BottomSheetDialogProperties(
                dismissWithAnimation = true,
            ),
        ) {
            BaseSheet(title = "유저 정보 추가") {
                AddUserView(
                    roomId = roomInfo.uniqueId
                ) {
                    showAddUserSheet = false
                }
            }
        }
    }
}
@Composable
fun RaidUsersView(
    db :AppDataBase,
    roomId : String,
    addAction : () -> Unit,
    refreshAction: () -> Unit
) {
    val userList by db.getUsersByRoomId(roomId).collectAsState(initial = emptyList())
    Column() {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MyIconButton(
                SharedRes.images.add
            ) {
                addAction()
            }
            MyIconButton(
                SharedRes.images.refresh
            ) {
                refreshAction()
            }
        }
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
    Text(text = raidInfo.type.toString())
}
