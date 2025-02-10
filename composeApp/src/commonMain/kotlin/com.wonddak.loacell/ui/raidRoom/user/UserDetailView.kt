package com.wonddak.loacell.ui.raidRoom.user

import CommonUserHelper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.network.onFailMsg
import com.wonddak.loacell.network.onSuccess
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.common.DropDownNameView
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.DeleteDialog
import com.wonddak.loacell.ui.modal.dialog.EditCharacterDialog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.change_person
import loacell.composeapp.generated.resources.delete
import loacell.composeapp.generated.resources.refresh
import org.koin.compose.koinInject

@Composable
fun UserDetailView(
    userInfo: UserInfo?,
    onBack: () -> Unit,
) {
    SetBackAction(true) {
        onBack()
    }
    if (userInfo == null) {
        TextButton(onClick = onBack) {
            Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
        }
    } else {
        val lostArkApi: LostArkApi = koinInject()
        val scope = rememberCoroutineScope()
        val fabStatus = rememberModalStatus()
        var sync by remember {
            mutableStateOf(false)
        }
        val deleteDialogStatus = rememberModalStatus()
        val changeCharacterStatus = rememberModalStatus()
        SetBackAction(fabStatus.status) {
            fabStatus.hide()
        }
        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    userInfo.name,
                    onBack = onBack
                )
            },
            floatingActionButton = {
                OpenableFabMenu(
                    fabStatus,
                    listOf(
                        FABInfo.Default(
                            Res.drawable.change_person
                        ) {
                            changeCharacterStatus.show()
                        },
                        FABInfo.Default(
                            Res.drawable.refresh
                        ) {
                            Napier.d { "갱신: ${userInfo.timeStamp}" }
                            Napier.d { "갱신: ${userInfo.checkTimeOver()}" }
                            if (userInfo.checkTimeOver()) {
                                scope.launch {
                                    sync = true
                                    lostArkApi.getCharacterInfo(userInfo.representativeCharacter)
                                        .onSuccess {
                                            sync = false
                                            CommonUserHelper.updateUserInfo(
                                                userInfo, it
                                            )
                                        }
                                        .onFailMsg {
                                            sync = false
                                        }
                                }
                            } else {
                                Napier.d { "갱신 ㄴㄴ" }
                            }
                        },
                        FABInfo.Default(
                            Res.drawable.delete
                        ) {
                            deleteDialogStatus.show()
                        },

                        )
                )
            }
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(userInfo.characterList) { index, character ->
                        UserInfoCharacter(
                            character,
                            userInfo.representativeCharacter == character.name
                        )
                        if (index != userInfo.characterList.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
                if (sync) {
                    LoadingView("캐릭터 정보를 갱신 중 입니다.")
                }
            }

        }

        EditCharacterDialog(
            changeCharacterStatus,
            userInfo,
        ) {
            CommonUserHelper.updateRepresentativeCharacter(userInfo, it)
        }

        DeleteDialog(
            deleteDialogStatus,
            title = Dialog.CHARACTER_DELETE.title,
            confirm = {
                onBack()
                CommonUserHelper.delete(
                    userInfo.roomId,
                    userInfo.name,
                    failAction = {

                    },
                    successAction = {

                    }
                )
            },
        ) {
            Text(text = "${userInfo.name}님 의 정보를 삭제 하시겠습니까?")
        }
    }
}

@Composable
fun UserInfoCharacter(
    character: Character,
    bold: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {

        DropDownNameView(
            character.name,
            bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val modifier = Modifier.fillMaxWidth(0.5f)
            Text(
                text = character.className,
                modifier = modifier
            )
            Text(
                text = character.getLevel().toString(),
                modifier = modifier
            )
        }
    }
}