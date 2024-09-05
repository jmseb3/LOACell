package com.wonddak.loacell.android.ui.modal.dialog

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
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
import com.wonddak.loacell.DialogAction
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.ext.SchemeData
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.ModalConst
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.FBRoomInfo
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
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
            Text(
                text = text,
                maxLines =  1,
                modifier = Modifier
                    .basicMarquee()
            )
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
    dialogAction: DialogAction,
    prevData: SchemeData? = null
) {
    val nowEnterRoomList = dialogAction.getRoomListToUniqueId()

    var roomId by remember {
        mutableStateOf(prevData?.roomId ?: "")
    }
    var errorMsg by remember {
        mutableStateOf("")
    }
    var nowRoomInfo: FBRoomInfo? by remember {
        mutableStateOf(prevData?.fbRoomInfo)
    }
    var password: String by remember {
        mutableStateOf(prevData?.fbRoomInfo?.enterPassword ?: "")
    }
    var enterPassword: String by remember {
        mutableStateOf("")
    }
    LaunchedEffect(errorMsg) {
        if (errorMsg.isNotEmpty()) {
            delay(2_000L)
            errorMsg = ""
        }
    }
    val regex = Regex("[a-zA-Z0-9]+")

    val dismiss = {
        dialogAction.hideDialog()
    }

    val success = { roomInfo: FBRoomInfo ->
        if (prevData == null) {
            dialogAction.dialogRoomEnter(roomId, roomInfo)
        } else {
            dialogAction.dialogRoomEnterByScheme(roomId, roomInfo)
        }
    }
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
                //입장하기
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
                                    dismiss()
                                    success(roomInfo)
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
                // 비밀번호 입력
                if (password == enterPassword) {
                    if (nowEnterRoomList.contains(roomId)) {
                        errorMsg = "이미 입장한 방입니다."
                    } else {
                        dismiss()
                        success(nowRoomInfo!!)
                    }
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
