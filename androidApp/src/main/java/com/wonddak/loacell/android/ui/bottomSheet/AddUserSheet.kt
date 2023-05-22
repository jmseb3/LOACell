package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.sharedapi.LostArkApi
import com.wonddak.sharedapi.onError
import com.wonddak.sharedapi.onException
import com.wonddak.sharedapi.onSuccess
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddUserSheet(
    modifier: Modifier = Modifier,
    roomId: String,
    onDismissRequest: () -> Unit,
    addAction: () -> Unit
) {

    var characterName by remember {
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
    val actionDone = {
        scope.launch {
            errorMsg = ""
            if (user.isEmpty()) {
                errorMsg = "유저 이름이 비어있습니다."
                return@launch
            }
            if (characterName.isEmpty()) {
                errorMsg = "캐릭터 이름이 비어있습니다."
                return@launch
            }
            showProgress = true

            val characterResult = LostArkApi().getCharacterInfo(characterName)
            characterResult.onSuccess { list ->
                FireStoreHelper.addCharacters(list)
                FireStoreHelper.addUser(
                    roomId = roomId,
                    name = user,
                    representativeCharacter = characterName,
                    characterList = list,
                    failAction = { e ->
                        errorMsg = e.localizedMessage ?: "서버 데이터 저장에 실패했습니다."
                    }
                ) {
                    addAction()
                }

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
    BaseSheet(
        title = "유저 정보 추가",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {
            actionDone()
        },
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
                text = characterName,
                label = "대표 캐릭터",
                placeHolder ="대표 캐릭터 입력" ,
                maxLine = 1,
                maxLength = 12,
                textChange = { characterName = it.replace(" ", "") },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        actionDone()
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
        }
    }
}
