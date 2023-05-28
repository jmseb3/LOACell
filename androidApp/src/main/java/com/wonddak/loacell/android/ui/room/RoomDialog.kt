package com.wonddak.loacell.android.ui.room

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.LengthLimitTextField
import com.wonddak.loacell.android.util.FireStoreHelper

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
    success: (roomId:String) -> Unit,
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
    AlertDialog(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = dismiss,
        title = {
            Text(text = "입장하기")
        },
        text = {
            val focusManager = LocalFocusManager.current
            Column() {
                AnimatedVisibility(visible = password.isEmpty()) {
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
                            roomId = it
                        }
                    )
                }
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
                            enterPassword = it
                        },
                        enabled = password.isNotEmpty()
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMsg
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (password.isEmpty()) {
                        if (roomId.isEmpty()) {
                            errorMsg = "ID를 입력해주세요."
                        } else {
                            FireStoreHelper.checkExistRoomInfo(
                                roomId,
                                successAction = {
                                    if (it.isEmpty()) {
                                        success(roomId)
                                    } else {
                                        password = it
                                    }
                                },
                                failAction = {
                                    errorMsg = "방이 존재 하지 않습니다."
                                }
                            )
                        }
                    } else {
                        if (password == enterPassword) {
                            success(roomId)
                        } else {
                            errorMsg = "비밀번호가 맞지 않습니다."
                        }
                    }
                },
            ) {
                Text("입장하기")
            }
        },
        dismissButton = {

        },
        shape = RoundedCornerShape(24.dp),
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    )
}