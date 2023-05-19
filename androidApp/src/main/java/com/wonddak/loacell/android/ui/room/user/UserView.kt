package com.wonddak.loacell.android.ui.room.user

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase

@Composable
fun UserView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {
    val userList by db.getUsersByRoomId(roomId).collectAsState(initial = emptyList())
    val focusUserName by loaCellViewModel.focusUserName.collectAsState()

    Box() {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(userList) { userInfo ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { loaCellViewModel.setNowUserInfo(userInfo.name) }
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "${userInfo.name} - ${userInfo.representativeCharacter}",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (focusUserName.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                BackHandler() {
                    loaCellViewModel.clearFocusItem()
                }
                val userInfo: UserInfo? by loaCellViewModel.userInfo.collectAsState(null)

                loaCellViewModel.apply {
                    userInfo?.let { userInfo ->
                        Text(text = "${userInfo.name}님의 캐릭터 정보입니다.")
                        Text(text = "대표 캐릭터 : ${userInfo.representativeCharacter}")
                        Divider()
                        LazyColumn {
                            items(userInfo.characterList.map { db.getCharacterValue(it) }) { character ->
                                character?.let { UserInfoCharacters(it) }
                            }
                        }
                        if (openCharacterEditDialog) {
                            EditCharacterDialog(
                                representativeCharacter = userInfo.representativeCharacter,
                                characters = userInfo.characterList,
                                confirm = { name ->
                                    FireStoreHelper.updateUserCharacter(
                                        roomId,
                                        userInfo.name,
                                        name
                                    )
                                    openCharacterEditDialog = false
                                },
                                dismiss = {
                                    openCharacterEditDialog = false
                                }
                            )
                        }
                        if (openCharacterDeleteDialog) {
                            DeleteCharacterDialog(
                                name = userInfo.name,
                                confirm = {
                                    FireStoreHelper.deleteUser(roomId, userInfo.name) {
                                        db.deleteUserName(userInfo.name, roomId)
                                    }
                                    loaCellViewModel.clearFocusItem()
                                    openCharacterDeleteDialog = false
                                },
                                dismiss = {
                                    openCharacterDeleteDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

