package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Sheet

@Composable
fun RaidUserAddSheet(
    modalStatus: ModalStatus,
    userAndCharacterMap: Map<String, List<Character>>,
) {

    var selectedUser: String? by remember { mutableStateOf(null) }
    var selectedCharacter: Character? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
//        selectedUser = userList[0]
    }

    LaunchedEffect(selectedUser) {
//        if (selectedUser != null) {
//            characterList = userAndCharacterMap[selectedUser!!]!!
//            selectedCharacter = characterList[0]
//            stateCharacter.animateScrollToItem(0)
//            return@LaunchedEffect
//        }
    }

    BaseSheet(
        modalStatus,
        title = Sheet.RAID_USER_ADD.title,
        enabledButton = selectedCharacter != null,
        buttonClickAction = {

        }
    ) {
        Column {
            userAndCharacterMap.forEach { (k, v) ->
                Text("key : $k")
                Text("value : ${v.joinToString("//")}")
            }
        }
//        Column(modifier = Modifier.fillMaxWidth()) {
//            Row(modifier = Modifier.fillMaxWidth()) {
//                val scope = rememberCoroutineScope()
//                val modifier = Modifier.weight(1f)
//                if (userList.isNotEmpty()) {
//                    val stateUser = rememberLazyListState(0)
//                    var currentIndexUser by remember { mutableStateOf(0) }
//                    VerticalWheelPicker(
//                        modifier = modifier,
//                        state = stateUser,
//                        count = userList.size,
//                        itemHeight = 44.dp,
//                        visibleItemCount = 3,
//                        onScrollFinish = {
//                            currentIndexUser = it
//                            selectedUser = userList[it]
//                        }
//                    ) { index ->
//                        Box(
//                            modifier = Modifier
//                                .wrapContentHeight()
//                                .noRippleClickable() {
//                                    scope.launch {
//                                        stateUser.animateScrollToItem(
//                                            index
//                                        )
//                                    }
//                                },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = userList[index],
//                                color = if (index == currentIndexUser) Color.Black else Color.Gray,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.width(10.dp))
//                    if (characterList.isNotEmpty()) {
//                        VerticalWheelPicker(
//                            modifier = modifier,
//                            state = stateCharacter,
//                            count = characterList.size,
//                            itemHeight = 44.dp,
//                            visibleItemCount = 3,
//                            onScrollFinish = {
//                                currentIndexCharacter = it
//                                selectedCharacter = characterList[it]
//                            }
//                        ) { index ->
//                            Box(
//                                modifier = Modifier
//                                    .wrapContentHeight()
//                                    .noRippleClickable {
//                                        scope.launch {
//                                            stateCharacter.animateScrollToItem(
//                                                index
//                                            )
//                                        }
//                                    },
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    text = characterList[index].name,
//                                    color = if (index == currentIndexCharacter) Color.Black else Color.Gray,
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//                        }
//                    } else {
//                        Text(
//                            modifier = modifier,
//                            text = "선택 가능한 캐릭터가 없습니다."
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(5.dp))
//            HorizontalDivider()
//            selectedCharacter?.let { character ->
//                Spacer(modifier = Modifier.height(10.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(text = character.className)
//                    Text(text = character.getLevel().toString())
//                }
//            }
//        }
    }
}