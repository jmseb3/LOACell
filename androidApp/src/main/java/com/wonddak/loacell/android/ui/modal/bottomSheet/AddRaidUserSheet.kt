package com.wonddak.loacell.android.ui.modal.bottomSheet

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.wonddak.loacell.Character
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.ext.getLevel
import com.wonddak.loacell.model.Sheet
import kotlinx.coroutines.launch

@Composable
fun AddRaidUserSheet(
    userAndCharacterMap: Map<String, List<Character>>,
    dialogAction: DialogAction
) {

    var selectedUser: String? by remember { mutableStateOf(null) }
    var selectedCharacter: Character? by remember { mutableStateOf(null) }

    var userList: List<String> by remember { mutableStateOf(emptyList()) }
    var characterList: List<Character> by remember { mutableStateOf(emptyList()) }

    val stateCharacter = rememberLazyListState(0)
    var currentIndexCharacter by remember { mutableIntStateOf(0) }

    LaunchedEffect(true) {
        userList = userAndCharacterMap.keys.toList()
        selectedUser = userList[0]
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            characterList = userAndCharacterMap[selectedUser!!]!!
            selectedCharacter = characterList[0]
            stateCharacter.animateScrollToItem(0)
            return@LaunchedEffect
        }
    }

    BaseSheet(
        title = Sheet.RAID_USER_ADD.title,
        onDismissRequest = {
            dialogAction.hideDialog()
        },
        buttonClickAction = {
            if (selectedCharacter != null) {
                dialogAction.dialogUserAdd(selectedCharacter!!)
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val scope = rememberCoroutineScope()
                val modifier = Modifier.weight(1f)
                if (userList.isNotEmpty()) {
                    val stateUser = rememberLazyListState(0)
                    var currentIndexUser by remember { mutableStateOf(0) }
                    VerticalWheelPicker(
                        modifier = modifier,
                        state = stateUser,
                        count = userList.size,
                        itemHeight = 44.dp,
                        visibleItemCount = 3,
                        onScrollFinish = {
                            currentIndexUser = it
                            selectedUser = userList[it]
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
                                text = userList[index],
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
            HorizontalDivider()
            selectedCharacter?.let { character ->
                Spacer(modifier = Modifier.height(10.dp))
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