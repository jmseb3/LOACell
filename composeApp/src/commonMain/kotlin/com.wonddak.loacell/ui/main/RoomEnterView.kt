package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.wonddak.loacell.ui.common.LengthLimitTextField

@Composable
fun RoomEnterView(
    onBack: () -> Unit,
) {
    val regex = Regex("[a-zA-Z0-9]+")

    Scaffold(
        topBar = {
            LoaCellTopAppBar(
                "입장하기",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        var roomId by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            LengthLimitTextField(
                modifier = Modifier.fillMaxWidth(),
                text = roomId,
                label = "방 ID",
                placeHolder = "방 ID를 입력해주세요.",
                maxLine = 1,
                maxLength = 20,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                textChange = {
                    if (it.isEmpty() || regex.matches(it)) {
                        roomId = it
                    }
                }
            )
        }
    }
}