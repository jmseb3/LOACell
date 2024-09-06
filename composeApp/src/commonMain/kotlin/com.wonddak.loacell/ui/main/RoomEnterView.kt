package com.wonddak.loacell.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.ui.common.LengthLimitTextField
import kotlinx.coroutines.delay

@Composable
fun RoomEnterView(
	prevId : String,
	nowEnterRoomList : List<RoomInfo>,
	uid : String,
	initRoom : (RoomInfo) -> Unit,
	onBack : () -> Unit,
) {
	val uniqueIdRoomList = nowEnterRoomList.map { it.uniqueId }.toSet()
	val regex = Regex("[a-zA-Z0-9]+")
	SetBackAction(true) {
		onBack()
	}
	Scaffold(
		topBar = {
			LoaCellTopAppBar(
				"입장하기",
				onBack = onBack
			)
		}
	) { innerPadding ->
		var errorMsg by remember {
			mutableStateOf("")
		}
		LaunchedEffect(errorMsg) {
			if (errorMsg.isNotEmpty()) {
				delay(2_000L)
				errorMsg = ""
			}
		}
		var roomId by remember {
			mutableStateOf(prevId)
		}
		var enterPassword by remember {
			mutableStateOf("")
		}
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(10.dp)
		) {
			LengthLimitTextField(
				modifier = Modifier.fillMaxWidth(),
				text = roomId,
				label = "방 ID",
				placeHolder = "방 ID를 입력해주세요.",
				maxLine = 1,
				maxLength = 20,
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Next
				),
				textChange = {
					if (it.isEmpty() || regex.matches(it)) {
						roomId = it
					}
				}
			)
			LengthLimitTextField(
				modifier = Modifier
					.fillMaxWidth(),
				text = enterPassword,
				label = "방 비밀번호",
				placeHolder = "방 비밀번호를 입력해주세요.",
				maxLine = 1,
				maxLength = 10,
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Done
				),
				textChange = {
					if (it.isEmpty() || regex.matches(it)) {
						enterPassword = it
					}
				},
			)
			OutlinedButton(
				modifier = Modifier.fillMaxWidth(),
				onClick = {
					if (uniqueIdRoomList.contains(roomId)) {
						errorMsg = "이미 입장한 방입니다."
						roomId = ""
						enterPassword = ""
					} else {
						CommonRoomHelper.checkExist(
							roomId,
							successAction = { roomInfo ->
								if (roomInfo.enterPassword.isEmpty() || enterPassword == roomInfo.enterPassword) {
									//init
									CommonRoomHelper.enterRoom(
										roomInfo.uniqueId,
										uid,
										successAction = {
											initRoom(roomInfo)
										},
										failAction = {
											errorMsg = "방 입장에 실패 했습니다."
										}
									)
								} else {
									errorMsg = "방이 존재 하지 않거나 비밀번호가 맞지 않습니다."
								}
							},
							failAction = {
								errorMsg = "방이 존재 하지 않습니다."
							}
						)
					}
				},
				enabled = roomId.length == 20
			) {
				Text("입장하기")
			}
			AnimatedVisibility(errorMsg.isNotEmpty()) {
				Text(
					modifier = Modifier
						.fillMaxWidth(),
					text = errorMsg,
					textAlign = TextAlign.Center
				)
			}
		}
	}
}