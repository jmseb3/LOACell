package com.wonddak.loacell.android.ui.raid

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddRoomSheet
import com.wonddak.loacell.android.ui.bottomSheet.AddUserSheet
import com.wonddak.loacell.api.model.CharacterInfo
import com.wonddak.loacell.database.AppDataBase

@Composable
fun RaidView(
    db :AppDataBase,
    selectedRoomId :Long,
    backAction: () -> Unit
) {
    val roomInfo = db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(selectedRoomId).collectAsState(initial = emptyList())
    Column {
        var showAddRaidSheet by remember {
            mutableStateOf(false)
        }
        var showAddUserSheet by remember {
            mutableStateOf(false)
        }
        BackHandler(showAddRaidSheet) {
            showAddRaidSheet = false
        }
        BackHandler(showAddUserSheet) {
            showAddUserSheet = false
        }
        BackHandler(!showAddUserSheet && !showAddRaidSheet) {
            backAction()
        }

        OutlinedButton(onClick = { backAction() }) {
            Text(text = "BACK")
        }

        Text(text = "SelectRoomId : ${roomInfo.id}")
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
                OutlinedButton(onClick = { showAddRaidSheet = true }) {
                    Text(text = "show")
                }
                LazyColumn {
                    items(raidInfoList) { raidInfo ->
                        RaidItemRow(raidInfo)
                    }
                }
            }

            1 -> {
                OutlinedButton(onClick = { showAddUserSheet = true }) {
                    Text(text = "show")
                }
                val userList by db.getUsersByRoomId(roomInfo.id).collectAsState(initial = emptyList())
                LazyColumn {
                    items(userList) { user ->
                        Text(text = user.name)
                        val characters by db.getCharactersByUser(user.name).collectAsState(initial = emptyList())
                        characters.forEach {
                            Text(text = "\t${it.name}")
                        }
                    }
                }
            }
        }
        val context = LocalContext.current

        if (showAddUserSheet) {
            BottomSheetDialog(
                onDismissRequest = { showAddUserSheet = false },
                properties = BottomSheetDialogProperties(dismissWithAnimation = true),
            ) {
                AddUserSheet() { user, characterList ->
                    when {
                        user.isEmpty() -> {
                            Toast.makeText(context,"유저 이름이 비어있습니다.",Toast.LENGTH_SHORT).show()
                        }
                        characterList.isEmpty() -> {
                            Toast.makeText(context,"선택된 캐릭터 정보가 없습니다.",Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            db.addUserAndCharacters(roomId = roomInfo.id,user,characterList)
                            showAddUserSheet = false
                        }
                    }

                }
            }
        }
        if (showAddRaidSheet) {
            BottomSheetDialog(
                onDismissRequest = { showAddRaidSheet = false },
                properties = BottomSheetDialogProperties(dismissWithAnimation = true),
            ) {
                AddRaidSheet()
            }
        }

    }
}

@Composable
fun RaidItemRow(raidInfo: RaidInfo) {
    Text(text = raidInfo.type.toString())
}
