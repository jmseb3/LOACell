package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.ui.common.FabMenuItem
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.util.Config
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
        var expand by remember {
            mutableStateOf(false)
        }
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
                    FabMenuItem(expand, Res.drawable.change_person) {}
                    FabMenuItem(expand, Res.drawable.refresh) {}
                    FabMenuItem(expand, Res.drawable.delete) {}
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                itemsIndexed(userInfo.characterList) { index, character ->
                    UserInfoCharacter(character, userInfo.representativeCharacter == character.name)
                    if (index != userInfo.characterList.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
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