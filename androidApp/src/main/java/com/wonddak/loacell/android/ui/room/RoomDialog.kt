package com.wonddak.loacell.android.ui.room

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.store.CommonRoomHelper
import kotlinx.coroutines.delay

@Composable
fun RoomActionDialog(
    confirm: (status: Int) -> Unit,
    dismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = dismiss,
        title = {
            Text(text = "작업을 선택해 주세요")
        },
        text = {
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
        },
        confirmButton = {

        },
        dismissButton = {

        },
        shape = RoundedCornerShape(24.dp),
    )
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
    confirm: () -> Unit,
    dismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = dismiss,
        title = {
            Text(text = "에러")
        },
        text = {
            Text(text = "해당 방에 입장 권한이 없거나 삭제 된 방입니다.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm()
                },
            ) {
                Text("확인")
            }
        },
        dismissButton = {

        },
        shape = RoundedCornerShape(24.dp),
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@Composable
fun RoomEnterDialog(
    nowEnterRoomList: List<String>,
    success: (roomId: String) -> Unit,
    dismiss: () -> Unit,
) {
    var roomId by remember {
        mutableStateOf("")
    }
    var errorMsg by remember {
        mutableStateOf("")
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
    AlertDialog(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(0.8f),
        onDismissRequest = dismiss,
        title = {
            Text(text = if (password.isEmpty()) "입장하기" else "비밀번호 입력")
        },
        text = {
            val focusManager = LocalFocusManager.current
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
                    LengthLimitTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = enterPassword,
                        label = "방 비밀번호",
                        placeHolder = "방 비밀번호를 입력해주세요.",
                        maxLine = 1,
                        maxLength = 6,
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
                        modifier = Modifier.fillMaxWidth(),
                        text = errorMsg,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (password.isEmpty()) {
                        if (roomId.isEmpty()) {
                            errorMsg = "ID를 입력해주세요."
                        } else {
                            if (nowEnterRoomList.contains(roomId)) {
                                errorMsg = "이미 입장한 방입니다."
                            } else {
                                CommonRoomHelper.checkExist(
                                    roomId,
                                    successAction = { getPassword ->
                                        if (getPassword.isEmpty()) {
                                            success(roomId)
                                        } else {
                                            password = getPassword
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
                            success(roomId)
                        } else {
                            errorMsg = "비밀번호가 맞지 않습니다."
                        }
                    }
                },
                enabled = if (password.isEmpty()) {
                    roomId.length == 20
                } else {
                    password.length == 6
                }
            ) {
                Text(if (password.isEmpty()) "입장" else "확인")
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text("취소")
            }
        },
        shape = RoundedCornerShape(24.dp),
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    )
}