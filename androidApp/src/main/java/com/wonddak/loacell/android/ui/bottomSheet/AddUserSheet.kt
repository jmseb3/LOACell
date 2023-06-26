package com.wonddak.loacell.android.ui.bottomSheet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.sharedapi.lostark.LostArkApi
import com.wonddak.sharedapi.lostark.model.CharacterInfo
import com.wonddak.sharedapi.onError
import com.wonddak.sharedapi.onException
import com.wonddak.sharedapi.onSuccess
import kotlinx.coroutines.launch

@Composable
fun AddUserSheet(
    modifier: Modifier = Modifier,
    roomId: String,
    onDismissRequest: () -> Unit,
    addAction: () -> Unit
) {

    var searchCharacterName by remember {
        mutableStateOf("")
    }
    var user by remember {
        mutableStateOf("")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val textFieldModifier = Modifier.fillMaxWidth()
    var showProgress by remember {
        mutableStateOf(false)
    }
    var searchResult by remember {
        mutableStateOf(emptyList<CharacterInfo>())
    }
    BackHandler(searchResult.isNotEmpty()) {
        searchResult = emptyList()
    }
    val searchAction = {
        scope.launch {
            errorMsg = ""
            showProgress = true

            val characterResult = LostArkApi().getCharacterInfo(searchCharacterName)
            characterResult.onSuccess { list ->
                searchResult = list
            }
            characterResult.onError { code, message ->
                errorMsg = "$message($code)"
            }
            characterResult.onException {
                errorMsg = it.message ?: "exception"
            }
            showProgress = false
        }
    }
    
    val initAction = {
        if (searchResult.isNotEmpty()) {
            CommonUserHelper.addOrUpdate(
                roomId,
                user,
                searchCharacterName,
                searchResult,
                {error -> errorMsg = error},
                addAction
            )
        }
    }

    val buttonEnabledSearch = searchResult.isEmpty() && user.isNotEmpty() && searchCharacterName.isNotEmpty()
    val buttonEnabledInit = searchResult.isNotEmpty()
    BaseSheet(
        title = "유저 정보 추가",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            if (searchResult.isEmpty()) {
                searchAction()
            } else {
                initAction()
            }
        },
        enabledButton = buttonEnabledSearch || buttonEnabledInit,
        buttonText = if (searchResult.isEmpty()) "검색" else "추가",
        errorMsg = errorMsg,
        updateErrorMsg = {errorMsg = it}
    ) {
        Column(
            modifier = modifier.padding(10.dp)
        ) {
            LengthLimitTextField(
                modifier = textFieldModifier,
                text = user,
                label = "유저 이름",
                placeHolder ="유저 이름 입력" ,
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
                placeHolder ="대표 캐릭터 입력" ,
                maxLine = 1,
                maxLength = 12,
                textChange = { searchCharacterName = it.replace(" ", "") },
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
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "${user}님의 캐릭터 정보를 불러 옵니다.")
                    CircularProgressIndicator()
                }
            }
            AnimatedVisibility(searchResult.isNotEmpty()) {
                val infoCharacter = searchResult.find { it.characterName == searchCharacterName }!!
                Column() {
                    Text("${infoCharacter.characterName}(${infoCharacter.characterClassName}) - ${infoCharacter.itemMaxLevel}")
                    Text(text = "외 ${searchResult.size-1}개의 캐릭터를 찾았습니다.")
                }
            }
        }
    }
}
