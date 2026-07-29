package com.wonddak.loacell.ui.modal.sheet

import CommonUserHelper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.network.lostark.model.CharacterInfo
import com.wonddak.loacell.network.onFailMsg
import com.wonddak.loacell.network.onSuccess
import com.wonddak.loacell.ui.common.LengthLimitTextField
import kotlinx.coroutines.launch
import com.wonddak.loacell.di.LocalLostArkApi

@Composable
fun AddUserSheet(
    modalStatus: ModalStatus,
    modifier: Modifier = Modifier,
    roomInfo: RoomInfo,
) {
    val lostArkApi = LocalLostArkApi.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val textFieldModifier = Modifier.fillMaxWidth()

    var searchCharacterName by remember {
        mutableStateOf("")
    }
    var user by remember {
        mutableStateOf("")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    var showProgress by remember {
        mutableStateOf(false)
    }
    var searchResult by remember {
        mutableStateOf(emptyList<CharacterInfo>())
    }

    LaunchedEffect(modalStatus.status) {
        searchCharacterName = ""
        user = ""
        errorMsg = ""
        showProgress = false
        searchResult = emptyList()
    }

    val searchAction = {
        scope.launch {
            showProgress = true
            lostArkApi.getCharacterInfo(searchCharacterName)
                .onSuccess {
                    showProgress = false
                    searchResult = it
                }
                .onFailMsg {
                    showProgress = false
                    errorMsg = it
                }
        }
    }

    val initAction = {
        if (searchResult.isNotEmpty()) {
            CommonUserHelper.addUserInfo(
                roomInfo.uniqueId,
                user,
                searchCharacterName,
                searchResult,
                { error -> errorMsg = error },
                { modalStatus.hide() }
            )
        }
    }

    val enabledBtn = if (showProgress) {
        false
    } else {
        if (searchResult.isEmpty()) {
            user.isNotEmpty() && searchCharacterName.isNotEmpty()
        } else {
            true
        }
    }

    BaseSheet(
        modalStatus = modalStatus,
        title = Sheet.USER_ADD.title,
        buttonClickAction = {
            if (searchResult.isEmpty()) {
                searchAction()
            } else {
                initAction()
            }
        },
        enabledButton = enabledBtn,
        buttonText = if (searchResult.isEmpty()) "검색" else "추가",
        errorMsg = errorMsg,
        updateErrorMsg = { errorMsg = it }
    ) {
        Column(
            modifier = modifier.padding(10.dp)
        ) {
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = user,
                label = "유저 이름",
                placeHolder = "유저 이름 입력",
                maxLine = 1,
                maxLength = 5,
                textChange = { user = it.replace(" ", "") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
            )
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = searchCharacterName,
                label = "대표 캐릭터",
                placeHolder = "대표 캐릭터 입력",
                maxLine = 1,
                maxLength = 12,
                textChange = {
                    searchCharacterName = it.replace(" ", "")
                    searchResult = emptyList()
                    errorMsg = ""
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        searchAction()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (showProgress) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${user}님의 캐릭터 정보를 불러 옵니다.")
                    CircularProgressIndicator()
                }
            }
            if (searchResult.isNotEmpty()) {
                searchResult.find { it.characterName.lowercase() == searchCharacterName.lowercase() }
                    ?.let { find ->
                        Column() {
                            Text("${find.characterName}(${find.characterClassName}) - ${find.itemAvgLevel}")
                            Text(text = "외 ${searchResult.size - 1}개의 캐릭터를 찾았습니다.")
                        }
                    }
            }
        }
    }
}
