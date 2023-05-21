package com.wonddak.loacell.android.ui.room.user

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.AppDataBase
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.sharedapi.LostArkApi
import com.wonddak.sharedapi.onError
import com.wonddak.sharedapi.onException
import com.wonddak.sharedapi.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//room화면에서 user리스트 화면
@Composable
fun UserView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {
    val userList by db.userInfoQueriesHelper.getUsersByRoomId(roomId)
        .collectAsState(initial = emptyList())
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
            FocusUserView(db, roomId, loaCellViewModel)
        }
    }
}

//유저를 선택했을때 보여질 화면
@Composable
fun FocusUserView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {
    val userInfo: UserInfo? by loaCellViewModel.userInfo.collectAsState(null)
    userInfo?.let { userInfo ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .noRippleClickable()
            ) {
                BackHandler() {
                    loaCellViewModel.clearFocusItem()
                }

                loaCellViewModel.apply {

                    Text(text = "${userInfo.name}님의 캐릭터 정보입니다.")
                    Text(text = "대표 캐릭터 : ${userInfo.representativeCharacter}")
                    Divider()
                    LazyColumn {
                        items(userInfo.characterList.map {
                            db.characterInfoQueriesHelper.getCharacterValue(
                                it
                            )
                        }) { character ->
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
                                    db.userInfoQueriesHelper.deleteUserName(userInfo.name, roomId)
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

        var msg by remember {
            mutableStateOf("캐릭터 정보를 갱신합니다.")
        }

        LaunchedEffect(loaCellViewModel.showLoading) {
            if (loaCellViewModel.showLoading) {
                val characterResult = LostArkApi().getCharacterInfo(userInfo.representativeCharacter)
                characterResult.onSuccess { list ->
                    FireStoreHelper.addCharacters(list)
                    FireStoreHelper.addUser(
                        roomId = roomId,
                        name = userInfo.name,
                        representativeCharacter = userInfo.representativeCharacter,
                        characterList = list,
                        failAction = {e ->
                            launch {
                                msg = e.localizedMessage ?: "서버 데이터 저장에 실패했습니다."
                                delay(3_000L)
                                loaCellViewModel.showLoading = false
                            }
                        }
                    ) {
                        loaCellViewModel.showLoading = false
                    }
                }
                characterResult.onError { code, message ->
                    msg = "$message($code)"
                    delay(3_000L)
                    loaCellViewModel.showLoading = false
                }
                characterResult.onException {
                    msg = it.message ?: "exception"
                    delay(3_000L)
                    loaCellViewModel.showLoading = false
                }
            }
        }
        if (loaCellViewModel.showLoading) {
            BackHandler() {

            }

            LoadingView(msg)
        }
    }
}

