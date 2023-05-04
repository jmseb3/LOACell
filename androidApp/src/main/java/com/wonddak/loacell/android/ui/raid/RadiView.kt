package com.wonddak.loacell.android.ui.raid

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.ui.bottomSheet.AddRaidSheet
import com.wonddak.loacell.android.ui.bottomSheet.BaseSheet
import com.wonddak.loacell.android.ui.raid.user.AddUserView
import com.wonddak.loacell.database.AppDataBase

@Composable
fun RaidView(
    db: AppDataBase,
    selectedRoomId: Long,
    backAction: () -> Unit
) {
    val roomInfo = db.roomInfoQueriesHelper.getRoomInfoById(selectedRoomId)
    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(selectedRoomId)
        .collectAsState(initial = emptyList())

    val context = LocalContext.current

    var showAddRaidSheet by remember {
        mutableStateOf(false)
    }
    var showAddUserSheet by remember {
        mutableStateOf(false)
    }
    BackHandler(!showAddUserSheet && !showAddRaidSheet) {
        backAction()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0f)
    ) {
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
                val userList by db.getUsersByRoomId(roomInfo.id)
                    .collectAsState(initial = emptyList())
                LazyColumn {
                    items(userList) { user ->
                        UserInfoCard(db = db, user = user)
                    }
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
            BaseSheet(title = "Add User Info") {
                AddUserView(
                ) { user, characterName ->
                    when {
                        user.isEmpty() -> {
                            Toast.makeText(context, "유저 이름이 비어있습니다.", Toast.LENGTH_SHORT).show()
                        }

                        characterName.isEmpty() -> {
                            Toast.makeText(context, "캐릭터 이름이 비어있습니다.", Toast.LENGTH_SHORT).show()
                        }

                        else -> {
                            db.addUserAndCharacters(roomInfo.id, user, characterName)
                                .let { result ->
                                    if (!result) {
                                        Toast.makeText(
                                            context,
                                            "이미 추가되어있는 캐릭터정보 입니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            showAddUserSheet = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(
    db: AppDataBase,
    user: UserInfo
) {
    fun updateRepresentativeCharacter() {
        db.updateUserRepresentativeCharacter(user.userId,"")
    }

    val characters by db.getCharacters(user.userId).collectAsState(initial = emptyList())
    var showCharacters by rememberSaveable {
        mutableStateOf(false)
    }
    var showMore by remember {
        mutableStateOf(false)
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCharacters = !showCharacters },
                text = "${user.name} - ${user.representativeCharacter}",
                textAlign = TextAlign.Center
            )
            AnimatedVisibility(visible = showCharacters) {
                Column {
                    Row() {
                        OutlinedButton(onClick = {  }) {
                            Text(text = "대표 캐릭터 변경")
                        }
                    }

                    Divider()
                    if (characters.size <= 6) {
                        characters.forEach { UserInfoCharacters(it) }
                    } else {
                        if (showMore) {
                            characters.forEach { UserInfoCharacters(it) }
                        } else {
                            characters.subList(0,6).forEach { UserInfoCharacters(it) }
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMore = !showMore },
                            text = if (showMore) "닫기" else "더보기 (${characters.size-6})",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoCharacters(
    character: Character
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Text(text = "${character.name}")
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val modifier = Modifier.fillMaxWidth(0.5f)
            Text(
                text = "${character.className}",
                modifier = modifier
            )
            Text(
                text = "${character.level}",
                modifier = modifier
            )
        }
        Divider()
    }
}

@Composable
fun RaidItemRow(raidInfo: RaidInfo) {
    Text(text = raidInfo.type.toString())
}
