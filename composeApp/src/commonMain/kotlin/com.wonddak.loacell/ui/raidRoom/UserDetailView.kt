package com.wonddak.loacell.ui.raidRoom

import CommonUserHelper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.network.onFailMsg
import com.wonddak.loacell.network.onSuccess
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.common.FabMenuItem
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.DeleteDialog
import com.wonddak.loacell.ui.modal.dialog.EditCharacterDialog
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.util.Config
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.change_person
import loacell.composeapp.generated.resources.delete
import loacell.composeapp.generated.resources.refresh
import loacell.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun UserDetailView(
    userInfo: UserInfo?,
    onBack: () -> Unit,
) {
    if (userInfo == null) {
        TextButton(onClick = onBack) {
            Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
        }
    } else {
        val lostArkApi: LostArkApi = koinInject()
        val scope = rememberCoroutineScope()
        var expand by remember {
            mutableStateOf(false)
        }
        var sync by remember {
            mutableStateOf(false)
        }
        val deleteDialogStatus = rememberModalStatus()
        val changeCharacterStatus = rememberModalStatus()
        SetBackAction(expand) {
            expand = false
        }
        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    userInfo.name,
                    onBack = onBack
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    FabMenuItem(expand, Res.drawable.change_person) {
                        changeCharacterStatus.show()
                    }
                    FabMenuItem(expand, Res.drawable.refresh) {
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
                    }
                    FabMenuItem(expand, Res.drawable.delete) {
                        deleteDialogStatus.show()
                    }
                    FloatingActionButton(
                        onClick = {
                            expand = !expand
                        },
                        shape = FloatingActionButtonDefaults.largeShape
                    ) {
                        Icon(
                            if (expand) {
                                Icons.Filled.Clear
                            } else {
                                Icons.Filled.Add
                            }, null
                        )
                    }
                }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInfoCharacter(
    character: Character,
    bold: Boolean,
) {
    val webLauncher = rememberWebLauncher()
    val config: Config = koinInject()
    val base by config.defaultUrl.collectAsState("")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        var openMenu by remember {
            mutableStateOf(false)
        }
        Text(
            modifier = Modifier.noRippleClickable { openMenu = true }.basicMarquee(),
            text = character.name,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
        DropdownMenu(expanded = openMenu, onDismissRequest = { openMenu = false }) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("검색")
                    }
                },
                onClick = {
                    webLauncher.launchWeb(base + character.name)
                    openMenu = false
                }
            )
        }
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