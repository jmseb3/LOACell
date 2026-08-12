/* Hallmark · pre-emit critique: P5 H5 E5 S5 R5 V4
 * component: participant character roster · genre: modern-minimal · theme: existing Material 3 blue
 */
package com.wonddak.loacell.ui.raidRoom.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace
import com.wonddak.loacell.ui.common.DropDownNameView
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.DeleteDialog
import com.wonddak.loacell.ui.modal.dialog.EditCharacterDialog
import io.github.aakira.napier.Napier
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.change_person
import loacell.sharedui.generated.resources.delete
import loacell.sharedui.generated.resources.refresh
import com.wonddak.loacell.viewModel.RaidViewModel

@Composable
fun UserDetailView(
    userInfo: UserInfo?,
    raidViewModel: RaidViewModel,
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
                            Res.drawable.change_person,
                            "대표 캐릭터 변경",
                        ) {
                            changeCharacterStatus.show()
                        },
                        FABInfo.Default(
                            Res.drawable.refresh,
                            "캐릭터 정보 갱신",
                        ) {
                            Napier.d { "갱신: ${userInfo.timeStamp}" }
                            Napier.d { "갱신: ${userInfo.checkTimeOver()}" }
                            if (userInfo.checkTimeOver()) {
                                sync = true
                                raidViewModel.findCharacters(
                                    characterName = userInfo.representativeCharacter,
                                    onSuccess = {
                                        sync = false
                                        raidViewModel.refreshUser(userInfo, it)
                                    },
                                    onFailure = { sync = false },
                                )
                            } else {
                                Napier.d { "갱신 ㄴㄴ" }
                            }
                        },
                        FABInfo.Default(
                            Res.drawable.delete,
                            "사용자 삭제",
                        ) {
                            deleteDialogStatus.show()
                        },

                        )
                )
            }
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(LoaCellSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(LoaCellSpace.sm),
                ) {
                    itemsIndexed(userInfo.characterList) { _, character ->
                        UserInfoCharacter(
                            character,
                            userInfo.representativeCharacter == character.name
                        )
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
            raidViewModel.updateRepresentativeCharacter(userInfo, it)
        }

        DeleteDialog(
            deleteDialogStatus,
            title = Dialog.CHARACTER_DELETE.title,
            confirm = {
                onBack()
                raidViewModel.deleteUser(userInfo)
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
    val contentColor = if (bold) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 112.dp),
        shape = RoundedCornerShape(LoaCellRadius.card),
        colors = CardDefaults.cardColors(
            containerColor = if (bold) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
            contentColor = contentColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(LoaCellSpace.md),
            verticalArrangement = Arrangement.spacedBy(LoaCellSpace.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DropDownNameView(
                    name = character.name,
                    bold = true,
                    textHorizontalAlignment = Alignment.Start,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                    otherContent = {
                        Text(
                            text = "${character.className} · Lv. ${character.level}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (bold) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    },
                )
                if (bold) {
                    Surface(
                        shape = RoundedCornerShape(LoaCellRadius.image),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            text = "대표",
                            modifier = Modifier.padding(
                                horizontal = LoaCellSpace.xs,
                                vertical = LoaCellSpace.xxs,
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
            CharacterMetric(
                label = "서버",
                value = character.server,
            )
        }
    }
}

@Composable
private fun CharacterMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
