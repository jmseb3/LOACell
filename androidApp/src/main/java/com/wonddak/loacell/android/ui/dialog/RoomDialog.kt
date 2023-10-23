package com.wonddak.loacell.android.ui.dialog

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.FBRoomInfo
import kotlinx.coroutines.delay

@Composable
fun RoomActionDialog(
    dismiss: () -> Unit,
    confirm: (status: Int) -> Unit,
) {
    BaseDialog(
        titleText = "작업을 선택해 주세요",
        dismiss = dismiss,
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val sameModifier = Modifier.size(100.dp)
            EnterButton(
                sameModifier,
                SharedRes.images.room_enter.drawableResId,
                "입장하기"
            ) {
                confirm(1)
            }
            EnterButton(
                sameModifier,
                SharedRes.images.room_make.drawableResId,
                "방 만들기",
            ) {
                confirm(2)
            }
        }
    }
}

@Composable
fun EnterButton(
    modifier: Modifier,
    @DrawableRes id: Int,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = id), contentDescription = null)
            Text(text = text)
        }
    }

}

@Composable
fun RoomEnterErrorDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit,
) {
    BaseDialog(
        modifier = Modifier.wrapContentHeight(),
        dismiss = dismiss,
        titleText = "에러",
        confirmButtonText = "확인",
        confirmButtonAction = confirm,
        dialogProperties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Text(text = "해당 방에 입장 권한이 없거나 삭제되었습니다.")
    }
}

@Composable
fun RoomEnterDialog(
    nowEnterRoomList: List<String>,
    dismiss: () -> Unit,
    success: (roomId: String, roomInfo: FBRoomInfo) -> Unit,
) {
    var roomId by remember {
        mutableStateOf("")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    var nowRoomInfo: FBRoomInfo? by remember {
        mutableStateOf(null)
    }
    var password by remember {
        mutableStateOf("")
    }
    var enterPassword by remember {
        mutableStateOf("")
    }
    LaunchedEffect(errorMsg) {
        if (errorMsg.isNotEmpty()) {
            delay(2_000L)
            errorMsg = ""
        }
    }
    val regex = Regex("[a-zA-Z0-9]+")

    BaseDialog(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(0.8f),
        dismiss = dismiss,
        titleText = if (password.isEmpty()) "입장하기" else "비밀번호 입력",
        confirmButtonText = if (password.isEmpty()) "입장" else "확인",
        confirmButtonEnabled = if (password.isEmpty()) roomId.length == 20 else true,
        confirmButtonAction = {
            if (password.isEmpty()) {
                if (roomId.isEmpty()) {
                    errorMsg = "ID를 입력해주세요."
                } else {
                    if (nowEnterRoomList.contains(roomId)) {
                        errorMsg = "이미 입장한 방입니다."
                    } else {
                        CommonRoomHelper.checkExist(
                            roomId,
                            successAction = { roomInfo ->
                                if (roomInfo.enterPassword.isEmpty()) {
                                    success(roomId, roomInfo)
                                } else {
                                    nowRoomInfo = roomInfo
                                    password = roomInfo.enterPassword
                                }
                            },
                            failAction = {
                                errorMsg = "방이 존재 하지 않습니다."
                            }
                        )
                    }
                }
            } else {
                if (password == enterPassword) {
                    success(roomId, nowRoomInfo!!)
                } else {
                    errorMsg = "비밀번호가 맞지 않습니다."
                }
            }
        },
        dismissButtonText = "취소",
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val focusRequester = remember { FocusRequester() }
        Column() {
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
            AnimatedVisibility(visible = password.isNotEmpty()) {
                LaunchedEffect(true) {
                    focusRequester.requestFocus()
                }
                LengthLimitTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    text = enterPassword,
                    label = "방 비밀번호",
                    placeHolder = "방 비밀번호를 입력해주세요.",
                    maxLine = 1,
                    maxLength = 10,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    textChange = {
                        if (it.isEmpty() || regex.matches(it)) {
                            enterPassword = it
                        }
                    },
                    enabled = password.isNotEmpty()
                )
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

@Composable
fun RoomEnterPasswordDialog(
    roomId: String,
    fbRoomInfo: FBRoomInfo,
    success: (roomId: String, roomInfo: FBRoomInfo) -> Unit,
    dismiss: () -> Unit,
) {
    var enterPassword by remember {
        mutableStateOf("")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    LaunchedEffect(errorMsg) {
        if (errorMsg.isNotEmpty()) {
            delay(2_000L)
            errorMsg = ""
        }
    }
    val regex = Regex("[a-zA-Z0-9]+")

    BaseDialog(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(0.8f),
        dismiss = dismiss,
        titleText = "비밀번호 입력",
        confirmButtonText = "확인",
        confirmButtonAction = {
            if (fbRoomInfo.enterPassword == enterPassword) {
                success(roomId, fbRoomInfo)
            } else {
                errorMsg = "비밀번호가 맞지 않습니다."
            }
        },
        dismissButtonText = "취소",
        dialogProperties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val focusRequester = remember { FocusRequester() }
        Column() {
            LengthLimitTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = enterPassword,
                label = "방 비밀번호",
                placeHolder = "방 비밀번호를 입력해주세요.",
                maxLine = 1,
                maxLength = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                textChange = {
                    if (it.isEmpty() || regex.matches(it)) {
                        enterPassword = it
                    }
                },
            )
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

@Composable
fun RoomExitDialog(
    dismiss: () -> Unit,
    success: () -> Unit,
) {
    BaseDialog(
        dismiss = dismiss,
        titleText = "방 나가기",
        confirmButtonText = "나가기",
        confirmButtonAction = success,
        dismissButtonText = "취소"
    ) {
        Text(text = "방에서 나가시겠습니까?")
    }

}