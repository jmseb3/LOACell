package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andyliu.compose_wheel_picker.VerticalWheelPicker
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.getLevel
import com.wonddak.loacell.getMinLevel
import kotlinx.coroutines.launch

@Composable
fun AddRaidUserSheet(
    raidInfo: RaidInfo,
    allUserList: List<UserInfo>,
    db: AppDataBase,
    onDismissRequest: () -> Unit,
    successAction: (character: Character) -> Unit
) {

    var selectedUser: UserInfo? by remember { mutableStateOf(null) }

    var characterList: List<Character> by remember { mutableStateOf(emptyList()) }

    var selectedCharacter: Character? by remember { mutableStateOf(null) }

    val stateCharacter = rememberLazyListState(0)
    var currentIndexCharacter by remember { mutableStateOf(0) }

    LaunchedEffect(allUserList) {
        if (allUserList.isNotEmpty()) {
            selectedUser = allUserList[0]
        }
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            db.characterInfoQueriesHelper.getCharacterValueFlow(selectedUser!!).collect {
                characterList = it.filter { it.getLevel() >= raidInfo.getMinLevel()}
                if (characterList.isNotEmpty()) {
                    selectedCharacter = characterList[0]
                    currentIndexCharacter = 0
                    launch {
                        stateCharacter.animateScrollToItem(0)
                    }
                } else {
                    selectedCharacter = null
                }
            }
            return@LaunchedEffect
        }
    }

    BaseSheet(
        title = "캐릭터 정보 추가",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            if (selectedCharacter != null) {
                successAction(selectedCharacter!!)
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val scope = rememberCoroutineScope()
                val modifier = Modifier.weight(1f)
                if (allUserList.isNotEmpty()) {
                    val stateUser = rememberLazyListState(0)
                    var currentIndexUser by remember { mutableStateOf(0) }
                    VerticalWheelPicker(
                        modifier = modifier,
                        state = stateUser,
                        count = allUserList.size,
                        itemHeight = 44.dp,
                        visibleItemCount = 3,
                        onScrollFinish = {
                            currentIndexUser = it
                            selectedUser = allUserList[it]
                        }
                    ) { index ->
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .noRippleClickable() {
                                    scope.launch {
                                        stateUser.animateScrollToItem(
                                            index
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = try {
                                    allUserList[index].name
                                } catch (e: Exception) {
                                    ""
                                },
                                color = if (index == currentIndexUser) Color.Black else Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    if (characterList.isNotEmpty()) {
                        VerticalWheelPicker(
                            modifier = modifier,
                            state = stateCharacter,
                            count = characterList.size,
                            itemHeight = 44.dp,
                            visibleItemCount = 3,
                            onScrollFinish = {
                                currentIndexCharacter = it
                                selectedCharacter = characterList[it]
                            }
                        ) { index ->
                            Box(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .noRippleClickable {
                                        scope.launch {
                                            stateCharacter.animateScrollToItem(
                                                index
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = characterList[index].name,
                                    color = if (index == currentIndexCharacter) Color.Black else Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Text(
                            modifier = modifier,
                            text = "선택 가능한 캐릭터가 없습니다."
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Divider()
            selectedCharacter?.let {character ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = character.className)
                    Text(text = character.getLevel().toString())
                }

            }
        }
    }
}